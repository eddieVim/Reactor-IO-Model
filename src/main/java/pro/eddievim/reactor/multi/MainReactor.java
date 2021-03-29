package pro.eddievim.reactor.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eddie
 * @create 2021/3/29 11:17
 */
public class MainReactor implements Runnable {

    /**
     * serverSocket使用的selector
     */
    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    // 这里数量可以进行动态计算
    private static final int reactorCounts = 8;

    private static ThreadPoolExecutor pool =
            new ThreadPoolExecutor(3, reactorCounts, 1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(1),
                    new ThreadFactory() {
                        private final AtomicInteger threadNum = new AtomicInteger(1);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r, "multi-threads-handler-" + threadNum.getAndIncrement());
                            if (t.isDaemon()) {
                                t.setDaemon(true);
                            }
                            if (t.getPriority() != Thread.NORM_PRIORITY) {
                                t.setPriority(Thread.NORM_PRIORITY);
                            }
                            return t;
                        }
                    },
                    new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 用于进行后续使用的selector
     */
    private final Selector[] selectors;
    private int next = 0;

    public MainReactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        // Multiplexing IO will wakeup this, class acceptor will run.
        serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor());

        selectors = new Selector[reactorCounts];
        for (int i = 0; i < reactorCounts; i++) {
            selectors[i] = Selector.open();
        }
    }

    /**
     * Alternatively, use explicit SPI provider:
     * SelectorProvider p = SelectorProvider.provider();
     * selector = p.openSelector();
     * serverSocket = p.openServerSocketChannel();
     */
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                // Multiplexing IO events set.
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey o : selected) {
                    dispatch(o);
                }
                selected.clear();
            }
        } catch (IOException ignored){

        }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) k.attachment();
        if (r != null) {
            r.run();
        }
    }

    private class Acceptor implements Runnable {
        @Override
        public synchronized void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null) {
                    SubReactor subReactor = new SubReactor(c, selectors[next]);
                    pool.execute(subReactor);
                }
                if (++next == selectors.length) {
                    next = 0;
                }
            } catch (IOException ignored) {

            }
        }
    }
}
