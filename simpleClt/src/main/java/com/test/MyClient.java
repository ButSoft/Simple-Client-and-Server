package com.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class MyClient {

    public static void main(String[] args) {
        Integer port = 2323;
        if (args.length > 0)
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                port = 2323;
            }

        Client client = new Client("localhost", port);
        for (int i = 0; i < 30; i++) {
            new Thread(new Caller(client)).start();
        }
    }

    private static class Caller implements Runnable {
        private Logger logger = LogManager.getLogger(Caller.class);
        private Client client;
        Random random = new Random();

        public Caller(Client client) {
            this.client = client;
        }

        public void run() {
            while (true) {

                logger.info("Result = " + client.remoteCall("service3", "", new Object[]{new Integer(random.nextInt(10 + 1)), new Integer(2), new Integer(random.nextInt(20 + 1))}));
                logger.info("Result = " + client.remoteCall("service2", "", new Object[]{new Integer(random.nextInt(10 + 1)), new Integer(2), new Integer(random.nextInt(20 + 1))}));
                logger.info("Result = " + client.remoteCall("service2", "sum", null));
                logger.info("Result = " + client.remoteCall("service2", "sum", new Object[]{new Integer(random.nextInt(10 + 1)), new Integer(2)}));

                client.remoteCall("service1", "sleep", new Object[]{new Long(random.nextInt(1000 + 1))});
                logger.info("Current Date is:" + client.remoteCall("service1", "getCurrentDate", new Object[]{}));
                logger.info("Result = " + client.remoteCall("service2", "sum", new Object[]{new Integer(random.nextInt(10 + 1)), new Integer(2), new Integer(random.nextInt(20 + 1))}));

            }
        }
    }

}
