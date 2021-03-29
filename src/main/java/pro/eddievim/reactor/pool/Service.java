package pro.eddievim.reactor.pool;

import java.io.IOException;

/**
 * @author eddie
 * @create 2021/3/28 15:50
 */
public class Service {
    public static void main(String[] args) throws IOException {
        new Reactor(11111).run();
    }
}
