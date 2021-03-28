package pro.eddievim.nio;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author eddie
 * @create 2021/2/25 22:25
 */
public class Sender {
    public static void main(String[] args) throws IOException, InterruptedException {
        int i = 0;
        while (true) {
            Socket socket = new Socket("127.0.0.1", 10101);
            ObjectOutputStream out1 = new ObjectOutputStream(socket.getOutputStream());
            out1.writeObject("hello, world!" + i);
            out1.flush();
            i++;
//            out1.writeObject("hello, world!" + i);
//            out1.flush();
//            i++;
//            out1.close();
            socket.close();
//            Socket socket1 = new Socket("127.0.0.1", 10101);
//            ObjectOutputStream out2 = new ObjectOutputStream(socket1.getOutputStream());
//            out2.writeObject("hello, world!");
//            out2.flush();
        }

    }
}
