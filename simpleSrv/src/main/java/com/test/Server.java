package com.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private Logger logger = LogManager.getLogger(Server.class);
    private int serverPort;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public Server(int port) {
        this.serverPort = port;
    }

    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    logger.info("Server Stopped.");
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            this.threadPool.execute(new ReaderRunnable(clientSocket, logger));
        }
        this.threadPool.shutdown();
        logger.info("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port ", e);
        }
    }

    public class ReaderRunnable implements Runnable {
        private Socket clientSocket = null;
        private Logger logger;

        private ReaderRunnable(Socket clientSocket, Logger logger) {
            super();
            this.clientSocket = clientSocket;
            this.logger = logger;
        }

        public void run() {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
                Object obj = inputStream.readObject();
                if (obj instanceof RecClient) {
                    RecClient recClient = (RecClient) obj;
                    Object service = ServiceManager.getInstance().getService(recClient.serviceName);
                    if (null != service) {
                        logger.info("service: " + recClient.toString());
                        if (null == recClient.methodName || recClient.methodName.isEmpty()) {
                            sendError(outStream, recClient, " Method can not be found: ");
                        } else
                            for (Method method : service.getClass().getMethods()) {
                                if (method.getName().equals(recClient.methodName)) {
                                    try {
                                        Class[] paramTypes = method.getParameterTypes();
                                        Object[] args = new Object[paramTypes.length];

                                        if (paramTypes.length > 0 && null == recClient.params) {
                                            sendError(outStream, recClient, " No parameters specified: ");
                                            break;
                                        } else if (paramTypes.length > recClient.params.length) {
                                            sendError(outStream, recClient, " Error in the number of parameters: ");
                                            break;
                                        }
                                        int i = 0;
                                        // Do I need to check the types of parameters?
                                        for (Class paramType : paramTypes) {
                                            if (i <= recClient.params.length)
                                                args[i] = recClient.params[i];
                                            i++;
                                        }
                                        Object res = method.invoke(service, args);

                                        RecServer recServer = new RecServer(recClient.commandId, "", res);
                                        outStream.writeObject(recServer);
                                        outStream.flush();
                                        logger.info(recServer);
                                    } catch (IllegalAccessException | InvocationTargetException ex) {
                                        ex.printStackTrace();
                                    }
                                    break;
                                }
                            }
                    } else {
                        sendError(outStream, recClient, "Service can not be found");
                    }
                }
                inputStream.close();
                outStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void sendError(ObjectOutputStream outStream, RecClient inObject, String message) throws IOException {
            RecServer recServer = new RecServer(inObject.commandId, message, null);
            outStream.writeObject(recServer);
            outStream.flush();
            logger.info(recServer);
        }
    }

}
