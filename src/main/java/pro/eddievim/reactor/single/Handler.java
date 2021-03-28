package pro.eddievim.reactor.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author eddie
 */
public class Handler implements Runnable {
    private static final int MAXIN = 10240;
    private static final int MAXOUT = 10240;

    private final SocketChannel socket;
    private final SelectionKey sk;
    private ByteBuffer input = ByteBuffer.allocateDirect(MAXIN);
    private ByteBuffer output = ByteBuffer.allocateDirect(MAXOUT);

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
            read();
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

    /**
     * Focus on sending the msg.
     */
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
}
