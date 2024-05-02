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
        ServerPersistence.saveLogs(logEntries);}

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