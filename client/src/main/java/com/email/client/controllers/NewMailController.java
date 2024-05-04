package com.email.client.controllers;
import com.email.Email;
import com.email.client.models.ClientModel;
import com.email.client.utils.MyAlert;
import com.email.client.utils.NewMailView;
import com.email.utils.EmailParser;
import javafx.scene.control.Dialog;

import javafx.util.Pair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class NewMailController {

    private final ClientModel clientModel;
    private final String user;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Creazione del controller
     * <p>
     * @param clientModel Il model del client
     */

    public NewMailController(ClientModel clientModel) {
        this.clientModel = clientModel;
        this.user = clientModel.getUser();
    }

    /**
     * Metodo per l'invio di una nuova mail.
     */

    public void nuovaMail() {
        Dialog<Pair<String, Pair<String, String>>> dialog = NewMailView.nuovaMail();

        Optional<Pair<String, Pair<String, String>>> result = dialog.showAndWait();
        result.ifPresent(destinatarioOggettoTesto -> {
            String destinatario = destinatarioOggettoTesto.getKey();
            String oggetto = destinatarioOggettoTesto.getValue().getKey();
            String testo = destinatarioOggettoTesto.getValue().getValue();
            if (!EmailParser.parseDestinatari(destinatario)) {
                MyAlert.error("Errore", "Email non valida", "Inserire un indirizzo email valido.");
            } else {
                String data = LocalDateTime.now().format(formatter);
                Email email = new Email(user, destinatario, oggetto, testo, data, "null");
                System.out.println(email.getMittente() + " " + email.getDestinatario() + " " + email.getOggetto() + " " + email.getTesto() + " " + email.getData() + " ");
                clientModel.sendEmail(email);
            }
        });
    }

    /**
     * Metodo per rispondere a una mail.
     * <p>
     * @param selectedEmail La mail a cui rispondere.
     */

    public void risposta(Email selectedEmail){
        Dialog<Pair<String, Pair<String, String>>> dialog = NewMailView.risposta(selectedEmail);

        Optional<Pair<String, Pair<String, String>>> result = dialog.showAndWait();

        result.ifPresent(response -> {
                Email email = new Email(user, selectedEmail.getMittente(), response.getValue().getKey(), response.getValue().getValue(), LocalDateTime.now().format(formatter), selectedEmail.getId());
                clientModel.sendEmail(email);
        });

    }

    /**
     * Metodo per rispondere a tutti i destinatari di una mail.
     *
     * @param selectedEmail La mail a cui rispondere.
     * @param user L'utente a cui sta rispondendo.
     * @param destinatari I destinatari.
     */

    public void rispostaATutti(Email selectedEmail, String user, String destinatari){
        Dialog<Pair<String, Pair<String, String>>> dialog = NewMailView.rispostaATutti(selectedEmail, user);

        Optional<Pair<String, Pair<String, String>>> result = dialog.showAndWait();

        result.ifPresent(response -> {
            Email email = new Email(user, destinatari, response.getValue().getKey(), response.getValue().getValue(), LocalDateTime.now().format(formatter), selectedEmail.getId());
            clientModel.sendEmail(email);
        });

    }

}