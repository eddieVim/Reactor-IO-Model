package pro.eddievim.reactor.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author eddie
 * 收发信息
 */
public class Handler implements Runnable {
    private static final int MAXIN = 10000;
    private static final int MAXOUT = 10000;

    private final SocketChannel socket;
    private final SelectionKey sk;
    private ByteBuffer input = ByteBuffer.allocate(MAXIN);
    private ByteBuffer output = ByteBuffer.allocate(MAXOUT);
    private static final int READING = 0, SENDING = 1;
    private int state = READING;

    public Handler(SocketChannel socket, Selector sel) throws IOException {
        this.socket = socket;
        socket.configureBlocking(false);
        sk = socket.register(sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }


    private boolean inputIsComplete() {
        return true;
    }
    private boolean outputIsComplete() {
        return true;
    }
    private void process() {
        // input buffer flip to read state
        input.flip();
        System.out.println("------process------");
        byte[] bytes = new byte[input.limit()];
        input.get(bytes);
        String str = new String(bytes);
        System.out.println(str);
        output.put(("received---" + str).getBytes());
        // output buffer flip to write state
        output.flip();
    }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ignored) {
        }
    }

    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            sk.attach(new Sender());
            sk.interestOps(SelectionKey.OP_WRITE);
            sk.selector().wakeup();
        }
    }

    private class Sender implements Runnable {
        @Override
        public void run(){
            try {
                socket.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputIsComplete()) {
                sk.cancel();
            }
        }
    }

    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete()) {
            sk.cancel();
        }
    }
}
