package com.email.server.support;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty utente;
    private final StringProperty messaggio;
    private final StringProperty data;

    public LogEntry(String utente, String messaggio, String data) {
        this.utente = new SimpleStringProperty(utente);
        this.messaggio = new SimpleStringProperty(messaggio);
        this.data = new SimpleStringProperty(data);
    }

    public String getUtente() {
        return utente.get();
    }

    public String getMessaggio() {
        return messaggio.get();
    }

    public String getData() {
        return data.get();
    }

}
