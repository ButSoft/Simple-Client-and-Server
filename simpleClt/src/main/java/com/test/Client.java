package com.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


class Client {
    private Logger logger = LogManager.getLogger(Client.class);
    private Socket socket;
    private static int counter = 0;

    String host;
    Integer port;
    ObjectOutputStream outStream;
    ObjectInputStream inputStream;

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
        init();
        logger.info("Client init host= " + host + " port" + port);
    }

    private void init() {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    public synchronized Object remoteCall(String serviceName, String methodName, Object[] params) {
        Object result = null;
        try {
            if (socket.isClosed()) {
                init();
            }
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            RecClient recClient = new RecClient(counter++, serviceName, methodName, params);
            outStream.writeObject(recClient);
            outStream.flush();

            logger.info("Client send: counter- " + counter + " : " + recClient.toString());

            Object obj = inputStream.readObject();
            RecServer recServer;
            if (obj instanceof RecServer) {
                recServer = (RecServer) obj;
                result = recServer.result;
                logger.info("Client response: counter- " + recServer.toString());
            }

            inputStream.close();
            outStream.close();
        } catch (SocketException ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }


}