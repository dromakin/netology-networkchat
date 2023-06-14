/*
 * File:     ChatMessageViewer
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

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ChatMessageViewerException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor
public class ChatMessageViewer {

    private static final Logger logger = LogManager.getLogger(ChatMessageViewer.class);
    private static final Path LOG_PATH = Paths.get("logs/client.log");
    private static final int BUFFER_SIZE = 8192;

    public synchronized void view() throws ChatMessageViewerException {
        try (InputStream input = new BufferedInputStream(new FileInputStream(LOG_PATH.normalize().toAbsolutePath().toString()))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int length = 0; (length = input.read(buffer)) != -1; ) {
                System.out.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            String msgErr = "Can't find client log file!";
            logger.error(msgErr, e);
            throw new ChatMessageViewerException(msgErr, e);
        } catch (IOException e) {
            String msgErr = "Can't read client log file!";
            logger.error(msgErr, e);
            throw new ChatMessageViewerException(msgErr, e);
        }
    }

}
