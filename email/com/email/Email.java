package com.email;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Email implements Serializable {
    private String id;
    private final String mittente;
    private final String destinatario;
    private final String oggetto;
    private final String testo;
    private final String data;
    private boolean isRead = false;

    /**
     * Costruttore della classe Email
     * <p>
     * @param mittente mittente dell'email
     * @param destinatario destinatario dell'email
     * @param oggetto oggetto dell'email
     * @param testo testo dell'email
     * @param data data di invio dell'email
     */

    public Email(String mittente, String destinatario, String oggetto, String testo, String data, String id) {
        generateId(id);
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.oggetto = oggetto;
        this.testo = testo;
        this.data = data;
    }

    /**
     * Metodo per ottenere l'id dell'email
     * <p>
     * @return id dell'email
     */

    public String getId() {return id;}

    /**
     * Metodo per ottenere il mittente dell'email
     * <p>
     * @return mittente dell'email
     */

    public String getMittente() {return mittente;}

    /**
     * Metodo per ottenere il destinatario dell'email
     * <p>
     * @return destinatario dell'email
     */

    public String getDestinatario() {return destinatario;}

    /**
     * Metodo per ottenere l'oggetto dell'email
     * <p>
     * @return oggetto dell'email
     */

    public String getOggetto() {return oggetto;}

    /**
     * Metodo per ottenere il testo dell'email
     * <p>
     * @return testo dell'email
     */

    public String getTesto() {
        return testo;
    }

    /**
     * Metodo per ottenere la data di invio dell'email
     * <p>
     * @return data di invio dell'email
     */

    public String getData() {return data;}

    /**
     * Metodo per ottenere la rappresentazione testuale dell'email (coincide con il testo dell'email)
     * <p>
     * @return testo dell'email
     */

    @Override
    public String toString() {return  testo;}

    /**
     * Metodo per sapere se una mail è stata letta o meno
     * <p>
     * @return true se la mail è stata letta, false altrimenti
     */

    public boolean isRead() {return isRead;}

    /**
     * Metodo per impostare lo stato di lettura di una mail
     * <p>
     * @param read true se la mail è stata letta, false altrimenti
     */

    public void setRead(boolean read) {isRead = read;}

    /**
     * Metodo per generare un id univoco per l'email (o utilizzare quello passato come parametro)
     * <p>
     * @param id id dell'email
     */

    private void generateId(String id) {
        if (Objects.equals(id, "null"))
            this.id = UUID.randomUUID().toString();
        else
            this.id = id;
    }
}
