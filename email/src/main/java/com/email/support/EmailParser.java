package com.email.support;

public class EmailParser {

    /**
     * Metodo per verificare se un indirizzo email è valido
     * <p>
     * @param email: email da verificare
     * @return true se l'email è valida, false altrimenti
     */

    public static boolean parser(String email){
        return email.matches("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$");
    }

    /**
     * Metodo per verificare se una stringa di destinatari è valida
     * <p>
     * @param destinatari stringa di destinatari da verificare
     * @return true se tutti i destinatari sono validi, false altrimenti
     */

    public static boolean parseDestinatari(String destinatari) {
        boolean allValid = true;
        String[] destinatariArray = destinatari.split(",");
        for (String destinatario : destinatariArray) {
            String parsedDestinatario = destinatario.trim();
            if (!parser(parsedDestinatario)) {
                System.err.println("Email non valida: " + parsedDestinatario);
                allValid = false;
            }
        }
        return allValid;
    }

}
