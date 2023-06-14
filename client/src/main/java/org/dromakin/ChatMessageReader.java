/*
 * File:     ChatMessageReader
 * Package:  org.dromakin
 * Project:  networkchat
 *
 * Created by dromakin as 14.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.14
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.models.ClientMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;

public class ChatMessageReader extends Thread {

    private static final Logger logger = LogManager.getLogger(ChatMessageReader.class);

    private static final String SERVER_DISCONNECTED = "Server disconnected!";
    private final BufferedReader in;

    public ChatMessageReader(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        try {
            while (!Thread.currentThread().isInterrupted()) {
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
