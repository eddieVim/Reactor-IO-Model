package pro.eddievim.nio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author eddie
 * @create 2021/2/25 22:25
 */
public class ReceiverBIO {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(10101);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(socket);
            new Work(socket).start();
        }
    }

    public static class Work extends Thread {

        ObjectInputStream in;

        public Work(Socket socket) throws IOException {
            in = new ObjectInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            try {
                Object obj;
                while ((obj = in.readObject()) != null) {
                    System.out.println(Thread.currentThread());
                    System.out.println(obj);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
