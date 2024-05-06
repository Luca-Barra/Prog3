package com.email.server.persistence;

import com.email.server.controller.ServerController;
import com.email.server.model.ServerModel;
import com.email.server.utils.LogEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerPersistence {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final Logger logger = Logger.getLogger(ServerModel.class.getName());

    /**
     * Metodo che inizializza il file di log.
     */

    public static void initializeLogs() {
        String filename = "server/src/main/resources/com/email/server/logs/server-logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
        try {
            System.out.println("Inizializzazione del file di log in corso...");
            Path path = Paths.get(filename);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write("\n---------------------------------------\n");
            writer.write("| Logs salvato il " + LocalDateTime.now().format(formatter) + " |");
            writer.write("\n---------------------------------------\n");
            writer.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio dei log: " + e.getMessage());
        }
    }

    /**
     * Metodo che salva sul file di log un log. Non è synchronized perché il metodo per aggiungere un log è già synchronized.
     * <p>
     * @param logEntry Il log da salvare.
     */

    public static void saveLogs(LogEntry logEntry){
        String filename = "server/src/main/resources/com/email/server/logs/server-logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
        try {
            System.out.println("Salvataggio dei log in corso...");
            Path path = Paths.get(filename);
            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write(logEntry.getUtente() + " - " + logEntry.getMessaggio() + " - " + logEntry.getData());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio dei log: " + e.getMessage());
        }
    }

    /**
     * Metodo che carica la lista degli utenti registrati
     * <p>
     * @return La lista degli utenti registrati al servizio.
     */

    public static List<String> loadRegisteredUsers() {
        List<String> registeredUsers = new ArrayList<>();
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
        return registeredUsers;
    }

}
