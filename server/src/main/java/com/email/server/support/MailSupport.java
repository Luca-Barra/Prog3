package com.email.server.support;

import com.email.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MailSupport {

    private static final Logger logger = Logger.getLogger(MailSupport.class.getName());

    public synchronized static List<Email> getEmails(String mailboxFileName) throws IOException {
        List<Email> emailList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                String[] parts = sb.toString().split(";");
                if (parts.length == 5) {
                    Email email = new Email(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    emailList.add(email);
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        }
        return emailList;
    }

    public static String getMailboxSent(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/SENT/" + user + ".txt";
    }

    public static String getMailboxReceived(String user){
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/server-mailbox/RECEIVED/" + user + ".txt";
    }

    public synchronized static void writeMail(String mailboxFileName, Email email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, true))) {
            writer.write(email.getMittente() + ";"
                    + email.getDestinatario() + ";"
                    + email.getOggetto() + ";"
                    + email.getTesto() + ";"
                    + email.getData() + "\n");
        } catch (IOException e) {
            logger.severe("Errore durante la scrittura dell'email: " + e.getMessage());
        }
    }

    public synchronized static void saveNewEmails(String mailboxFileNameReceived, List<Email> emailList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileNameReceived, true))) {
            for (Email email : emailList) {
                writer.write(email.getMittente() + ";"
                        + email.getDestinatario() + ";"
                        + email.getOggetto() + ";"
                        + email.getTesto() + ";"
                        + email.getData() + "\n");
            }
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio delle email: " + e.getMessage());
        }
    }

    public synchronized static void deleteEmail(String mailboxFileName, Email email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName)) ) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                String[] parts = sb.toString().split(";");
                if (parts.length == 5) {
                    Email emailInMailbox = new Email(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    if (!email.equals(emailInMailbox)) {
                        writeMail(mailboxFileName, emailInMailbox);
                    }
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        } catch (IOException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
        }
    }

}
