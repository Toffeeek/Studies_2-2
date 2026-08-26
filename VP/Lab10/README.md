# Lab 10 Multi-user Chat

This is an IntelliJ/Maven Java project for the Visual Programming Lab 10 socket chat task.

## Requirements Covered

- Register a new user with username and password.
- Log in before opening the chat screen.
- Show all registered users in the left side panel.
- Select a user and exchange direct one-to-one messages.
- Save all accounts and messages in the local MariaDB server.
- Load the full previous message history whenever a conversation is opened.

## Run in IntelliJ IDEA

1. Open this folder as a project in IntelliJ IDEA.
2. Let IntelliJ import the Maven project and download dependencies.
3. Run `com.iut.chat.ChatServer` first.
4. Run `com.iut.chat.ChatClient` twice or more to open multiple clients.
5. Register different accounts, log in, select another user, and send messages.

The server listens on port `5001`. Database tables are created automatically when the server starts.

## MariaDB

This project uses the MariaDB server already installed on this PC.

Default connection:

- URL: `jdbc:mariadb://localhost:3306/test`
- User: `tawfiq`
- Password: empty
- Tables: `lab10_chat_users`, `lab10_chat_messages`

You can override those values with environment variables before running the server:

- `CHAT_DB_URL`
- `CHAT_DB_USER`
- `CHAT_DB_PASSWORD`

## Edit UI in Scene Builder

The JavaFX layouts are in `src/main/resources`:

- `login-view.fxml`
- `chat-view.fxml`

Open either file in Scene Builder to edit the layout. Keep the existing `fx:id`
values unless you also update `ChatClientApp.java`, because the client code uses
those IDs to connect buttons and fields to the chat logic.
