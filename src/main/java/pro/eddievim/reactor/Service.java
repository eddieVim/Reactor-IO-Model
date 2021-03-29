package pro.eddievim.reactor;


import pro.eddievim.reactor.multi.MainReactor;

import java.io.IOException;

/**
 * @author eddie
 * @create 2021/3/27 15:49
 */
public class Service {
    public static void main(String[] args) throws IOException {
        // test1();
        // test2();
         test3();
    }

    private static void test1() throws IOException {
        new pro.eddievim.reactor.single.Reactor(11111).run();
    }

    private static void test2() throws IOException {
        new pro.eddievim.reactor.pool.Reactor(11111).run();
    }

    private static void test3() throws IOException {
        new MainReactor(11111).run();
    }
}
