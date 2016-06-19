package server;

import calculate.KochManager;
import main.EdgeRequestMode;
import server.packets.PacketIn;
import server.packets.in.PacketIn00RequestStartCalc;
import server.packets.in.PacketIn04Zoom;
import server.packets.in.PacketIn05Press;
import server.packets.in.PacketIn06Drag;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Cas Eliens
 */
public class ClientRunnable implements Runnable {

    private static int nextID = 0;

    private int id;
    private ServerRunnable server;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private boolean alive = false;
    private KochManager manager = null;

    public ClientRunnable(ServerRunnable server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());

            this.id = nextID;
            nextID++;

            Thread th = new Thread(this);
            th.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() throws IOException {
        if (!alive) {
            return;
        }

        alive = false;

        server.removeClient(this);
        server.log("Client disconnected");

        if (manager != null) {
            manager.stop();
        }

        Thread.currentThread().interrupt();

        socket.close();

        // Remove cache file
        String path = "/mnt/tempdisk/usercache" + this.id + ".rand";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void run() {
        alive = true;
        server.log("Client started listening");

        while (server.isRunning() && alive) {
            try {
                String line = in.readLine();

                if (line == null) {
                    this.close();
                    return;
                }

                // Handle packets
                PacketIn pack = PacketIn.parse(this, line);

                // Ignore if invalid packet
                if (pack == null) {
                    continue;
                }

                switch (pack.getType()) {
                    case REQUEST_START_CALC:
                        if (manager != null) {
                            manager.stop();
                        }

                        PacketIn00RequestStartCalc startCalc = (PacketIn00RequestStartCalc) pack;
                        manager = new KochManager(server, startCalc.getLevel());

                        server.log("Started calculating edges (Level: " + startCalc.getLevel() + ")");
                        manager.calculate(startCalc.getMode(), this, true);
                        break;
                    case ZOOM:
                        if (manager == null || manager.isRunning()) {
                            break;
                        }

                        PacketIn04Zoom zoom = (PacketIn04Zoom) pack;

                        server.log("Started zoom (Level: " + manager.getLevel() + ")");
                        manager.zoom(zoom.getZoomType(), zoom.getPosition());

                        manager.calculate(EdgeRequestMode.Single, this, false);
                        break;
                    case PRESS:
                        if (manager == null || manager.isRunning()) {
                            break;
                        }

                        PacketIn05Press press = (PacketIn05Press) pack;
                        manager.press(press.getPosition());
                        break;
                    case DRAG:
                        if (manager == null || manager.isRunning()) {
                            break;
                        }

                        PacketIn06Drag drag = (PacketIn06Drag) pack;
                        manager.drag(drag.getPosition());

                        manager.calculate(EdgeRequestMode.Single, this, false);
                        break;
                }

            } catch (IOException ex) {
                try {
                    this.close();
                    return;
                } catch (IOException ex1) {
                    //
                }
            }
        }
    }

    public ServerRunnable getServer() {
        return this.server;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public int getID() {
        return this.id;
    }

    public DataOutputStream getOutputStream() {
        return this.out;
    }

    public void sendMessageRaw(String message) {
        if (alive && out != null) {
            try {
                out.writeBytes(message);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(ClientRunnable.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }
        }
    }
}
