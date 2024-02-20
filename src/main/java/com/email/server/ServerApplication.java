package com.email.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication extends Application {

    private ServerSocket serverSocket;
    private static final int PORT = 12345;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica la GUI
        Parent root = FXMLLoader.load(getClass().getResource("server-view.fxml"));
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

                // Gestione del client in un thread separato
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server chiuso.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
