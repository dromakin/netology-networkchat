/*
 * File:     ConnectionHandler
 * Package:  org.dromakin
 * Project:  networkchat
 *
 * Created by dromakin as 07.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.07
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ConnectionHandlerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnectionHandler extends Thread {

    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class);

    private final ChatController chatController;

    public ConnectionHandler(ChatController chatController) {
        this.chatController = chatController;
    }

    @Override
    public void run() {
        logger.info("Start listening incoming messages!");
        try (ServerSocket serverSocket = new ServerSocket(chatController.getPort())) {

            logger.info("Listening on port: {}", serverSocket.getLocalPort());
            serverSocket.setSoTimeout(chatController.getTimeout());
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    Socket client = serverSocket.accept();
                    chatController.addClient(client);

                } catch (SocketTimeoutException e) {
                    String msgErr = "Socket timeout!";
                    logger.error(msgErr);
                    throw new ConnectionHandlerException(msgErr, e);
                }
            }

        } catch (IOException e) {
            String msgErr = "Socket closed!";
            logger.error(msgErr);
            this.interrupt();

        } catch (ConnectionHandlerException ex) {
            if (ex.getCause() != null)
                ex.printStackTrace();
        }
    }

}
