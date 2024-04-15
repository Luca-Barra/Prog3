package com.email.client;
import com.email.email.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class ClientModel {
    private final ObservableList<Email> emailList;
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private String user;

    public ClientModel(String username) {


        emailList = FXCollections.observableArrayList();

        user = username;

    }

    public void loadEmailsFromLocal(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    String sender = parts[0].trim();
                    String recipients = parts[1].trim();
                    String subject = parts[2].trim();
                    String body = parts[3].trim();
                    String data = parts[4].trim();
                    emailList.add(new Email(sender, recipients, subject, body, data));
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            logger.severe("Errore durante il caricamento delle email dal file locale: " + e.getMessage());
        }
    }

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    public void sendEmail(Email email) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 12345), 30000);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject("SEND_EMAIL");
                String serverResponse = in.readObject().toString();
                out.flush();
                if(serverResponse.equals("OK")) {
                    System.out.println("Risposta dal server: " + serverResponse);
                    Platform.runLater(() -> emailList.add(email));
                    out.writeObject(email);
                }
                serverResponse = in.readObject().toString();
                if(serverResponse.equals("Errore durante l'invio dell'email")) {
                    System.out.println(serverResponse);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Errore");
                        alert.setHeaderText("Email non valida");
                        alert.setContentText("Inserire un indirizzo email valido.");
                        alert.showAndWait();
                    });
                } else {
                    System.out.println(serverResponse);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Email inviata");
                        alert.setHeaderText("Email inviata con successo");
                        alert.setContentText("L'email è stata inviata con successo.");
                        alert.showAndWait();
                    });
                }
                in.close();
                out.close();
                socket.close();
            } catch (ConnectException e) {
                System.out.println("Impossibile connettersi al server.");
                Platform.runLater(NewMailview::serverDown);

            } catch (IOException e) {
                logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        executor.execute(task);
    }

    public void setUser(String username) {
        this.user = username;
    }

    public String getUser() {
        return user;
    }

    public void deleteEmail(Email selectedEmail) {
        synchronized (emailList) {
            System.out.println("eliminazione");
            emailList.remove(selectedEmail);
            emailList.notifyAll();
        }
    }

    public void forwardEmail(Email email) {
        Email forwardedEmail = dataEmail(email);
        sendEmail(forwardedEmail);
    }

    private Email dataEmail(Email email) {
        return new Email(email.getMittente(), email.getDestinatario(), email.getOggetto(), email.getTesto(), email.getData());
    }

    public void updateLocalMailboxPeriodically(String filepath) {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 12345), 30000);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject("RETRIEVE_EMAILS");
                String serverResponse = in.readObject().toString();
                out.writeObject(user);
                if(serverResponse.equals("OK")) {
                    System.out.println("Risposta dal server: " + serverResponse);
                    List<Email> emails = (ArrayList<Email>) in.readObject();
                    Platform.runLater(() -> {
                        emailList.addAll(emails);
                        saveEmailsToLocal(filepath);
                    });
                }
                if(serverResponse.equals("Errore durante il recupero delle email")) {
                    System.out.println("Errore durante il recupero delle email");
                }
                in.close();
                out.close();
                socket.close();
            } catch (ConnectException e) {
                System.out.println("Impossibile connettersi al server.");
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout di connessione al server.");
            } catch (IOException e) {
                logger.severe("Errore durante il recupero delle email: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }


    public void saveEmailsToLocal(String s) {
        System.out.println("Salvataggio su file locale");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(s))) {
            for (Email email : emailList) {
                writer.write(email.getMittente() + ";"
                        + email.getDestinatario() + ";"
                        + email.getOggetto() + ";"
                        + email.getTesto() + ";"
                        + email.getData() + "\n");
            }
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio delle email su file locale: " + e.getMessage());
        }
    }
}
