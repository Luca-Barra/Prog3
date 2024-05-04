package com.email.server.handler;
import com.email.Email;
import com.email.server.utils.LogEntry;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static com.email.server.model.ServerModel.addLogEntry;
import static com.email.server.model.ServerModel.checkUser;
import static com.email.server.utils.MailSupport.*;

public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Costruttore della classe ClientHandler
     * <p>
     * @param clientSocket Socket del client
     */

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Metodo run
     * <p>
     * Gestisce la comunicazione con il client
     */

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
                    if (checkUser(username))
                        addLogEntry(new LogEntry(username, "Tentativo di login", LocalDateTime.now().format(formatter)));
                    else
                        addLogEntry(new LogEntry("Sconosciuto", "Tentativo di login fallito: utente non esistente", LocalDateTime.now().format(formatter)));
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
            addLogEntry(new LogEntry("Server", "Comando " + command + " gestito con successo", LocalDateTime.now().format(formatter)));
            addLogEntry(new LogEntry("Server", "Chiusura della connessione con " + clientSocket, LocalDateTime.now().format(formatter)));
        } catch (IOException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante la comunicazione con il client: " + e.getMessage(), LocalDateTime.now().format(formatter)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo caseSendEmail
     * <p>
     * Invia un'email
     * <p>
     * @param in Stream di input
     * @return true se l'email è stata inviata con successo, false altrimenti
     */

    private static synchronized boolean caseSendEmail(ObjectInputStream in) {
        try {
            Email email = (Email) in.readObject();
            String mailboxFileName;
            boolean sent = false;

            String[] destinatari = email.getDestinatario().split(",");

            for (String destinatario : destinatari) {
                if(!destinatario.trim().equals(email.getMittente())) {
                    if (checkUser(destinatario.trim())) {
                        sent = true;
                        mailboxFileName = getMailboxSent(destinatario.trim());
                        writeMail(mailboxFileName, email);
                        addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " inviata a " + destinatario.trim(), LocalDateTime.now().format(formatter)));
                    } else {
                        addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() +" non inviata a " + destinatario.trim() + ": utente non esistente", LocalDateTime.now().format(formatter)));
                        return false;
                    }
                }
            }
            if(sent)
                writeMail(getMailboxSent(email.getMittente()), email);

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante l'invio dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter)));
            return false;
        }
        return true;
    }

    /**
     * Metodo caseForwardEmail
     * <p>
     * Inoltra un'email
     * <p>
     * @param in Stream di input
     * @param out Stream di output
     * @return true se l'email è stata inoltrata con successo, false altrimenti
     */

    private boolean caseForwardEmail(ObjectInputStream in, ObjectOutputStream out) {
        try {
            Email email = (Email) in.readObject();
            out.writeObject("A chi vuoi inoltrare l'email?");
            String destinatario = (String) in.readObject();

            for(String dest : destinatario.split(",")) {
                if(!checkUser(dest.trim())) {
                    addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " non inoltrata a " + dest.trim() + ": utente non esistente", LocalDateTime.now().format(formatter)));
                    return false;
                } else {
                    String mailboxFileName = getMailboxSent(dest.trim());
                    writeMail(mailboxFileName, email);
                    addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " inoltrata a " + dest.trim(), LocalDateTime.now().format(formatter)));
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'inoltro dell'email: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante l'inoltro dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter)));
            return false;
        }
        return true;
    }

    /**
     * Metodo caseRetrieveEmails
     * <p>
     * Recupera le email
     * <p>
     * @param in Stream di input
     * @param out Stream di output
     */

    private static void caseRetrieveEmails(ObjectInputStream in, ObjectOutputStream out) {
        try {
            String username = (String) in.readObject();
            String mailboxFileName = getMailboxSent(username);
            String mailboxFileNameReceived = getMailboxReceived(username);
            List<Email> emailList = getEmails(mailboxFileName);
            saveNewEmails(mailboxFileNameReceived, emailList);

            out.writeObject(emailList);
            addLogEntry(new LogEntry(username, "Aggiornamento della casella di posta", LocalDateTime.now().format(formatter)));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName))) {
                writer.write("");
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante il recupero delle email: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante il recupero delle email: " + e.getMessage(), LocalDateTime.now().format(formatter)));
        }
    }

    /**
     * Metodo caseDeleteEmail
     * <p>
     * Elimina un'email
     * <p>
     * @param in Stream di input
     * @param out Stream di output
     */

    private static void caseDeleteEmail(ObjectInputStream in, ObjectOutputStream out) {
        try {
            Email email = (Email) in.readObject();
            System.out.println("Email da eliminare: " + email);

            out.writeObject("Identificarsi");

            String username = (String) in.readObject();

            String mailboxFileName = getMailboxReceived(username);
            deleteEmail(mailboxFileName, email);

            addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " eliminata", LocalDateTime.now().format(formatter)));
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante l'eliminazione dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter)));
        }

    }
}