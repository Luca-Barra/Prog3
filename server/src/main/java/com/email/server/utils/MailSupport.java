package com.email.server.utils;

import com.email.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class MailSupport {

    private static final Logger logger = Logger.getLogger(MailSupport.class.getName());

    /**
     * Metodo per leggere le email presenti nel file mailbox
     * <p>
     * @param mailboxFileName nome del file mailbox
     * @return lista di email presenti nel file mailbox
     * @throws IOException se si verifica un errore durante la lettura del file
     */

    public synchronized static List<Email> getEmails(String mailboxFileName) throws IOException {
        List<Email> emailList = new ArrayList<>();

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
        }
        return emailList;
    }

    /**
     * Metodo per ottenere il percorso del file mailbox SENT
     * <p>
     * @param user nome dell'utente
     * @return percorso del file mailbox SENT
     */

    public static String getMailboxSent(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/SENT/" + user + ".txt";
    }

    /**
     * Metodo per ottenere il percorso del file mailbox RECEIVED
     * <p>
     * @param user nome dell'utente
     * @return percorso del file mailbox RECEIVED
     */

    public static String getMailboxReceived(String user){
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/RECEIVED/" + user + ".txt";
    }

    public synchronized static void writeMail(String mailboxFileName, Email email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, true))) {
            writer.write(email.getMittente() + ";"
                    + email.getDestinatario() + ";"
                    + email.getOggetto() + ";"
                    + email.getTesto() + ";"
                    + email.getData() + ";" + email.getId() +
                    "\n");
        } catch (IOException e) {
            logger.severe("Errore durante la scrittura dell'email: " + e.getMessage());
        }
    }

    /**
     * Metodo per salvare le nuove email ricevute nel file mailbox RECEIVED
     * <p>
     * @param mailboxFileNameReceived nome del file mailbox RECEIVED
     * @param emailList lista di email da salvare
     */

    public synchronized static void saveNewEmails(String mailboxFileNameReceived, List<Email> emailList) {
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
        }
    }

    /**
     * Metodo per eliminare un'email dal file mailbox
     * <p>
     * @param mailboxFileName nome del file mailbox
     * @param email email da eliminare
     */

    public synchronized static void deleteEmail(String mailboxFileName, Email email) {
        List<Email> emailList = new ArrayList<>();
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
        }

        emailList.removeIf(e -> Objects.equals(e.getId(), email.getId()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, false))) {
            for (Email e : emailList) {
                writer.write(e.getMittente() + ";"
                        + e.getDestinatario() + ";"
                        + e.getOggetto() + ";"
                        + e.getTesto() + ";"
                        + e.getData() + ";"
                        + e.getId() + "\n");
            }
        } catch (IOException e) {
            logger.severe("Errore durante la scrittura dell'email: " + e.getMessage());
        }
    }

}
