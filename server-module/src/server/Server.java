package server;

/**
 * @author Edwin
 */

class Server {

    public static void main(String[] args) {
        new Thread(new ServerRunnable()).start();
    }
}
