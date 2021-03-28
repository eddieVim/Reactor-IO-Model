package pro.eddievim.reactor.single;

import java.io.IOException;

/**
 * @author eddie
 * @create 2021/3/27 15:49
 */
public class Service {
    public static void main(String[] args) throws IOException {
        new Reactor(11111).run();
    }
}
