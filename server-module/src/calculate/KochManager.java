package calculate;

import main.*;
import server.ClientRunnable;
import server.ServerRunnable;
import server.packets.out.FractalInfoPacket;
import server.packets.out.EdgePacket;
import server.packets.out.FractalDonePacket;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.SocketException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cas Eliens
 */
public class KochManager implements Serializable {

    private int level;
    private KochWorker manager;
    private Thread workerThread;
    private boolean running = false;

    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;

    public KochManager(ServerRunnable server, int level) {
        this.level = level;

        resetZoom();
    }

    public void calculate(EdgeRequestMode mode, ClientRunnable client, boolean allowMode) {
        // Check cache
        String path = "/mnt/tempdisk/usercache" + client.getID() + ".rand";
        File file = new File(path);
        if (file.exists()) {
            FileLock lock = null;

            try {
                RandomAccessFile memoryMappedFile = new RandomAccessFile(path, "rw");

                //Mapping a file into memory
                FileChannel fc = memoryMappedFile.getChannel();

                // Get level of cache file
                lock = fc.lock(0, 4, true);

                MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, 4);
                int level = out.getInt();
                lock.release();

                // Cache file is for the same level as the one we're supposed to calculate
                if (level == this.level) {
                    // Read cache file
                    readCacheFile(client, allowMode);
                    return;
                }
            } catch (IOException ex) {
                // Ignore cache file
            } finally {
                if (lock != null && lock.isValid()) {
                    try {
                        lock.release();
                    } catch (IOException ex) {
                        Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        running = true;
        workerThread = new Thread(() -> {
            manager = new KochWorker(client, mode, level, this, allowMode);

            // Wait until all edges are calculated
            while (running) {
                if (manager.isDone()) {
                    break;
                }
            }

            running = false;
        });

        workerThread.start();
    }

    private void readCacheFile(ClientRunnable client, boolean allowMode) {
        System.out.println("[START]: KochManager.readCacheFile");

        FileLock lock = null;

        try {
            String path = "/mnt/tempdisk/usercache" + client.getID() + ".rand";

            RandomAccessFile memoryMappedFile = new RandomAccessFile(path, "rw");

            //Mapping a file into memory
            FileChannel fc = memoryMappedFile.getChannel();

            MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            lock = fc.lock(0, 8, true);
            level = out.getInt();
            int edgeCount = out.getInt();
            lock.release();

            // Send info to client
            try {
                FractalInfoPacket infoPack = new FractalInfoPacket(level, edgeCount);
                infoPack.sendData(client.getOutputStream());
            } catch (SocketException ex) {
                System.out.println("Failed to send info packet to client");
            }

            out.position(8);

            for (int i = 0; i < edgeCount; i++) {
                // Lock next 56 bytes (= one edge)
                lock = fc.lock(8 + i * 56, 8 + i * 56 + 56, true);

                Vector2 side1 = new Vector2(out.getDouble(), out.getDouble());
                Vector2 side2 = new Vector2(out.getDouble(), out.getDouble());
                Vector3 color = new Vector3(out.getDouble(), out.getDouble(), out.getDouble());

                // Send packet to client
                EdgePacket edgePack = new EdgePacket(level, this.edgeAfterZoomAndDrag(new Edge(side1, side2, color)), allowMode);
                edgePack.sendData(client.getOutputStream());

                lock.release();
            }

            // Notify client that the calculating is done
            FractalDonePacket donePack = new FractalDonePacket(level, allowMode);
            donePack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (IOException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.out.println("[END]: KochManager.readCacheFile");
    }

    public void stop() {
        running = false;

        if (workerThread != null) {
            workerThread.interrupt();
        }

        if (manager != null) {
            manager.stop();
        }
    }

    public int getLevel() {
        return level;
    }

    public boolean isRunning() {
        return this.running;
    }

    // GUI STUFF
    private void resetZoom() {
        zoom = 500;
        zoomTranslateX = 0;
        zoomTranslateY = 0;
    }

    public void zoom(ZoomType type, Vector2 position) {
        if (type == ZoomType.RESET) {
            resetZoom();
            return;
        }

        if (Math.abs(position.getX() - startPressedX) < 1.0 && Math.abs(position.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (position.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (position.getY() - zoomTranslateY) / zoom;
            if (type == ZoomType.INCREASE) {
                zoom *= 2.0;
            } else if (type == ZoomType.DECREASE) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (position.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (position.getY() - originalPointClickedY * zoom);
        }
    }

    public void press(Vector2 position) {
        startPressedX = position.getX();
        startPressedY = position.getY();
        lastDragX = position.getX();
        lastDragY = position.getY();
    }

    public void drag(Vector2 position) {
        zoomTranslateX = zoomTranslateX + position.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + position.getY() - lastDragY;
        lastDragX = position.getX();
        lastDragY = position.getY();
    }

    public Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.getX1() * zoom + zoomTranslateX,
                e.getY1() * zoom + zoomTranslateY,
                e.getX2() * zoom + zoomTranslateX,
                e.getY2() * zoom + zoomTranslateY,
                e.getColor());
    }
}
