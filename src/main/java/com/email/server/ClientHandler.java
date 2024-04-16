package com.email.server;
import com.email.email.Email;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import static com.email.server.support.MailSupport.getEmails;
import static com.email.server.support.MailSupport.getMailboxFileName;


public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;

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
                case "SEND_EMAIL":
                    if(caseSendEmail(in))
                        out.writeObject("Email inviata con successo");
                    else
                        out.writeObject("Errore durante l'invio dell'email");
                    break;
                case "RETRIEVE_EMAILS":
                    caseRetrieveEmails(in, out);
                    break;
                case "DELETE_EMAIL":
                    caseDeleteEmail(in);
                    break;
                default:
                    System.out.println("Comando non riconosciuto: " + command);
            }
            ServerModel.addLogEntry("Server", "Comando " + command + " gestito con successo", LocalDateTime.now().toString());
            ServerModel.addLogEntry("Server", "Chiusura della connessione con " + clientSocket, LocalDateTime.now().toString());
        } catch (IOException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante la comunicazione con il client: " + e.getMessage(), LocalDateTime.now().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void caseRetrieveEmails(ObjectInputStream in, ObjectOutputStream out) {
        try {
            String username = (String) in.readObject();
            String mailboxFileName = getMailboxFileName(username);
            List<Email> emailList = getEmails(mailboxFileName);

            out.writeObject(emailList);
            ServerModel.addLogEntry(username, "Aggiornamento della casella di posta", LocalDateTime.now().toString());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName))) {
                writer.write("");
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante il recupero delle email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante il recupero delle email: " + e.getMessage(), LocalDateTime.now().toString());
        }
    }

    private static synchronized boolean caseSendEmail(ObjectInputStream in) {
        try {
            Email email = (Email) in.readObject();
            System.out.println("Email ricevuta: " + email);

            String[] destinatari = email.getDestinatario().split(",");

            for (String destinatario : destinatari) {
                if(ServerModel.checkUser(destinatario.trim())) {

                    String mailboxFileName = getMailboxFileName(destinatario.trim());
                    if(!email.getMittente().equals(email.getDestinatario())){
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(mailboxFileName, true))) {
                            writer.write(email.getMittente() + ";"
                                    + email.getDestinatario() + ";"
                                    + email.getOggetto() + ";"
                                    + email.getTesto() + ";"
                                    + email.getData() + "\n");
                        }
                        ServerModel.addLogEntry(email.getMittente(), "Email inviata a " + destinatario.trim(), LocalDateTime.now().toString());
                    }
                } else {
                    ServerModel.addLogEntry(email.getMittente(), "Email non inviata a " + destinatario.trim() + ": utente non esistente", LocalDateTime.now().toString());
                    return false;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'invio dell'email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante l'invio dell'email: " + e.getMessage(), LocalDateTime.now().toString());
            return false;
        }
        return true;
    }

    private static void caseDeleteEmail(ObjectInputStream in) {
        try {
            Email email = (Email) in.readObject();
            System.out.println("Email da eliminare: " + email);

            String mailboxFileName = getMailboxFileName(email.getDestinatario());
            List<Email> emailList = getEmails(mailboxFileName);
            emailList.remove(email);

            ServerModel.addLogEntry(email.getMittente(), "Email eliminata", LocalDateTime.now().toString());
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante l'eliminazione dell'email: " + e.getMessage());
            ServerModel.addLogEntry("Server", "Errore durante l'eliminazione dell'email: " + e.getMessage(), LocalDateTime.now().toString());
        }

    }
}
