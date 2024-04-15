package com.email.email;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Email implements Serializable {
    private String mittente;
    private String destinatario;
    private String oggetto;
    private String testo;
    private String data;
    private boolean isRead = false;

    public Email(String mittente, String destinatario, String oggetto, String testo, String data) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.testo = testo;
        this.data = data;
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

    public List<String> getDestinatari() {
        // Dividi la stringa dei destinatari utilizzando la virgola come delimitatore
        String[] destinatariArray = destinatario.split(",");

        // Rimuovi eventuali spazi bianchi in eccesso dai destinatari
        for (int i = 0; i < destinatariArray.length; i++) {
            destinatariArray[i] = destinatariArray[i].trim();
        }

        // Converti l'array in una lista e restituisci
        return new ArrayList<>(Arrays.asList(destinatariArray));
    }
}