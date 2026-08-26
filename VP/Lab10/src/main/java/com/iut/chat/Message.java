package com.iut.chat;

record Message(String sender, String recipient, String body, long sentAt) {
}
