package com.iut.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ChatServer {
    private final ChatDatabase database = new ChatDatabase();
    private final Map<String, ClientSession> onlineClients = new ConcurrentHashMap<>();
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        System.out.println("Chat server listening on port " + ChatProtocol.PORT);
        try (ServerSocket serverSocket = new ServerSocket(ChatProtocol.PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                clientPool.submit(new ClientSession(socket));
            }
        } catch (IOException ex) {
            System.err.println("Server stopped: " + ex.getMessage());
        }
    }

    private void broadcastUsers() {
        String[] encodedUsers = database.users().stream()
                .map(ChatProtocol::encode)
                .toArray(String[]::new);
        String line = ChatProtocol.command("USERS", encodedUsers);
        onlineClients.values().forEach(client -> client.send(line));
    }

    private String messageLine(Message message) {
        return ChatProtocol.command(
                "MESSAGE",
                ChatProtocol.encode(message.sender()),
                ChatProtocol.encode(message.recipient()),
                Long.toString(message.sentAt()),
                ChatProtocol.encode(message.body()));
    }

    private final class ClientSession implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String username;

        private ClientSession(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    handle(line);
                }
            } catch (IOException ex) {
                System.err.println("Client disconnected: " + ex.getMessage());
            } finally {
                if (username != null) {
                    onlineClients.remove(username, this);
                }
            }
        }

        private void handle(String line) {
            String[] parts = line.split("\\|", -1);
            switch (parts[0]) {
                case "REGISTER" -> register(parts);
                case "LOGIN" -> login(parts);
                case "SEND" -> sendMessage(parts);
                case "HISTORY" -> sendHistory(parts);
                default -> send(ChatProtocol.command("ERROR", ChatProtocol.encode("Unknown command")));
            }
        }

        private void register(String[] parts) {
            if (parts.length != 3) {
                send(ChatProtocol.command("AUTH_FAIL", ChatProtocol.encode("Invalid register request")));
                return;
            }
            String requestedUsername = ChatProtocol.decode(parts[1]).trim();
            String password = ChatProtocol.decode(parts[2]);
            if (database.register(requestedUsername, password)) {
                send(ChatProtocol.command("REGISTER_OK", ChatProtocol.encode("Account created. You can log in now.")));
                broadcastUsers();
            } else {
                send(ChatProtocol.command("AUTH_FAIL", ChatProtocol.encode(
                        "Username must be unique, 3-20 letters/numbers/underscores; password needs 4+ characters.")));
            }
        }

        private void login(String[] parts) {
            if (parts.length != 3) {
                send(ChatProtocol.command("AUTH_FAIL", ChatProtocol.encode("Invalid login request")));
                return;
            }
            String requestedUsername = ChatProtocol.decode(parts[1]).trim();
            String password = ChatProtocol.decode(parts[2]);
            if (!database.authenticate(requestedUsername, password)) {
                send(ChatProtocol.command("AUTH_FAIL", ChatProtocol.encode("Wrong username or password.")));
                return;
            }
            username = requestedUsername;
            ClientSession previous = onlineClients.put(username, this);
            if (previous != null && previous != this) {
                previous.send(ChatProtocol.command("ERROR", ChatProtocol.encode("You signed in from another window.")));
                previous.close();
            }
            send(ChatProtocol.command("AUTH_OK", ChatProtocol.encode(username)));
            broadcastUsers();
        }

        private void sendMessage(String[] parts) {
            if (!isLoggedIn() || parts.length != 3) {
                send(ChatProtocol.command("ERROR", ChatProtocol.encode("Select a user and type a message first.")));
                return;
            }
            String recipient = ChatProtocol.decode(parts[1]);
            String body = ChatProtocol.decode(parts[2]).trim();
            if (recipient.equals(username) || body.isBlank() || !database.users().contains(recipient)) {
                send(ChatProtocol.command("ERROR", ChatProtocol.encode("Invalid recipient or message.")));
                return;
            }

            Message message = database.saveMessage(username, recipient, body);
            String messageLine = messageLine(message);
            send(messageLine);
            ClientSession recipientClient = onlineClients.get(recipient);
            if (recipientClient != null) {
                recipientClient.send(messageLine);
            }
        }

        private void sendHistory(String[] parts) {
            if (!isLoggedIn() || parts.length != 2) {
                return;
            }
            String otherUser = ChatProtocol.decode(parts[1]);
            send(ChatProtocol.command("HISTORY_BEGIN", ChatProtocol.encode(otherUser)));
            List<Message> history = database.history(username, otherUser);
            for (Message message : history) {
                send(messageLine(message));
            }
            send(ChatProtocol.command("HISTORY_END", ChatProtocol.encode(otherUser)));
        }

        private boolean isLoggedIn() {
            return username != null;
        }

        private void send(String line) {
            if (out != null) {
                out.println(line);
            }
        }

        private void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
