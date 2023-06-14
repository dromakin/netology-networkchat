package org.dromakin;

import org.dromakin.exceptions.ChatClientException;
import org.dromakin.exceptions.ClientException;
import org.dromakin.exceptions.ClientHandlerException;
import org.dromakin.exceptions.ServerException;
import org.dromakin.models.ClientInfo;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServerClientIntegrationTest {

    private static final Path CLIENT_LOG = Paths.get("logs/client.log");
    private static final Path SERVER_LOG = Paths.get("logs/server.log");

    private ChatClient chatClient;
    private Server server;
    private Client client;
    private ChatController chatController;

    @BeforeEach
    void setUp() throws ServerException, ClientException {
        ClientInfo clientInfo = ClientInfo.builder()
                .nickname("nickname")
                .name("name")
                .surname("surname")
                .build();

        // server
        server = new Server();
        server.loadSettings();
        chatController = new ChatController(server.getPort(), server.getTimeout(), server.getMaxClients());
        // client
        client = new Client();
        client.loadSettings();
        chatClient = new ChatClient("localhost", server.getPort(), clientInfo);
    }

    private void close(boolean cl, boolean serv) throws ChatClientException, ClientHandlerException {
        if (cl)
            chatClient.close();

        if (serv)
            chatController.stopChat();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(CLIENT_LOG)) {
            Files.delete(CLIENT_LOG);
        }

        if (Files.exists(SERVER_LOG)) {
            Files.delete(SERVER_LOG);
        }
    }

    @Test
    @Order(1)
    void getCountMembersMessages() throws ChatClientException, ClientHandlerException, InterruptedException {
        chatController.startChat();
        Thread.sleep(5000);
        assertEquals(0, chatController.getCountMembers());
        chatClient.connect();
        assertEquals(1, chatController.getCountMembers());
        assertEquals(1, chatController.getCountMessages());
        close(true, true);
    }

    @Test
    @Order(2)
    void getCountMessages() throws ChatClientException, ClientHandlerException, InterruptedException {
        chatController.startChat();
        Thread.sleep(3000);
        assertEquals(0, chatController.getCountMessages());
        chatClient.connect();
        close(true, true);
    }

    @Test
    @Order(3)
    void getChatClients() throws ChatClientException, ClientHandlerException, InterruptedException {
        chatController.startChat();
        Thread.sleep(5000);
        assertEquals(0, chatController.getChatClients().getActiveClients().size());
        chatClient.connect();
        assertEquals(1, chatController.getChatClients().getActiveClients().size());
        close(true, true);
    }

    @Test
    @Order(4)
    void getPort() {
        assertEquals(8888, server.getPort().intValue());
    }

    @Test
    @Order(5)
    void getTimeout() {
        assertEquals(500000, server.getTimeout().intValue());
    }

    @Test
    @Order(6)
    void getMaxMembers() {
        assertEquals(10, server.getMaxClients().intValue());
    }
}