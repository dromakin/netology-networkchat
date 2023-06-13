/*
 * File:     Server
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
import org.dromakin.exceptions.ClientHandlerException;
import org.dromakin.exceptions.ServerException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    private static final String NAME_SERVER_PROPERTIES_FILE = "server.properties";

    private final Path pathToPropertyFile;

    @Getter(AccessLevel.PUBLIC)
    private Integer port;
    @Getter(AccessLevel.PUBLIC)
    private Integer timeout;
    @Getter(AccessLevel.PUBLIC)
    private Integer maxClients;

    public Server() {
        this.pathToPropertyFile = getResourceFile();
    }

    private Path getResourceFile() {
        URL url = Server.class.getClassLoader().getResource(NAME_SERVER_PROPERTIES_FILE);

        if (url == null) {
            throw new IllegalArgumentException(NAME_SERVER_PROPERTIES_FILE + " is not found!");
        }

        return Paths.get(url.getFile());
    }

    private void loadSettings() throws ServerException {
        try (InputStream input = Files.newInputStream(this.pathToPropertyFile)) {
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property values and create MongoBuilder
            this.port = Integer.parseInt(prop.getProperty("server.port"));
            this.timeout = Integer.parseInt(prop.getProperty("server.timeout"));
            this.maxClients = Integer.parseInt(prop.getProperty("server.chat.clients"));

        } catch (IOException e) {
            String errMsg = "Can't load settings from properties file!";
            logger.error(errMsg);
            throw new ServerException(errMsg, e);
        }
    }


    public static void main(String[] args) {
        logger.info("Welcome to server management console!");

        try (Scanner scanner = new Scanner(System.in)) {
            Server server = new Server();
            server.loadSettings();
            printMenu();
            ChatController chatController = new ChatController(server.getPort(), server.getTimeout(), server.getMaxClients());

            boolean exit = false;
            while (!exit) {
                String command = scanner.nextLine();

                switch (command) {
                    case "/start":
                        chatController.startChat();
                        break;

                    case "/stop":
                        chatController.stopChat();
                        logger.info("Go back to menu...");
                        printMenu();
                        break;

                    case "/exit":
                        chatController.stopChat();
                        exit = true;
                        break;

                    case "/stat":
                        printStat(chatController.getCountMessages(), chatController.getCountMembers(), server.getMaxClients());
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

        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            e.print();
        } catch (ClientHandlerException e) {
            logger.error(e.getMessage(), e);
            e.print();
        }
    }

    protected static void printMenu() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Welcome to server menu!").append("\n")
                .append("Server CLI commands:").append("\n")
                .append("/start - start server").append("\n")
                .append("/stop - stop server").append("\n")
                .append("/exit - stop server and close menu").append("\n")
                .append("/stat - print statistic from server").append("\n");

        logger.info(stringBuilder);
    }

    protected static void printStat(long countMsgs, int countClients, int maxClients) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Server statistic:").append("\n")
                .append("Messages count: ").append(countMsgs).append("\n")
                .append(countClients).append(" active users out of ").append(maxClients).append("\n");

        logger.info(stringBuilder);
    }
}
