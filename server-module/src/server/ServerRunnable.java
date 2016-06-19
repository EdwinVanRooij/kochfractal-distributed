package server;

import main.Const;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edwin
 */

public class ServerRunnable implements Runnable {

    private ServerSocket socket;
    private List<ClientRunnable> clients;
    private boolean running = true;

    ServerRunnable() {
        try {
            socket = new ServerSocket(Const.PORT);
            clients = new ArrayList<>();
        } catch (IOException ex) {
            running = false;
            System.out.println("Unable to start server");
        }
    }

    @Override
    public void run() {
        System.out.println("[START]: ServerRunnable.run");

        try {
            while (isRunning()) {
                Socket clSock = socket.accept();

                System.out.println("Client connected");

                this.clients.add(new ClientRunnable(this, clSock));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    synchronized boolean isRunning() {
        return this.running;
    }

    void removeClient(ClientRunnable client) {
        this.clients.remove(client);
    }
}
