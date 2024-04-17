package com.email.support;

public class EmailParser {

    public static boolean parser(String email){
        int state = 0;
        System.out.println(email);
        for (char c : email.toCharArray()) {
            switch (state) {
                case 0:
                    if (Character.isLetter(c) || Character.isDigit(c)){
                        state = 1;
                    } else state = 404;
                    break;
                case 1:
                    if (c == '.'){
                        state = 2;
                    } else if (Character.isWhitespace(c))
                        state = 404;
                    break;
                case 2:
                    if (c == '@')
                        state = 3;
                    else if (Character.isWhitespace(c))
                        state = 404;
                    break;
                case 3:
                    if (Character.isWhitespace(c))
                        state = 404;
                    if (c == '.')
                        state = 4;
                    break;
                case 4:
                    if (!Character.isLetter(c))
                        state = 404;
                    break;
            }
        }

        if (state != 4) System.err.println("Email scorretta + " + state);

        return state == 4;

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
