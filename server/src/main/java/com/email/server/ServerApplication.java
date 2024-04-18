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

    /**
     * Metodo per avviare l'applicazione server
     * <p>
     * @param primaryStage Stage dell'applicazione
     * @throws Exception Eccezione generica
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("UI/server-view.fxml")));
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

    /**
     * Metodo per avviare il server
     */

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server avviato sulla porta " + PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ServerModel.addLogEntry("Server", "Connessione accettata da " + clientSocket, LocalDateTime.now().toString());
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

    /**
     * Metodo per fermare il server
     * <p>
     * @throws Exception Eccezione generica
     */

    @Override
    public void stop() throws Exception {
        super.stop();
        stopServer();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server chiuso.");
        }
    }

    /**
     * Metodo per fermare il server e salvare i log
     */

    private void stopServer() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                ServerModel.addLogEntry("Server", "Server chiuso", LocalDateTime.now().toString());
                ServerModel.saveLogs();
            } catch (IOException e) {
                logger.severe("Errore durante la chiusura del server: " + e.getMessage());
                ServerModel.addLogEntry("Server", "Errore durante la chiusura del server: " + e.getMessage(), LocalDateTime.now().toString());
            }
        }
    }

    /**
     * Metodo main per avviare l'applicazione
     * <p>
     * @param args Argomenti passati da riga di comando
     */

    public static void main(String[] args) {
        launch(args);
    }
}
