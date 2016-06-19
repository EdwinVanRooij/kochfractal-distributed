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
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Edwin
 */

public class ClientRunnable implements Runnable {

    private int id;
    private ServerRunnable server;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private boolean alive = false;
    private KochManager manager = null;

    public int getID() {
        return this.id;
    }

    public DataOutputStream getOutputStream() {
        return this.out;
    }

    ClientRunnable(int id, ServerRunnable server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            this.id = id;
            this.server = server;
            this.socket = socket;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        if (!alive) {
            return;
        }

        alive = false;

        System.out.println("Client disconnected");

        if (manager != null) {
            manager.stop();
        }

        Thread.currentThread().interrupt();

        socket.close();

        // Remove cache file
        String path = String.format("/mnt/tempdisk/usercache%s.rand", String.valueOf(this.id));
        Files.deleteIfExists(Paths.get(path));
    }

    @Override
    public void run() {
        System.out.println("[START]: ClientRunnable.run");
        alive = true;

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

                        System.out.println("Started calculating edges (Level: " + startCalc.getLevel() + ")");
                        manager.calculate(startCalc.getMode(), this, true);
                        break;
                    case ZOOM:
                        if (manager == null || manager.isRunning()) {
                            break;
                        }

                        PacketIn04Zoom zoom = (PacketIn04Zoom) pack;

                        System.out.println("Started zoom (Level: " + manager.getLevel() + ")");
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
                    ex1.printStackTrace();
                }
                ex.printStackTrace();
            }
        }
    }
}
