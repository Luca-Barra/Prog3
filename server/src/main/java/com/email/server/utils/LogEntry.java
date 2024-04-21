package com.email.server.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty utente;
    private final StringProperty messaggio;
    private final StringProperty data;

    /**
     * Costruttore della classe LogEntry
     * <p>
     * @param utente il server o il nome del client
     * @param messaggio il messaggio di stato
     * @param data la data in cui è stato elaborato il messaggio
     */

    public LogEntry(String utente, String messaggio, String data) {
        this.utente = new SimpleStringProperty(utente);
        this.messaggio = new SimpleStringProperty(messaggio);
        this.data = new SimpleStringProperty(data);
    }

    /**
     * Metodo per ottenere l'utente
     * <p>
     * @return l'utente
     */

    public String getUtente() {
        return utente.get();
    }

    /**
     * Metodo per ottenere il messaggio
     * <p>
     * @return il messaggio
     */

    public String getMessaggio() {
        return messaggio.get();
    }

    /**
     * Metodo per ottenere la data
     * <p>
     * @return la data
     */

    public String getData() {
        return data.get();
    }

}

