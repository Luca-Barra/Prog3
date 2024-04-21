package com.email.server;
import com.email.Email;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static com.email.server.support.MailSupport.*;

public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String command = in.readObject().toString();

            System.out.println("Comando ricevuto: " + command);

            out.writeObject("OK");

            switch (command) {
                case "LOGIN":
                    String username = (String) in.readObject();
                    if (ServerModel.checkUser(username))
                        ServerModel.addLogEntry(username, "Tentativo di login", LocalDateTime.now().format(formatter));
                    else
                        ServerModel.addLogEntry("Sconosciuto", "Tentativo di login fallito: utente non esistente", LocalDateTime.now().format(formatter));
                    break;
                case "SEND_EMAIL":
                    if (caseSendEmail(in))
                        out.writeObject("Email inviata con successo");
                    else
                        out.writeObject("Errore durante l'invio dell'email");
                    break;
                case "FORWARD_EMAIL":
                    if (caseForwardEmail(in, out))
                        out.writeObject("Email inoltrata con successo");
                    else
                        out.writeObject("Errore durante l'inoltro dell'email");
                    break;
                case "RETRIEVE_EMAILS":
                    caseRetrieveEmails(in, out);
                    break;
                case "DELETE_EMAIL":
                    caseDeleteEmail(in, out);
                    break;
                default:
                    System.out.println("Comando non riconosciuto: " + command);
            }
            ServerModel.addLogEntry("Server", "Comando " + command + " gestito con successo", LocalDateTime.now().format(formatter));
            ServerModel.addLogEntry("Server", "Chiusura della connessione con " + clientSocket, LocalDateTime.now().format(formatter));
        } catch (IOException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante la comunicazione con il client: " + e.getMessage(), LocalDateTime.now().format(formatter));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized boolean caseSendEmail(ObjectInputStream in) {
        try {
            Email email = (Email) in.readObject();
            String mailboxFileName = getMailboxSent(email.getMittente());
            writeMail(mailboxFileName, email);
            System.out.println("Email ricevuta: " + email);
            String[] destinatari = email.getDestinatario().split(",");

            for (String destinatario : destinatari) {
                if(!destinatario.trim().equals(email.getMittente())) {
                    if (ServerModel.checkUser(destinatario.trim())) {
                        mailboxFileName = getMailboxSent(destinatario.trim());
                        writeMail(mailboxFileName, email);
                        ServerModel.addLogEntry(email.getMittente(), "Email " + email.getId() + " inviata a " + destinatario.trim(), LocalDateTime.now().format(formatter));
                    } else {
                        ServerModel.addLogEntry(email.getMittente(), "Email " + email.getId() +" non inviata a " + destinatario.trim() + ": utente non esistente", LocalDateTime.now().format(formatter));
                        return false;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante l'invio dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter));
            return false;
        }
        return true;
    }

    private boolean caseForwardEmail(ObjectInputStream in, ObjectOutputStream out) {
        try {
            Email email = (Email) in.readObject();
            out.writeObject("A chi vuoi inoltrare l'email?");
            String destinatario = (String) in.readObject();

            for(String dest : destinatario.split(",")) {
                if(!ServerModel.checkUser(dest.trim())) {
                    ServerModel.addLogEntry(email.getMittente(), "Email " + email.getId() + " non inoltrata a " + dest.trim() + ": utente non esistente", LocalDateTime.now().format(formatter));
                    return false;
                } else {
                    String mailboxFileName = getMailboxSent(dest.trim());
                    writeMail(mailboxFileName, email);
                    ServerModel.addLogEntry(email.getMittente(), "Email " + email.getId() + " inoltrata a " + dest.trim(), LocalDateTime.now().format(formatter));
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'inoltro dell'email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante l'inoltro dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter));
            return false;
        }
        return true;
    }

    private static void caseRetrieveEmails(ObjectInputStream in, ObjectOutputStream out) {
        try {
            String username = (String) in.readObject();
            String mailboxFileName = getMailboxSent(username);
            String mailboxFileNameReceived = getMailboxReceived(username);
            List<Email> emailList = getEmails(mailboxFileName);
            saveNewEmails(mailboxFileNameReceived, emailList);

            out.writeObject(emailList);
            ServerModel.addLogEntry(username, "Aggiornamento della casella di posta", LocalDateTime.now().format(formatter));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName))) {
                writer.write("");
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante il recupero delle email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante il recupero delle email: " + e.getMessage(), LocalDateTime.now().format(formatter));
        }
    }

    private static void caseDeleteEmail(ObjectInputStream in, ObjectOutputStream out) {
        try {
            Email email = (Email) in.readObject();
            System.out.println("Email da eliminare: " + email);

            out.writeObject("Identificarsi");

            String username = (String) in.readObject();

            String mailboxFileName = getMailboxReceived(username);
            deleteEmail(mailboxFileName, email);

            ServerModel.addLogEntry(email.getMittente(), "Email " + email.getId() + " eliminata", LocalDateTime.now().format(formatter));
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante l'eliminazione dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter));
        }

    }
}
