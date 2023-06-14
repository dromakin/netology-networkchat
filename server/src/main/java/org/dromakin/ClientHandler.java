/*
 * File:     ClientHandler
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ClientHandlerException;
import org.dromakin.models.ClientInfo;
import org.dromakin.models.ClientMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;

public class ClientHandler extends Thread {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private static final String COMMAND_CLIENT_START = "/";
    private static final String COMMAND_CLIENT_CONFIGURATION = COMMAND_CLIENT_START + "client";
    private static final String COMMAND_CLIENT_STOP = COMMAND_CLIENT_START + "stop";
    private static final String COMMAND_CLIENT_CONFIGURATION_SPLITTER = " ";
    private static final String MSG_CLIENT_JSON_START = "{";
    private static final String MSG_OK = "OK";
    private static final String MSG_ON_START = "For start send \"/client {JSON with Client data}";
    private static final String MSG_CLIENT_CONNECTION_ERROR_LIMITED = "Limited number of chat participants! Please contact to support!";
    private static final String MSG_CLIENT_CONNECTION_ERROR_CLIENT_INFO = "Client information didn't fill correctly!";
    private static final String MSG_CLIENT_END = "\n";
    private final Socket clientSocket;
    private final ChatController chatController;
    private final int countMembers;

    @Getter(AccessLevel.PUBLIC)
    private ClientInfo clientInfo;
    private BufferedReader in;
    private BufferedWriter out;


    public ClientHandler(ChatController chatController, Socket socket, int countMembers) {
        this.chatController = chatController;
        this.clientSocket = socket;
        this.countMembers = countMembers;
        this.clientInfo = null;
    }

    public void run() {
        String text;

        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            sendOnStart(MSG_ON_START);

            while (true) {
                text = in.readLine();

                if (text == null) {
                    logger.info("Connection with client lost!");
                    close();
                    break;
                }

                processMessage(text);
            }

        } catch (SocketException e) {
            String msgErr = String.format("Сlient disconnected [%s]", clientInfo.getNickname());
            logger.error(msgErr, e);
            this.interrupt();
        } catch (IOException e) {
            String msgErr = String.format("IO Error while processing client [%s]", clientInfo.getNickname());
            logger.error(msgErr, e);
            this.interrupt();
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null)
                ex.printStackTrace();
        }

        logger.info("Thread user @{} finished!", clientInfo.getNickname());
    }


    private void sendOnStart(String msg) throws ClientHandlerException {
        try {
            out.write(msg + MSG_CLIENT_END);
            out.flush();
        } catch (IOException e) {
            String msgErr = "Unregistered client socket has been closed!";
            logger.error(msgErr, e);
            throw new ClientHandlerException(msgErr, e);
        }
    }

    private void send(String msg, String nickname) throws ClientHandlerException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(
                    ClientMessage.builder()
                            .nickname(nickname)
                            .message(msg)
                            .time(timestamp.getTime())
                            .build()
            );
            out.write(jsonStr + MSG_CLIENT_END);
            out.flush();
        } catch (IOException e) {
            String msgErr = String.format("Client socket @%s has been closed!", nickname);
            logger.error(msgErr, e);
            throw new ClientHandlerException(msgErr, e);
        }
    }

    public void sendSystemInfo(String msg) throws ClientHandlerException {
        send(msg, "System BOT");
    }

    public void sendClient(String msg) throws ClientHandlerException {
        send(msg, this.clientInfo.getNickname());
    }

    public void close() throws ClientHandlerException {
        try {
            this.clientSocket.close();
            this.chatController.getChatClients().getActiveClients().remove(clientInfo.getNickname());
            logger.info("Thread client @{} finished!", clientInfo.getNickname());
        } catch (IOException e) {
            String msgErr = String.format("Error while closing client's socket [%s]!", clientInfo.getNickname());
            logger.error(msgErr, e);
            throw new ClientHandlerException(msgErr, e);
        }
    }

    // processMessage
    private void processMessage(String message) throws ClientHandlerException {
        try {

            if (message.startsWith(COMMAND_CLIENT_START)) {

                processCommand(message);

            } else if (message.startsWith(MSG_CLIENT_JSON_START)) {

                ObjectMapper objectMapper = new ObjectMapper();
                ClientMessage clientMessage = objectMapper.readValue(message, ClientMessage.class);
                if (clientMessage.getNickname().equals(this.clientInfo.getNickname())) {
                    this.chatController.sendAll(clientMessage.getMessage(), this);
                }

            }

        } catch (JsonProcessingException e) {
            String msgErr = "Error while parsing json with client message!";
            logger.error(msgErr, e);
            throw new ClientHandlerException(msgErr, e);
        }
    }

    // processCommand
    private void processCommand(String message) throws ClientHandlerException, JsonProcessingException {
        if (message.startsWith(COMMAND_CLIENT_CONFIGURATION)) {

            try {
                String[] msgParts = message.split(COMMAND_CLIENT_CONFIGURATION_SPLITTER);

                ObjectMapper objectMapper = new ObjectMapper();
                if (msgParts.length == 2) {
                    if (msgParts[1].startsWith(MSG_CLIENT_JSON_START)) {
                        this.clientInfo = objectMapper.readValue(msgParts[1], ClientInfo.class);

                        // check count members
                        if (countMembers >= ChatController.MAX_CHAT_MEMBERS) {
                            sendClient(MSG_CLIENT_CONNECTION_ERROR_LIMITED);
                            close();
                        }

                        // registration
                        if (this.clientInfo.getNickname() == null) {
                            sendClient(MSG_CLIENT_CONNECTION_ERROR_CLIENT_INFO);
                            close();
                        } else {
                            this.chatController.registrationClient(this);
                        }

                        // send to client that ok
                        sendClient(MSG_OK);

                        // send to chat: we have new client
                        // optional: check user if auth server now it
                        // yes - Welcome back!
                        // no - Welcome!
                        this.chatController.sendSystemMessage(String.format("User @%s has joined our chat!", this.clientInfo.getNickname()));

                        // update atomic values - count members
                        this.chatController.incrementCountMembers();
                    }
                }

            } catch (JsonProcessingException e) {
                String msgErr = "Error while parsing json with client info!";
                logger.error(msgErr, e);
                throw new ClientHandlerException(msgErr, e);
            }

        }

        if (message.equals(COMMAND_CLIENT_STOP)) {
            // close client
            this.chatController.sendSystemMessage(String.format("User @%s left the chat!", this.clientInfo.getNickname()));
            close();
        }
    }

}
