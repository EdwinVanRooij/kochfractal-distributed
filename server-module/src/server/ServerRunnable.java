package server;

import main.Const;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Cas Eliens
 */
public class ServerRunnable implements Runnable {

    private ServerSocket socket;
    private List<ClientRunnable> clients;
    private boolean running = true;
    private ExecutorService threadPool;

    public ServerRunnable() {
        try {
            socket = new ServerSocket(Const.PORT);
            clients = new ArrayList<>();
            threadPool = Executors.newCachedThreadPool();

            // No need to save thread in variable, as it can be called with Thread.currentThread() in this class
            Thread th = new Thread(this);
            th.start();

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

    public synchronized boolean isRunning() {
        return this.running;
    }

    public void close() throws IOException {
        if (!running) {
            return;
        }

        running = false;

        threadPool.shutdownNow();

        for (ClientRunnable client : clients) {
            client.close();
        }

        socket.close();
    }

    public void submitThread(Runnable runnable) {
        this.threadPool.submit(runnable);
    }

    void removeClient(ClientRunnable client) {
        this.clients.remove(client);
    }
}
