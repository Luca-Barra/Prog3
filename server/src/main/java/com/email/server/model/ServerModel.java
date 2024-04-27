package com.email.server.model;

import com.email.server.controller.ServerController;
import com.email.server.utils.LogEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerModel {

    public static final ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();
    private static final Logger logger = Logger.getLogger(ServerModel.class.getName());
    private static final ArrayList<String> registeredUsers = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Metodo che aggiunge un log alla lista dei log
     * <p>
     * @param utente utente che ha inviato il messaggio
     * @param messaggio messaggio inviato
     * @param data data e ora dell'invio del messaggio
     */
    
    public static synchronized void addLogEntry(String utente, String messaggio, String data) {
        logEntries.add(new LogEntry(utente, messaggio, data));
    }
    
    /**
     * Metodo che salva i log in un file .txt
     */

    public static void saveLogs() {
        String filename = "server/src/main/resources/com/email/server/logs/server-logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
        try {
            System.out.println("Salvataggio dei log in corso...");
            Path path = Paths.get(filename);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write("\n---------------------------------------\n");
            writer.write("| Logs salvato il " + LocalDateTime.now().format(formatter) + " |");
            writer.write("\n---------------------------------------\n");
            for (LogEntry logEntry : logEntries) {
                writer.write(logEntry.getUtente() + ";" + logEntry.getMessaggio() + ";" + logEntry.getData());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio dei log: " + e.getMessage());
        }
    }

    /**
     * Metodo che carica la lista degli utenti registrati
     */

    public static void loadRegisteredUsers() {
        try(InputStream inputStream = ServerController.class.getResourceAsStream("/com/email/server/data/registeredUsers.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        registeredUsers.add(line);
                    }
                }
            } else {
                logger.severe("registeredUsers.txt non trovato o non accessibile");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura di registeredUsers.txt", e);
        }

    }

    /**
     * Metodo che controlla se un utente è registrato
     * <p>
     * @param user utente da controllare
     * @return true se l'utente è registrato, false altrimenti
     */

    public static boolean checkUser(String user){
        System.out.println("User: " + user);
        return registeredUsers.contains(user);
    }
    
}