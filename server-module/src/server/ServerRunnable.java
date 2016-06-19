package server;

import main.Const;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Edwin
 */

public class ServerRunnable implements Runnable {

    private ServerSocket socket;
    private boolean running = true;
    private int client_id = 0;
    private ExecutorService threadPool;

    ServerRunnable() {
        System.out.println("[START]: ServerRunnable.ServerRunnable");
        try {
            threadPool = Executors.newCachedThreadPool();
            socket = new ServerSocket(Const.PORT);
        } catch (IOException ex) {
            running = false;
            System.out.println("Unable to start server");
        }
        System.out.println("[END]: ServerRunnable.ServerRunnable");
    }

    @Override
    public void run() {
        System.out.println("[START]: ServerRunnable.run");

        try {
            while (isRunning()) {
                Socket clientSocket = socket.accept();

                ClientRunnable client = new ClientRunnable(client_id, this, clientSocket);
                Thread clientThread = new Thread(client);
                threadPool.execute(clientThread);

                System.out.format("Thread with ID %s has just connected.\r\n", client_id);

                addClientId();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Synchronized because when two clients do a request at the same time,
     * there will be a race condition.
     */
    private synchronized void addClientId() {
        client_id++;
    }

    /**
     * Same story, race condition is possible when polling for running while
     * another thread has just changed the running variable.
     *
     * @return bool indicating the value of running
     */
    synchronized boolean isRunning() {
        return this.running;
    }
}
