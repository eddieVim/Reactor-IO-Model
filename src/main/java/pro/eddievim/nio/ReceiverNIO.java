package pro.eddievim.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author eddie
 * @create 2021/2/25 22:25
 */
public class ReceiverNIO {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9999));

        while (true) {
            SocketChannel socketChannel = serverSocket.accept();
            new Work(socketChannel).start();
        }
    }

    public static class Work extends Thread {

        SocketChannel socketChannel;

        public Work(SocketChannel socketChannel) throws IOException {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
        }
    }
}
