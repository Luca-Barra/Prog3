package com.email.server;

import com.email.email.Email;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.email.server.ServerModel.addClientEntry;
import static com.email.server.ServerModel.getMailboxFileName;

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

                Thread clientHandlerThread = new Thread(() -> handleClient(clientSocket));
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

    private static void handleClient(Socket clientSocket) {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String command = in.readObject().toString();

            System.out.println("Comando ricevuto: " + command);

            out.writeObject("OK");

            switch (command) {
                case "SEND_EMAIL":
                    if(caseSendEmail(in))
                        out.writeObject("Email inviata con successo");
                    else
                        out.writeObject("Errore durante l'invio dell'email");
                    break;
                case "RETRIEVE_EMAILS":
                    caseRetrieveEmails(in, out);
                    break;
                case "DELETE_EMAIL":
                    caseDeleteEmail(in);
                    break;
                default:
                    System.out.println("Comando non riconosciuto: " + command);
            }
            ServerController.addLogEntry("Server", "Comando " + command + " gestito con successo", LocalDateTime.now().toString());
            ServerController.addLogEntry("Server", "Chiusura della connessione con " + clientSocket, LocalDateTime.now().toString());
        } catch (IOException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
            ServerController.addLogEntry("Server", "Errore durante la comunicazione con il client: " + e.getMessage(), LocalDateTime.now().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void caseRetrieveEmails(ObjectInputStream in, ObjectOutputStream out) {
        try {
            String username = (String) in.readObject();
            String mailboxFileName = getMailboxFileName(username);
            List<Email> emailList = getEmails(mailboxFileName);

            out.writeObject(emailList);
            ServerController.addLogEntry(username, "Aggiornamento della casella di posta", LocalDateTime.now().toString());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName))) {
                writer.write("");
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante il recupero delle email: " + e.getMessage());
            ServerController.addLogEntry("Server", "Errore durante il recupero delle email: " + e.getMessage(), LocalDateTime.now().toString());
        }
    }

    private static List<Email> getEmails(String mailboxFileName) throws IOException {
        List<Email> emailList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                Email email = new Email(parts[0], parts[1], parts[2], parts[3], parts[4]);
                emailList.add(email);
            }
        }
        return emailList;
    }

    private static synchronized boolean caseSendEmail(ObjectInputStream in) {
        try {
            Email email = (Email) in.readObject();
            System.out.println("Email ricevuta: " + email);

            String[] destinatari = email.getDestinatario().split(",");

            for (String destinatario : destinatari) {
                if(ServerController.checkUser(destinatario) && !Objects.equals(email.getMittente(), destinatario.trim())) {

                    String mailboxFileName = getMailboxFileName(destinatario.trim());
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, true))) {
                        writer.write(email.getMittente() + ";"
                                + destinatario.trim() + ";"
                                + email.getOggetto() + ";"
                                + email.getTesto() + ";"
                                + email.getData() + "\n");
                    }
                    addClientEntry(destinatario.trim(), "Nuova email da " + email.getMittente());
                    ServerController.addLogEntry(email.getMittente(), "Email inviata a " + destinatario.trim(), LocalDateTime.now().toString());
                    return true;
                } else {
                    ServerController.addLogEntry(email.getMittente(), "Email non inviata a " + destinatario.trim() + ": utente non esistente", LocalDateTime.now().toString());
                    return false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
            ServerController.addLogEntry("Server", "Errore durante l'invio dell'email: " + e.getMessage(), LocalDateTime.now().toString());
        }
        return false;
    }

    private static void caseDeleteEmail(ObjectInputStream in) {
        try {
           Email email = (Email) in.readObject();
            System.out.println("Email da eliminare: " + email);

            String mailboxFileName = getMailboxFileName(email.getDestinatario());
            List<Email> emailList = getEmails(mailboxFileName);
            emailList.remove(email);

            ServerController.addLogEntry(email.getMittente(), "Email eliminata", LocalDateTime.now().toString());
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            ServerController.addLogEntry("Server", "Errore durante l'eliminazione dell'email: " + e.getMessage(), LocalDateTime.now().toString());
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
