package com.email.support;

public class EmailParser {

    public static boolean parser(String email){
        return email.matches("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$");
    }

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
