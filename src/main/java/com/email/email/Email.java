package com.email.email;
import java.io.Serializable;

public class Email implements Serializable {
    private final String mittente;
    private final String destinatario;
    private final String oggetto;
    private final String testo;
    private final String data;
    private boolean isRead = false;

    public Email(String mittente, String destinatario, String oggetto, String testo, String data) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.testo = testo;
        this.data = data;
    }

    public String getMittente() {
        return mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getOggetto() {
        return oggetto;
    }

    public String getTesto() {
        return testo;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return  testo;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

}