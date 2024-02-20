package com.email.client;
import com.email.email.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientModel {
    private final ObservableList<Email> emailList;
    private String user;

    public ClientModel() {

        emailList = FXCollections.observableArrayList();

        loadEmailsFromServer();
    }

    private void loadEmailsFromServer() {
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com, destinatario2@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente6@example.com", "destinatario@example.com", "Oggetto 3", "Arefdddddddddddddddddd" +
                "fdddddddddddfdddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "rwewerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                "3e4rrtrtrtrtrtrtrtrtrtrtrttsdddddddddddddddd" +
                "sdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "sssssssssssssssssssssss3ewwerweerer" +
                "erer4e"));
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
                    out.writeObject(email);
                    System.out.println("2sdsd");
                    try {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        Object serverResponse = in.readObject();
                        System.out.println("Risposta dal server: " + serverResponse);
                    } catch (SocketTimeoutException e) {
                        System.out.println("Nessuna risposta dal server entro 30 secondi.");
                    }
                    socket.close();
                } catch (ConnectException e) {
                    System.out.println("Impossibile connettersi al server.");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
        };

            executor.schedule(task, 30, TimeUnit.SECONDS);

        emailList.add(email);
    }

    public void setUser(String username) {
        this.user = username;
    }

    public String getUser() {
        return user;
    }

    public void deleteEmail(Email selectedEmail) {
        synchronized (emailList) {
            System.out.println("elim");
            emailList.remove(selectedEmail);
            emailList.notifyAll();
        }
    }

    public void sendEmailToAll(String destinatario, String mittente, String oggetto, String testo) {
    }

    public void forwardEmail(Email email, List<String> newRecipients) {
        Email forwardedEmail = new Email(email.getMittente(), String.join(",", newRecipients), email.getOggetto(), email.getTesto());

        sendEmail(forwardedEmail);
    }
}
