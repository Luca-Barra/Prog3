package com.email.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static com.email.server.ServerModel.addClientEntry;
import static com.email.server.ServerModel.getMailboxFileName;

public class ServerApplication extends Application {

    private ServerSocket serverSocket;
    private static final int PORT = 12345;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica la GUI
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("server-view.fxml")));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        // Avvia il server in un thread separato
        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server avviato sulla porta " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connessione accettata da " + clientSocket);

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

    @Override
    public void stop() throws Exception {
        super.stop();
        // Interruzione del thread del server prima di chiudere il socket
        stopServer();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server chiuso.");
        }
    }

    private void stopServer() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
