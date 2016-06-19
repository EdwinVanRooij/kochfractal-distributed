package calculate;

import main.Edge;
import main.EdgeRequestMode;
import server.ClientRunnable;
import server.packets.out.PacketOut01FractalInfo;
import server.packets.out.PacketOut02EdgeSingle;
import server.packets.out.PacketOut03FractalDone;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 *
 * @author Cas Eliens
 */
class KochWorker implements Observer, Runnable {

    private List<Edge> edges;
    private KochFractal frac;
    private Thread thread;
    private boolean done = false, running = true, allowMode = true;
    private EdgeRequestMode mode;
    private ClientRunnable client;
    private KochManager manager;

    private int edgesWritten = 0;

    public KochWorker(ClientRunnable client, EdgeRequestMode mode, int level, KochManager manager, boolean allowMode) {
        this.client = client;
        this.mode = mode;
        this.manager = manager;
        this.edges = new ArrayList();
        this.allowMode = allowMode;

        this.frac = new KochFractal();
        frac.setLevel(level);
        frac.addObserver(this);

        PacketOut01FractalInfo infoPack = new PacketOut01FractalInfo(level, frac.getNrOfEdges());
        try {
            infoPack.sendData(client.getOutputStream());
        } catch (IOException ex) {
            return;
        }

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);

        // Send calculated edge
        if (mode == EdgeRequestMode.EachEdge && running) {
            PacketOut02EdgeSingle edgePack = new PacketOut02EdgeSingle(frac.getLevel(), manager.edgeAfterZoomAndDrag((Edge) arg), allowMode);
            try {
                edgePack.sendData(client.getOutputStream());
                edgesWritten++;
            } catch (IOException ex) {
                stop();
            }
        }
    }

    @Override
    public void run() {
        FileLock lock = null;
        try {
            // Start writing cache file
            String path = "cache" + client.getID() + ".rand";

            RandomAccessFile memoryMappedFile = new RandomAccessFile(path, "rw");

            //Mapping a file into memory
            FileChannel fc = memoryMappedFile.getChannel();

            // File size. 8 for level and edgecount + nr of edges * 56 (one edge is 56 bytes)
            long filesize = 8 + frac.getNrOfEdges() * 56;

            MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, 0, filesize);

            lock = fc.lock(0, 8, false);
            out.putInt(frac.getLevel());
            out.putInt(frac.getNrOfEdges());
            lock.release();

            frac.generateLeftEdge();
            frac.generateBottomEdge();
            frac.generateRightEdge();

            done = true;

            int position = 8;
            for (Edge e : edges) {
                lock = fc.lock(position, position + 56, false);
                // Write edge to memorymap file
                out.putDouble(e.getSide1().getX());
                out.putDouble(e.getSide1().getY());

                out.putDouble(e.getSide2().getX());
                out.putDouble(e.getSide2().getY());

                out.putDouble(e.getRGB().getX());
                out.putDouble(e.getRGB().getY());
                out.putDouble(e.getRGB().getZ());
                lock.release();

                position += 56;

                // Send all edges
                if (mode == EdgeRequestMode.Single && running) {
                    PacketOut02EdgeSingle edgePack = new PacketOut02EdgeSingle(frac.getLevel(), manager.edgeAfterZoomAndDrag(e), allowMode);
                    try {
                        edgePack.sendData(client.getOutputStream());
                        edgesWritten++;
                    } catch (IOException ex) {
                        stop();
                        return;
                    }

                    if (edgesWritten % 100 == 0) {
                        // Give java the time to send the data properly
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }

            if (running && edgesWritten == edges.size()) {
                // Notify client that edge calculation is done
                System.out.println("Done calculating level " + frac.getLevel());

                PacketOut03FractalDone donePack = new PacketOut03FractalDone(frac.getLevel(), allowMode);
                try {
                    donePack.sendData(client.getOutputStream());
                } catch (IOException ex) {
                    stop();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            stop();
        } finally {
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public synchronized void stop() {
        running = false;

        frac.cancel();

        if (thread != null) {
            thread.interrupt();
        }
    }

    public synchronized List<Edge> getEdges() {
        if (!this.done) {
            return new ArrayList();
        }

        return new ArrayList(this.edges);
    }

    public synchronized boolean isDone() {
        return this.done;
    }
}
