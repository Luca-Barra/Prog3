package com.email.server.model;

import com.email.server.persistence.ServerPersistence;
import com.email.server.utils.LogEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;


public class ServerModel {

    public static final ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();
    private static ArrayList<String> registeredUsers = new ArrayList<>();

    /**
     * Metodo che aggiunge un log alla lista dei log
     * <p>
     * @param logEntry Il log da aggiungere
     */
    
    public static synchronized void addLogEntry(LogEntry logEntry) {
        logEntries.add(logEntry);
        saveLogs(logEntry);
    }
    
    /**
     * Metodo che inizializza il file di log.
     */

    public static void initializeLogs() {ServerPersistence.initializeLogs();}

    /**
     * Metodo che salva sul file di log un log.
     * <p>
     * @param logEntry il log da salvare
     */

    public static void saveLogs(LogEntry logEntry) {ServerPersistence.saveLogs(logEntry);}

    /**
     * Metodo che carica la lista degli utenti registrati
     */

    public static void loadRegisteredUsers() {registeredUsers = (ArrayList<String>) ServerPersistence.loadRegisteredUsers();}

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