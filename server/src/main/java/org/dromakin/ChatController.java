/*
 * File:     ChatController
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

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ClientHandlerException;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChatController {
    private static final Logger logger = LogManager.getLogger(ChatController.class);
    private static final int TIMEOUT_MILLISECONDS = 10_000;
    private static final int PORT = 8080;
    public static final int MAX_CHAT_MEMBERS = 100;
    public static final String SYSTEM_MSG_START = "System BOT";

    @Getter(AccessLevel.PUBLIC)
    private final ChatClients chatClients = new ChatClients();

    private final ConnectionHandler connectionHandler;

    @Getter(AccessLevel.PUBLIC)
    private final int port;

    @Getter(AccessLevel.PUBLIC)
    private final int timeout;

    @Getter(AccessLevel.PUBLIC)
    private final int maxMembers;

    private final AtomicInteger countMembers = new AtomicInteger(0);
    private final AtomicLong countMessages = new AtomicLong(0L);

    public ChatController(Integer port, Integer timeout, Integer maxMembers) {
        this.port = getPort(port);
        this.timeout = getTimeout(timeout);
        this.maxMembers = getMaxChatMembers(maxMembers);
        this.connectionHandler = new ConnectionHandler(this);
    }

    private int getTimeout(Integer timeout) {
        return timeout == null ? TIMEOUT_MILLISECONDS : timeout;
    }

    private int getPort(Integer port) {
        return port == null ? PORT : port;
    }

    private int getMaxChatMembers(Integer maxMembers) {
        return maxMembers == null ? MAX_CHAT_MEMBERS : maxMembers;
    }

    protected int getCountMembers() {
        return countMembers.get();
    }

    protected void incrementCountMembers() {
        countMembers.incrementAndGet();
    }

    protected long getCountMessages() {
        return countMessages.get();
    }

    protected void incrementCountMessages() {
        countMessages.incrementAndGet();
    }

    public void stopChat() throws ClientHandlerException {
        String msgWarning = String.format("%s: Get command to close chat! Start processing...", SYSTEM_MSG_START);
        logger.debug(msgWarning);
        logger.info(msgWarning);
        if (!chatClients.getActiveClients().isEmpty()) {
            for (ClientHandler c : chatClients.getActiveClients().values()) {
                c.close();
                logger.debug("Client @{} disconnected!", c.getClientInfo().getNickname());
            }
        }
        connectionHandler.interrupt();
        logger.info("Server finished!");
    }

    public void startChat() {
        connectionHandler.start();
    }

    public void addClient(Socket client) {
        logger.info("Connected new client: {}:{}", client.getInetAddress(), client.getPort());
        Thread thread = new ClientHandler(this, client, getCountMembers());
        thread.start();
    }

    public void registrationClient(ClientHandler clientHandler) {
        // 1 variant
        // send json to auth server
        // 2 variant *
        // create ConcurrentHashMap
        chatClients.getActiveClients().put(clientHandler.getClientInfo().getNickname(), clientHandler);
    }

    public void sendSystemMessage(String message) throws ClientHandlerException {
        logger.debug("{}: {}", SYSTEM_MSG_START, message);
        for (ClientHandler client : chatClients.getActiveClients().values()) {
            client.sendSystemInfo(message);
        }
        incrementCountMessages();
    }

    public synchronized void sendAll(String message, ClientHandler sender) throws ClientHandlerException {
        logger.debug("@{}: {}", sender.getClientInfo().getNickname(), message);
        for (ClientHandler clients : chatClients.getActiveClients().values()) {
            clients.sendClient(message);
        }
        incrementCountMessages();
    }

}
