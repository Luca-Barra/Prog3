package com.email.server.handler;
import com.email.Email;

import com.email.server.connection.ServerConnection;
import com.email.server.utils.LogEntry;

import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static com.email.server.model.ServerModel.addLogEntry;
import static com.email.server.model.ServerModel.checkUser;
import static com.email.server.utils.MailSupport.*;

public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final ServerConnection clientSocket;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Costruttore della classe ClientHandler
     * <p>
     * @param clientSocket Socket del client
     */

    public ClientHandler(ServerConnection clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Metodo run
     * <p>
     * Gestisce la comunicazione con il client
     */

    public void run() {

        try {

            String command = clientSocket.receiveFromClient().toString();

            System.out.println("Comando ricevuto: " + command);
            clientSocket.sendToClient("OK");
            System.out.println("OK inviato");

            switch (command) {
                case "LOGIN":
                    String username = clientSocket.receiveFromClient().toString();
                    System.out.println("User: " + username + "sta tentando di accedere al sistema");
                    if (checkUser(username))
                        addLogEntry(new LogEntry(username, "Tentativo di login", LocalDateTime.now().format(formatter)));
                    else
                        addLogEntry(new LogEntry("Sconosciuto", "Tentativo di login fallito: utente non esistente", LocalDateTime.now().format(formatter)));
                    break;
                case "SEND_EMAIL":
                    if (caseSendEmail())
                        clientSocket.sendToClient("Email inviata con successo");
                    else
                        clientSocket.sendToClient("Errore durante l'invio dell'email");
                    break;
                case "FORWARD_EMAIL":
                    if (caseForwardEmail())
                        clientSocket.sendToClient("Email inoltrata con successo");
                    else
                        clientSocket.sendToClient("Errore durante l'inoltro dell'email");
                    break;
                case "RETRIEVE_EMAILS":
                    caseRetrieveEmails();
                    break;
                case "DELETE_EMAIL":
                    caseDeleteEmail();
                    break;
                default:
                    System.out.println("Comando non riconosciuto: " + command);
            }
            addLogEntry(new LogEntry("Server", "Comando " + command + " gestito con successo", LocalDateTime.now().format(formatter)));
            System.out.println("Comando " + command + " gestito con successo");
            addLogEntry(new LogEntry("Server", "Chiusura della connessione con " + clientSocket, LocalDateTime.now().format(formatter)));
            System.out.println("Chiusura della connessione con " + clientSocket);
        } catch (IOException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante la comunicazione con il client: " + e.getMessage(), LocalDateTime.now().format(formatter)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                logger.severe("Errore durante la chiusura degli stream: " + e.getMessage());
                addLogEntry(new LogEntry("Server", "Errore durante la chiusura degli stream: " + e.getMessage(), LocalDateTime.now().format(formatter)));
            }
        }
    }


    /**
     * Metodo caseSendEmail
     * <p>
     * Invia un'email
     * <p>
     * @return true se l'email è stata inviata con successo, false altrimenti
     */

    private synchronized boolean caseSendEmail() {
        try {
            Email email = (Email) clientSocket.receiveFromClient();
            String mailboxFileName;
            boolean sent = false;
            boolean allValid = true;

            String[] destinatari = email.getDestinatario().split(",");

            System.out.println("Invio della mail: " + email.getId() + " a " + email.getDestinatario() + " in corso...");

            for (String destinatario : destinatari) {
                    if (!checkUser(destinatario.trim())) {
                        allValid = false;
                    }
            }

            for (String destinatario : destinatari) {
                if(allValid) {
                    sent = true;
                    if (!destinatario.trim().equals(email.getMittente())) {
                        mailboxFileName = getMailboxSent(destinatario.trim());
                        writeMail(mailboxFileName, email);
                        addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " inviata a " + destinatario.trim(), LocalDateTime.now().format(formatter)));
                        System.out.println("Email: " + email.getId() + " inviata a " + destinatario.trim());
                    }
                }
            }
            if(sent)
                writeMail(getMailboxSent(email.getMittente()), email);
            else
                return false;

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
     * @return true se l'email è stata inoltrata con successo, false altrimenti
     */

    private boolean caseForwardEmail() {
        try {
            Email email = (Email) clientSocket.receiveFromClient();
            System.out.println("Inoltro della mail: " + email.getId() + " in corso...");
            clientSocket.sendToClient("A chi vuoi inoltrare l'email?");
            System.out.println("A chi vuoi inoltrare l'email?");
            String destinatario = (String) clientSocket.receiveFromClient();
            System.out.println("Destinatario: " + destinatario);

            for(String dest : destinatario.split(",")) {
                if(!checkUser(dest.trim())) {
                    System.out.println("Utente non esistente");
                    addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " non inoltrata a " + dest.trim() + ": utente non esistente", LocalDateTime.now().format(formatter)));
                    return false;
                } else {
                    System.out.println("Utente esistente");
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
     */

    private void caseRetrieveEmails() {
        try {
            String username = (String) clientSocket.receiveFromClient();
            System.out.println("User: " + username);
            String mailboxFileName = getMailboxSent(username);
            String mailboxFileNameReceived = getMailboxReceived(username);
            List<Email> emailList = getEmails(mailboxFileName);
            saveNewEmails(mailboxFileNameReceived, emailList);
            clientSocket.sendToClient(emailList);
            addLogEntry(new LogEntry(username, "Aggiornamento della casella di posta", LocalDateTime.now().format(formatter)));
            System.out.println("Email inviate: " + emailList.size());
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
     */

    private void caseDeleteEmail() {
        try {
            Email email = (Email) clientSocket.receiveFromClient();
            System.out.println("Email da eliminare: " + email.getId());

            clientSocket.sendToClient("Identificarsi");
            System.out.println("Identificazione richiesta");

            String username = (String) clientSocket.receiveFromClient();

            System.out.println("User: " + username);

            String mailboxFileName = getMailboxReceived(username);
            deleteEmail(mailboxFileName, email);

            addLogEntry(new LogEntry(email.getMittente(), "Email " + email.getId() + " eliminata", LocalDateTime.now().format(formatter)));
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            addLogEntry(new LogEntry("Server", "Errore durante l'eliminazione dell'email: " + e.getMessage(), LocalDateTime.now().format(formatter)));
        }

    }
}