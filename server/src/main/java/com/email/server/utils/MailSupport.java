package com.email.server.utils;

import com.email.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class MailSupport {

    private static final Logger logger = Logger.getLogger(MailSupport.class.getName());
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();

    /**
     * Metodo per ottenere la lista di email in arrivo
     * <p>
     * @param mailboxFileName il nome della casella di posta
     * <p>
     * @return La lista delle email in arrivo
     * <p>
     * @throws IOException Eccezioni di input/output
     */

    public static List<Email> getEmails(String mailboxFileName) throws IOException {
        List<Email> emailList = new ArrayList<>();

        readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                String[] parts = sb.toString().split(";");
                if (parts.length == 6) {
                    Email email = new Email(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    emailList.add(email);
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        } finally {
            readLock.unlock();
        }
        return emailList;
    }

    /**
     * Metodo per ottenere l'indirizzo della casella di posta con le mail inviate, ma non ricevute
     * <p>
     * @param user L'utente di cui si vuole ottenere la casella di posta
     * <p>
     * @return L''indirizzo della casella di posta
     */

    public static String getMailboxSent(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/SENT/" + user + ".txt";
    }

    /**
     * Metodo per ottenere l'indirizzo della casella di posta con le mail ricevute
     * <p>
     * @param user L'utente di cui si vuole ottenere la casella di posta
     * <p>
     * @return L''indirizzo della casella di posta
     */

    public static String getMailboxReceived(String user){
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/RECEIVED/" + user + ".txt";
    }

    /**
     * Metodo per scrivere su file SENT una mail
     * <p>
     * @param mailboxFileName Il nome della casella di posta
     * @param email L'email
     */

    public static void writeMail(String mailboxFileName, Email email) {
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, true))) {
            writer.write(email.getMittente() + ";"
                    + email.getDestinatario() + ";"
                    + email.getOggetto() + ";"
                    + email.getTesto() + ";"
                    + email.getData() + ";" + email.getId() +
                    "\n");
        } catch (IOException e) {
            logger.severe("Errore durante la scrittura dell'email: " + e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Metodo per scrivere su file RECEIVED una mail
     * <p>
     * @param mailboxFileNameReceived Il nome della casella di posta
     * @param emailList La lista di email
     */

    public static void saveNewEmails(String mailboxFileNameReceived, List<Email> emailList) {
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileNameReceived, true))) {
            for (Email email : emailList) {
                writer.write(email.getMittente() + ";"
                        + email.getDestinatario() + ";"
                        + email.getOggetto() + ";"
                        + email.getTesto() + ";"
                        + email.getData() + ";"
                        + email.getId() +"\n");
            }
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio delle email: " + e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Metodo per cancellare definitivamente una mail da RECEIVED
     * <p>
     * @param mailboxFileName Il nome della casella di posta
     * @param email L'email da cancellare
     */

    public static void deleteEmail(String mailboxFileName, Email email) {
        List<Email> emailList = new ArrayList<>();
        readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    Email emailInMailbox = new Email(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    emailList.add(emailInMailbox);
                }
            }
        } catch (IOException e) {
            logger.severe("Errore durante la lettura dell'email: " + e.getMessage());
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, false))) {
            for (Email e : emailList) {
                if (!Objects.equals(e.getId(), email.getId())) {
                    writer.write(e.getMittente() + ";"
                            + e.getDestinatario() + ";"
                            + e.getOggetto() + ";"
                            + e.getTesto() + ";"
                            + e.getData() + ";"
                            + e.getId() + "\n");
                }
            }
        } catch (IOException e) {
            logger.severe("Errore durante la scrittura dell'email: " + e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }
}
