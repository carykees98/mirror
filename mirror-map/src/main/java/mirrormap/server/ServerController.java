package mirrormap.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class ServerController extends Thread {
    private final ServerSocket socket;

    private final ExecutorService threadPool;

    public ServerController(int port) throws IOException {
        threadPool = Executors.newCachedThreadPool();
        socket = new ServerSocket(port);
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @Override
    public void run() {
        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept()));
            } catch(IOException e) {
                if(socket.isClosed()) { System.err.println("Socket closed."); }
                e.printStackTrace();
                return;
            } catch(RejectedExecutionException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
