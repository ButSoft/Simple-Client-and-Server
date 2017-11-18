package com.test;

import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        Integer port = 2323;
        if (args.length > 0)
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                port = 2323;
            }
        Server server = new Server(port);
        new Thread(server).start();
    }

}
