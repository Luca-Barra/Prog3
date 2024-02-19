package com.email.email;
import java.time.LocalDateTime;

public class Email {
    private String mittente;
    private String destinatario;
    private String oggetto;
    private String testo;
    private LocalDateTime data;

    public Email(String mittente, String destinatario, String oggetto, String testo) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.testo = testo;
        this.data = LocalDateTime.now();
    }

    // Metodi getter
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
        return data.toString();
    }
}