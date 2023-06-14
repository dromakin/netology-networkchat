/*
 * File:     ChatClient
 * Package:  org.dromakin
 * Project:  networkchat
 *
 * Created by dromakin as 13.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.13
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ChatClientException;
import org.dromakin.models.ClientInfo;
import org.dromakin.models.ClientMessage;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ChatClient {
    private static final Logger logger = LogManager.getLogger(ChatClient.class);

    private static final String COMMAND_CLIENT_START = "/";
    private static final String COMMAND_CLIENT_CONFIGURATION = COMMAND_CLIENT_START + "client";
    private static final String COMMAND_CLIENT_CONFIGURATION_SPLITTER = " ";
    private static final String COMMAND_CLIENT_STOP = COMMAND_CLIENT_START + "stop";
    private static final String MSG_OK = "OK";
    private static final String MSG_ON_START = "For start send \"/client {JSON with Client data}";
    private static final String MSG_CLIENT_CONNECTION_ERROR_LIMITED = "Limited number of chat participants! Please contact to support!";
    private static final String MSG_CLIENT_CONNECTION_ERROR_CLIENT_INFO = "Client information didn't fill correctly!";
    private static final String MSG_CLIENT_END = "\n";
    private static final int WAIT_CHAT_CLOSE = 2000;
    private static final String SERVER_DISCONNECTED = "Server disconnected!";

    private final String host;
    private final int port;
    private final ClientInfo clientInfo;
    private ChatMessageReader chatMessageReader;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ChatClient(String host, int port, ClientInfo clientInfo) {
        this.host = host;
        this.port = port;
        this.clientInfo = clientInfo;
    }

    public boolean connect() throws ChatClientException {
        logger.info("User @{} try to connect to server {}:{}", clientInfo.getNickname(), host, port);

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message;

            ObjectMapper objectMapper = new ObjectMapper();
            message = in.readLine();
            if (message.equals(MSG_ON_START)) {
                String jsonStr = objectMapper.writeValueAsString(clientInfo);
                out.write(COMMAND_CLIENT_CONFIGURATION + COMMAND_CLIENT_CONFIGURATION_SPLITTER + jsonStr + MSG_CLIENT_END);
                out.flush();
            }

            message = in.readLine();
            ClientMessage clientMessage = objectMapper.readValue(message, ClientMessage.class);
            if (!clientMessage.getNickname().equals(clientInfo.getNickname())) {
                return false;
            }

            if (clientMessage.getMessage().equals(MSG_OK)) {
                this.chatMessageReader = new ChatMessageReader();
                this.chatMessageReader.start();

            } else if (clientMessage.getMessage().equals(MSG_CLIENT_CONNECTION_ERROR_LIMITED)) {
                return false;
            }

        } catch (IOException e) {
            String msgErr = "Can't connect to server!";
            logger.error(msgErr, e);
            throw new ChatClientException(msgErr, e);
        }

        return socket.isConnected();
    }


    public void close() throws ChatClientException {
        try {
            out.write(COMMAND_CLIENT_STOP + MSG_CLIENT_END);
            out.flush();
            this.chatMessageReader.interrupt();
            Thread.sleep(WAIT_CHAT_CLOSE);
            socket.close();
        } catch (IOException e) {
            String msgErr = "Error while send close command to server!";
            logger.error(msgErr, e);
            throw new ChatClientException(msgErr, e);
        } catch (InterruptedException e) {
            String msgErr = "Error while closing chat!";
            logger.error(msgErr, e);
            throw new ChatClientException(msgErr, e);
        }
    }

    public void sendMessage(String msg) throws ChatClientException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(
                    ClientMessage.builder()
                            .nickname(clientInfo.getNickname())
                            .message(msg)
                            .time(timestamp.getTime())
                            .build()
            );
            out.write(jsonStr + MSG_CLIENT_END);
            out.flush();

        } catch (IOException e) {
            String msgErr = "Can't send msg to chat!";
            logger.error(msgErr, e);
            throw new ChatClientException(msgErr, e);
        }
    }

    private class ChatMessageReader extends Thread {
        @Override
        public void run() {
            String message;
            try {
                while (this.isAlive()) {
                    message = in.readLine();

                    if (message == null) {
                        logger.info(SERVER_DISCONNECTED);
                        break;
                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    ClientMessage clientMessage = objectMapper.readValue(message, ClientMessage.class);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    long timeDelay = timestamp.getTime() - clientMessage.getTime();

                    StringBuilder stringBuilder = new StringBuilder()
                            .append("| delay: ").append(timeDelay).append(" ms | ")
                            .append("@").append(clientMessage.getNickname()).append(": ")
                            .append(clientMessage.getMessage());

                    // log in file
                    logger.debug(stringBuilder);
                }

            } catch (IOException e) {
                String msgErr = "IO Error while getting msg!";
                logger.error(msgErr, e);
                this.interrupt();
                if (e.getCause() != null)
                    e.printStackTrace();
            }
        }

    }
}
