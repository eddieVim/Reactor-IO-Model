package pro.eddievim.reactor.single;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author eddie
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {

        byte[] input = new byte[1024];
        for (int i = 0; i < 100; i++) {
            Socket socket = new Socket("127.0.0.1", 11111);

            // send
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(("hello, world!" + i).getBytes());
            outputStream.flush();

            // receive
            InputStream inputStream = socket.getInputStream();
            int read = inputStream.read(input);
            if (read != -1) {
                System.out.println(new String(input, 0, read));
            }

            socket.close();
        }

    }
}
