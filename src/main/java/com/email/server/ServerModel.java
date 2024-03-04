package com.email.server;

import com.email.email.Email;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerModel {
    private static Map<String, String> clientMailboxMap = new HashMap<>();

    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server avviato. In attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connessione accettata da " + clientSocket.getInetAddress());

                Thread clientHandlerThread = new Thread(() -> handleClient(clientSocket));
                clientHandlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        System.out.println("fdgfgr");
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            String clientName = in.readLine();

            System.out.println("Nome del client: " + clientName);

            String mailboxFileName = getMailboxFileName(clientName);
            out.write(mailboxFileName + "\n");
            out.flush();

            addClientEntry(clientName, "aggiornamento");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("3445554");
    }

    static String getMailboxFileName(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/src/main/resources/com/email/server/servermailbox/" + user + ".txt";
    }

    static void addClientEntry(String clientName, String text) {
        clientMailboxMap.put(clientName, text);
        System.out.println("Nuova entry aggiunta: " + clientName + " - " + text);
    }
}