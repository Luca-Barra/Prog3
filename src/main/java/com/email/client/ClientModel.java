package com.email.client;
import com.email.email.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
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
            e.printStackTrace();
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
        Email forwardedEmail = new Email(email.getMittente(), String.join(",", newRecipients), email.getOggetto(), email.getTesto(), email.getData());

        sendEmail(forwardedEmail);
    }

    public void updateLocalMailboxPeriodically(String filepath) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        System.out.println("2323232323");
        Runnable task = () -> {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 12345), 30000);
                System.out.println("ferreer");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("erfferefererererer");
                out.writeObject(user);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                System.out.println("ferreer");
                in.readLine();
                System.out.println("jddjdfjdd");

                out.writeObject("GET_MAILBOX_FILE");


                String line;
                try (BufferedWriter localMailboxWriter = new BufferedWriter(new FileWriter(filepath))) {
                    while ((line = in.readLine()) != null) {
                        localMailboxWriter.write(line + "\n");
                    }
                }

            } catch (IOException e) {

                System.out.println("Impossibile connettersi al server. Verifica che sia in esecuzione.");
            }
        };

        executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }


    public void saveEmailsToLocal(String s) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(s))) {
            for (Email email : emailList) {
                writer.write(email.getMittente() + ";" + email.getDestinatario() + ";" + email.getOggetto() + ";" + email.getTesto() + ";" + email.getData() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
