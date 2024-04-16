package com.email.server;


import java.util.HashMap;
import java.util.Map;

public class ServerModel {
    private static final Map<String, String> clientMailboxMap = new HashMap<>();


    static String getMailboxFileName(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/src/main/resources/com/email/server/servermailbox/" + user + ".txt";
    }

    static void addClientEntry(String clientName, String text) {
        clientMailboxMap.put(clientName, text);
        System.out.println("Nuova entry aggiunta: " + clientName + " - " + text);
    }
}