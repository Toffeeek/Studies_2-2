package com.iut.chat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class ChatProtocol {
    static final int PORT = 5001;
    static final String HOST = "localhost";

    private ChatProtocol() {
    }

    static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    static String command(String name, String... args) {
        StringBuilder builder = new StringBuilder(name);
        for (String arg : args) {
            builder.append('|').append(arg);
        }
        return builder.toString();
    }
}
