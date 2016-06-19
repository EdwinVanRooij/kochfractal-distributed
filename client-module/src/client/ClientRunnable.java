package client;

import calculate.KochManager;
import client.packets.PacketIn;
import client.packets.in.FractalInfoPacket;
import client.packets.in.EdgePacket;
import client.packets.in.FractalDonePacket;
import main.Const;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Edwin
 */

public class ClientRunnable implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private boolean running = false, calculating = false;
    private KochManager man;

    public ClientRunnable(KochManager kochManager) {
        this.man = kochManager;

        Thread th = new Thread(this);
        th.start();
    }

    @Override
    public void run() {
        try {
            //socket = new Socket("192.168.117.1", 2585);
            socket = new Socket(Const.ADDRESS, Const.PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            log("Unable to connect to server");
            return;
        }

        running = true;

        log("Started listening");
        try {
            while (running) {
                try {
                    String line = in.readLine();

                    if (line == null) {
                        this.close();
                        return;
                    }

                    // Handle packets
                    PacketIn pack = PacketIn.parse(line);

                    // Ignore if invalid packet
                    if (pack == null) {
                        continue;
                    }

                    switch (pack.getType()) {
                        case FRACTALINFO:
                            log("Received fractal info packet");
                            FractalInfoPacket fracInfo = (FractalInfoPacket) pack;
                            man.setLevel(fracInfo.getLevel(), false);
                            man.setEdgeCount(fracInfo.getEdgeCount());

                            calculating = true;
                            break;
                        case EDGE_SINGLE:
                            if (!calculating) {
                                log("Received invalid edge packet");
                                break;
                            }

                            // Add edge to kochmanager
                            EdgePacket edgeSingle = (EdgePacket) pack;
                            if (edgeSingle.getLevel() == man.getLevel()) {
                                man.addEdge(this, edgeSingle.getEdge(), edgeSingle.doAllowMode());
                            }
                            break;
                        case FRACTALDONE:
                            FractalDonePacket fractalDone = (FractalDonePacket) pack;

                            if (fractalDone.getLevel() == man.getLevel() && calculating) {
                                log("Fractal done");

                                calculating = false;
                                man.doneReading(fractalDone.doAllowMode());
                            }
                            break;
                    }
                    ///////////
                } catch (IllegalArgumentException ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            try {
                this.close();
            } catch (IOException ex1) {
                //
            }
        }
    }

    public void sendMessageRaw(String message) {
        if (!running) {
            return;
        }

        try {
            out.writeBytes(message + "\n");
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void close() throws IOException {
        if (!running) {
            return;
        }

        log("Lost connection");

        running = false;
        socket.close();
    }

    public synchronized boolean isCalculating() {
        return this.calculating;
    }

    private void log(String message) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();

        System.out.println("(" + elements[1].getClassName() + "." + elements[1].getMethodName() + ":" + elements[1].getLineNumber() + "): " + message);
    }
}
