package com.email.client.models;
import com.email.client.utils.MyAlert;
import com.email.Email;
import com.email.client.utils.NewMailView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


public class ClientModel {
    private final ObservableList<Email> emailList;
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());
    private final String user;
    private final ReentrantLock lock = new ReentrantLock();

    public ClientModel(String username) {

        emailList = FXCollections.observableArrayList();

        user = username;

    }

    public synchronized void loadEmailsFromLocal(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                String[] parts = sb.toString().split(";");
                if (parts.length == 7) {
                    String sender = parts[0].trim();
                    String recipients = parts[1].trim();
                    String subject = parts[2].trim();
                    String body = parts[3].trim();
                    String data = parts[4].trim();
                    String read = parts[5].trim();
                    String id = parts[6].trim();
                    Email email = new Email(sender, recipients, subject, body, data, id);
                    emailList.add(email);
                    if(read.equals("READ")) {
                        email.setRead(true);
                    }
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
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
            for (int i = 0; i < 3; i++) {
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
                        out.writeObject(email);
                    }
                    serverResponse = in.readObject().toString();
                    System.out.println(serverResponse);
                    if(serverResponse.equals("Errore durante l'invio dell'email")) {
                        Platform.runLater(() -> MyAlert.error("Errore nell'invio dell'email", "Email non valida", "Inserire un indirizzo email valido."));
                    } else {
                        Platform.runLater(() -> {
                            saveEmailsToLocal();
                            refreshEmails();
                            MyAlert.info("Email inviata", "Email inviata con successo", "L'email è stata inviata con successo.");
                        });
                    }
                    in.close();
                    out.close();
                    socket.close();
                    break;
                } catch (ConnectException e) {
                    System.out.println("Impossibile connettersi al server.");
                    Platform.runLater(NewMailView::serverDown);
                } catch (IOException e) {
                    logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        executor.execute(task);
    }
    public String getUser() {
        return user;
    }

    public void deleteEmail(Email selectedEmail) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 12345), 30000);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject("DELETE_EMAIL");
                String serverResponse = in.readObject().toString();
                out.writeObject(selectedEmail);
                if(serverResponse.equals("OK")) {
                    System.out.println("Risposta dal server: " + serverResponse);
                    serverResponse = in.readObject().toString();
                    if(serverResponse.equals("Identificarsi")) {
                        out.flush();
                        out.writeObject(user);
                        Platform.runLater(() -> {
                            lock.lock();
                            emailList.remove(selectedEmail);
                            lock.unlock();
                            refreshEmails();
                            MyAlert.info("Email eliminata", "Email eliminata con successo", "L'email è stata eliminata con successo.");
                            saveEmailsToLocal();
                        });
                    }
                }
                if(serverResponse.equals("Errore durante l'eliminazione dell'email")) {
                    System.out.println("Errore durante l'eliminazione dell'email");
                    MyAlert.error("Errore nell'eliminazione dell'email", "Errore durante l'eliminazione dell'email", "Errore durante l'eliminazione dell'email.");
                }
                in.close();
                out.close();
                socket.close();
            } catch (ConnectException e) {
                System.out.println("Impossibile connettersi al server.");
                Platform.runLater(() ->
                MyAlert.error("Errore nell'eliminazione dell'email", "Impossibile connettersi al server", "Il server è down."));
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout di connessione al server.");
                MyAlert.error("Errore nell'eliminazione dell'email", "Impossibile connettersi al server", "Il server è down.");
            } catch (IOException e) {
                logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        executor.execute(task);
    }

    public void forwardEmail(Email email, String destinatari) {

        if(email.getDestinatario().isEmpty()) {
            MyAlert.error("Errore nell'inoltro dell'email", "Uno o più indirizzi email inseriti non sono validi.", "Inserire uno o più indirizzi email validi.");
        } else {
            for(String destinatario : destinatari.split(",")) {
                if(!Objects.equals(destinatario, user)) {
                    Email emailForwarded = new Email(email.getMittente(), email.getDestinatario(), email.getOggetto(), email.getTesto(), email.getData(), email.getId());
                    sendEmailV2(emailForwarded, destinatario);
                    refreshEmails();
                }
            }
        }
        
    }

    private void sendEmailV2(Email emailForwarded, String destinatario) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("localhost", 12345), 30000);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    out.writeObject("FORWARD_EMAIL");
                    String serverResponse = in.readObject().toString();
                    out.flush();
                    if(serverResponse.equals("OK")) {
                        System.out.println("Risposta dal server: " + serverResponse);
                        out.writeObject(emailForwarded);
                        serverResponse = in.readObject().toString();
                        System.out.println(serverResponse);
                        if(serverResponse.equals("A chi vuoi inoltrare l'email?")) {
                            out.flush();
                            out.writeObject(destinatario);
                        }
                    }
                    serverResponse = in.readObject().toString();
                    if(serverResponse.equals("Errore durante l'invio dell'email")) {
                        Platform.runLater(() -> MyAlert.error("Errore nell'invio dell'email", "Email non valida", "Inserire un indirizzo email valido."));
                    } else {
                        Platform.runLater(() -> {
                            saveEmailsToLocal();
                            refreshEmails();
                            MyAlert.info("Email inoltrata", "Email inoltrata con successo", "L'email è stata inoltrata con successo.");
                        });
                    }
                    in.close();
                    out.close();
                    socket.close();
                    break;
                } catch (ConnectException e) {
                    System.out.println("Impossibile connettersi al server.");
                    Platform.runLater(NewMailView::serverDown);
                } catch (IOException e) {
                    logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        executor.execute(task);
    }

    public void updateLocalMailboxPeriodically() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = this::refreshEmails;

        executor.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }

    public synchronized void saveEmailsToLocal() {
        System.out.println("Salvataggio su file locale");
        String filename = "/home/luna/IdeaProjects/Project-Prog-3/client/src/main/resources/com/email/client/local-mailbox/" + user + ".txt";
        try {
            BufferedWriter bw = getBufferedWriter(filename);
            bw.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio delle email su file locale: " + e.getMessage());
        }
    }

    private synchronized BufferedWriter getBufferedWriter(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for (Email email : emailList) {
            bw.write(email.getMittente() + ";");
            bw.write(email.getDestinatario() + ";");
            bw.write(email.getOggetto() + ";");
            bw.write(email.getTesto() + ";");
            bw.write(email.getData() + ";");
            if(email.isRead()) {
                bw.write("READ ;");
            } else {
                bw.write("UNREAD ;");
            }
            bw.write(email.getId() + "\n");
        }
        return bw;
    }

    public void refreshEmails() {
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
                Object receivedObject = in.readObject();
                if (receivedObject instanceof List<?> objectList) {
                    List<Email> emails = new ArrayList<>();
                    for (Object obj : objectList) {
                        if (obj instanceof Email) {
                            emails.add((Email) obj);
                        } else {
                            System.out.println("Il server ha inviato un oggetto non riconosciuto: " + obj);
                        }
                    }
                    Platform.runLater(() -> {
                        int size = emailList.size();
                        lock.lock();
                        emailList.addAll(emails);
                        lock.unlock();
                        System.out.println(emailList.size());
                        if (size != emailList.size()) {
                            saveEmailsToLocal();
                            MyAlert.info("Aggiornamento casella di posta", "Casella di posta aggiornata", "La casella di posta è stata aggiornata con successo.");
                        }
                    });
                } else {
                    System.out.println("Il server non ha inviato una lista.");
                }
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
    }

    public void markAsRead(Email email) {
        email.setRead(true);
        saveEmailsToLocal();
    }

}
