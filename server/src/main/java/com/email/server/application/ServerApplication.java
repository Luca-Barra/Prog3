package com.email.server.application;

import com.email.server.handler.ClientHandler;
import com.email.server.model.ServerModel;
import com.email.server.utils.LogEntry;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Logger;

public class ServerApplication extends Application {

    private ServerSocket serverSocket;
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Metodo per avviare l'applicazione server
     * <p>
     * @param primaryStage Stage dell'applicazione
     * @throws Exception Eccezione generica
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("ServerApplication started");
        ServerModel.initializeLogs();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/email/server/UI/view/server-view.fxml")));
        Scene scene = new Scene(root, 900, 600);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/email/server/UI/css/server.css")).toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
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
            LogEntry logEntry = new LogEntry("Server", "Server aperto", LocalDateTime.now().format(formatter));
            ServerModel.addLogEntry(logEntry);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server avviato sulla porta " + PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                logEntry = new LogEntry("Server", "Connessione accettata da " + clientSocket, LocalDateTime.now().format(formatter));
                ServerModel.addLogEntry(logEntry);
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
            LogEntry logEntry = new LogEntry("Server", "Server chiuso", LocalDateTime.now().format(formatter));
            try {
                serverSocket.close();
                ServerModel.addLogEntry(logEntry);
            } catch (IOException e) {
                logger.severe("Errore durante la chiusura del server: " + e.getMessage());
                ServerModel.addLogEntry(logEntry);
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
