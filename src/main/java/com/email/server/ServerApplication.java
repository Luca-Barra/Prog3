package com.email.server;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Logger;

public class ServerApplication extends Application {

    private ServerSocket serverSocket;
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("server-view.fxml")));
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                stop();
                System.exit(0);
            } catch (Exception e) {
                logger.severe("Errore durante la chiusura del server: " + e.getMessage());
            }
        });
        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server avviato sulla porta " + PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ServerController.addLogEntry("Server", "Connessione accettata da " + clientSocket, LocalDateTime.now().toString());
                System.out.println("Connessione accettata da " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread clientHandlerThread = new Thread(clientHandler);
                clientHandlerThread.start();
            }
        } catch (IOException e) {
            if(serverSocket.isClosed()) {
                System.out.println("Server was closed successfully.");
            } else {
                logger.severe("Errore durante l'avvio del server: " + e.getMessage());
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
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
                ServerController.addLogEntry("Server", "Server chiuso", LocalDateTime.now().toString());
                ServerController.saveLogs();
            } catch (IOException e) {
                logger.severe("Errore durante la chiusura del server: " + e.getMessage());
                ServerController.addLogEntry("Server", "Errore durante la chiusura del server: " + e.getMessage(), LocalDateTime.now().toString());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
