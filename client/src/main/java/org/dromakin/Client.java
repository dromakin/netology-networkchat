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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromakin.exceptions.ClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private static final Logger logger = LogManager.getLogger(Client.class);

    public void startConnection(String ip, int port) throws ClientException {
        try {
            Scanner scanner = new Scanner(System.in);

            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            logger.info(in.readLine());
            out.println(scanner.nextLine());

            logger.info(in.readLine());
            out.println(scanner.nextLine());

            logger.info(in.readLine());
            out.println(scanner.nextLine());

            logger.info(in.readLine());
            out.println(scanner.nextLine());

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public String sendMessage(String msg) throws ClientException {
        out.println(msg);
        String resp;

        try {
            resp = in.readLine();
        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }

        return resp;
    }

    public void stopConnection() throws ClientException {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startConnection("netology.homework", 8888);
            String res = client.sendMessage("Hi server!");
            logger.info("Server response: {}", res);
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);

            try {
                client.stopConnection();
            } catch (ClientException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
