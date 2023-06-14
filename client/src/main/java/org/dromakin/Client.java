/*
 * File:     Client
 * Package:  org.dromakin
 * Project:  netology_client_server
 *
 * Created by dromakin as 21.01.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.01.21
 */

package org.dromakin;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ChatClientException;
import org.dromakin.exceptions.ChatMessageViewerException;
import org.dromakin.exceptions.ClientException;
import org.dromakin.models.ClientInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;


public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);

    private static final String NAME_CLIENT_PROPERTIES_FILE = "client.properties";

    private final Path pathToPropertyFile;

    @Getter(AccessLevel.PUBLIC)
    private Integer port;

    @Getter(AccessLevel.PUBLIC)
    private String host;

    public Client()  {
        this.pathToPropertyFile = getResourceFile();
    }

    private Path getResourceFile() {
        URL url = Client.class.getClassLoader().getResource(NAME_CLIENT_PROPERTIES_FILE);

        if (url == null) {
            throw new IllegalArgumentException(NAME_CLIENT_PROPERTIES_FILE + " is not found!");
        }

        return Paths.get(url.getFile());
    }

    private void loadSettings() throws ClientException {
        try (InputStream input = Files.newInputStream(this.pathToPropertyFile)) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property values and create MongoBuilder
            this.port = Integer.parseInt(prop.getProperty("server.port"));
            this.host = prop.getProperty("server.host");

        } catch (IOException e) {
            String errMsg = "Can't load settings from properties file!";
            logger.error(errMsg);
            throw new ClientException(errMsg, e);
        }
    }

    public static void main(String[] args) {
        logger.info("Welcome to terminal chat!");

        try (Scanner scanner = new Scanner(System.in)) {
            Client client = new Client();
            client.loadSettings();

            logger.info("Please write who you are:");
            logger.info("Write your nickname:");
            String nickname = scanner.nextLine();

            logger.info("Write your name:");
            String name = scanner.nextLine();

            logger.info("Write your surname:");
            String surname = scanner.nextLine();

            ClientInfo clientInfo = ClientInfo.builder()
                    .nickname(nickname)
                    .name(name)
                    .surname(surname)
                    .build();

            ChatClient chatClient = new ChatClient(client.getHost(), client.getPort(), clientInfo);
            boolean connected = chatClient.connect();
            if (!connected) {
                throw new ClientException("Can't connect to server!");
            }
            printMenu();

            boolean exit = false;
            while (!exit) {
                String command = scanner.nextLine();
                switch (command) {
                    case "/chat":
                        ChatMessageViewer chatMessageViewer = new ChatMessageViewer();
                        chatMessageViewer.view();
                        logger.info("Go back to menu...");
                        printMenu();
                        break;

                    case "/msg":
                        logger.info("Write your message:");
                        String msg = scanner.nextLine();
                        chatClient.sendMessage(msg);
                        logger.info("Go back to menu...");
                        printMenu();
                        break;

                    case "/exit":
                        chatClient.close();
                        exit = true;
                        break;

                    case "/menu":
                    case "/help":
                    case "/h":
                        printMenu();
                        break;

                    default:
                        logger.warn("Unknown command!");
                        printMenu();
                        break;

                }
            }

        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
            e.print();
        } catch (ChatClientException e) {
            logger.error(e.getMessage(), e);
            e.print();
        } catch (ChatMessageViewerException e) {
            logger.error(e.getMessage(), e);
            e.print();
        }
    }

    protected static void printMenu() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Welcome to client menu!").append("\n")
                .append("Client CLI commands:").append("\n")
                .append("/chat - view chat").append("\n")
                .append("/msg - type msg and send").append("\n")
                .append("/exit - stop client and close menu");

        logger.info(stringBuilder);
    }

}
