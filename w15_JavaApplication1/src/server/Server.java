package server;

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
public class Server implements Runnable {

    private ServerSocket socket;
    private List<Client> clients;
    private boolean running = true;
    private ExecutorService threadPool;

    public Server() {
        try {
            socket = new ServerSocket(2585);
            clients = new ArrayList();
            threadPool = Executors.newCachedThreadPool();

            // No need to save thread in variable, as it can be called with Thread.currentThread() in this class
            Thread th = new Thread(this);
            th.start();

        } catch (IOException ex) {
            running = false;
            log("Unable to start server");
        }
    }

    @Override
    public void run() {
        log("Started listening");

        try {
            while (isRunning()) {
                Socket clSock = socket.accept();

                log("Client connected");

                this.clients.add(new Client(this, clSock));
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

        for (Client client : clients) {
            client.close();
        }

        socket.close();
    }

    public void log(String message) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();

        System.out.println("(" + elements[1].getClassName() + "." + elements[1].getMethodName() + ":" + elements[1].getLineNumber() + "): " + message);
    }
    
    public void errorlog(String message) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();

        System.err.println("(" + elements[1].getClassName() + "." + elements[1].getMethodName() + ":" + elements[1].getLineNumber() + "): " + message);
    }

    public void submitThread(Runnable runnable) {
        this.threadPool.submit(runnable);
    }

    void removeClient(Client client) {
        this.clients.remove(client);
    }
}
