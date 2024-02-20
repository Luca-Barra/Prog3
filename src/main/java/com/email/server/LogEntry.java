package com.email.server;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty utente;
    private final StringProperty messaggio;

    public LogEntry(String utente, String messaggio) {
        this.utente = new SimpleStringProperty(utente);
        this.messaggio = new SimpleStringProperty(messaggio);
    }

    public String getUtente() {
        return utente.get();
    }

    public StringProperty utenteProperty() {
        return utente;
    }

    public String getMessaggio() {
        return messaggio.get();
    }

    public StringProperty messaggioProperty() {
        return messaggio;
    }
}

