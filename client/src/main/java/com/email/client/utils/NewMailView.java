package com.email.client.utils;

import com.email.Email;
import com.email.client.models.ClientModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.email.utils.EmailParser.parseDestinatari;

public class NewMailView {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Metodo per la creazione di una nuova email
     * <p>
     * @param clientModel modello del client
     * @param LabelUsername label contenente l'username dell'utente
     */

    public static void NuovaMail(ClientModel clientModel, Label LabelUsername){
        Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Nuova Email");
        dialog.setHeaderText("Nuova email");

        ButtonType confermaButton = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField destinatarioField = new TextField();
        destinatarioField.setPromptText("Destinatario");
        TextField oggettoField = new TextField();
        oggettoField.setPromptText("Oggetto");
        TextArea testoArea = new TextArea();
        testoArea.setPromptText("Testo");

        grid.add(new Label("Destinatario:"), 0, 0);
        grid.add(destinatarioField, 1, 0);
        grid.add(new Label("Oggetto:"), 0, 1);
        grid.add(oggettoField, 1, 1);
        grid.add(new Label("Testo:"), 0, 2);
        grid.add(testoArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(destinatarioField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(destinatarioField.getText(), new Pair<>(oggettoField.getText(), testoArea.getText()));
            }
            return null;
        });

        Optional<Pair<String, Pair<String, String>>> result = dialog.showAndWait();
        result.ifPresent(destinatarioOggettoTesto -> {
            String destinatario = destinatarioOggettoTesto.getKey();
            String oggetto = destinatarioOggettoTesto.getValue().getKey();
            String testo = destinatarioOggettoTesto.getValue().getValue();
            if (!parseDestinatari(destinatarioField.getText())) {
                MyAlert.error("Errore", "Email non valida", "Inserire un indirizzo email valido.");
            } else {
                Email email = new Email(LabelUsername.getText(), destinatario, oggetto, testo, LocalDateTime.now().format(formatter), "null");
                System.out.println(email.getMittente() + " " + email.getDestinatario() + " " + email.getOggetto() + " " + email.getTesto() + " " + email.getData() + " ");
                clientModel.sendEmail(email);
            }
        });
    }

    /**
     * Metodo per la risposta a una email
     * <p>
     * @param selectedEmail email selezionata
     * @param clientModel modello del client
     * @param LabelUsername label contenente l'username dell'utente
     */

    public static void Risposta(Email selectedEmail, ClientModel clientModel, Label LabelUsername) {
        Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Rispondi");
        dialog.setHeaderText("Rispondi all'email selezionata");

        ButtonType confermaButton = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField destinatarioField = new TextField(selectedEmail.getMittente());
        TextField oggettoField = new TextField("R: " + selectedEmail.getOggetto());
        TextArea testoArea = new TextArea();
        testoArea.setPromptText("Rispondi all'email qui...");

        grid.add(new Label("Destinatario:"), 0, 0);
        grid.add(destinatarioField, 1, 0);
        grid.add(new Label("Oggetto:"), 0, 1);
        grid.add(oggettoField, 1, 1);
        grid.add(new Label("Testo:"), 0, 2);
        grid.add(testoArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(testoArea::requestFocus);

        String reply = selectedEmail.getMittente() + " ha scritto:" +
                "\n\n----------------------------------------\n"
                        + selectedEmail.getTesto() + "\nIn data: " + selectedEmail.getData() +
                "\n------------------------------------------\n\n";

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(selectedEmail.getMittente(), new Pair<>(oggettoField.getText(), reply + testoArea.getText()));
            }
            return null;
        });

        dialogIfPresent(selectedEmail, clientModel, LabelUsername, dialog);
    }

    /**
     * Metodo per la risposta a tutti i destinatari di una email
     * <p>
     * @param selectedEmail email selezionata
     * @param clientModel modello del client
     * @param LabelUsername label contenente l'username dell'utente
     */

    public static void RispostaATutti(Email selectedEmail, ClientModel clientModel, Label LabelUsername) {
        Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Rispondi a tutti");
        dialog.setHeaderText("Rispondi a tutti i destinatari");

        ButtonType confermaButton = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField destinatarioField = new TextField(selectedEmail.getMittente() + "," + selectedEmail.getDestinatario());
        TextField oggettoField = new TextField("R: " + selectedEmail.getOggetto());
        TextArea testoArea = new TextArea();
        testoArea.setPromptText("Rispondi all'email qui...");

        grid.add(new Label("Destinatario:"), 0, 0);
        grid.add(destinatarioField, 1, 0);
        grid.add(new Label("Oggetto:"), 0, 1);
        grid.add(oggettoField, 1, 1);
        grid.add(new Label("Testo:"), 0, 2);
        grid.add(testoArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        String reply = selectedEmail.getMittente() + " ha scritto:" +
                "\n\n----------------------------------------\n"
                + selectedEmail.getTesto() + "\nIn data: " + selectedEmail.getData() +
                "\n------------------------------------------\n\n";

        Platform.runLater(testoArea::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(selectedEmail.getMittente() + "," + selectedEmail.getDestinatario(), new Pair<>(oggettoField.getText(), reply + testoArea.getText()));
            }
            return null;
        });

        dialog.showAndWait().ifPresent(response -> {
            if (!parseDestinatari(selectedEmail.getMittente())) {
                MyAlert.error("Errore", "Email non valida", "Inserire un indirizzo email valido.");
            } else {
                Email email = new Email(LabelUsername.getText(), selectedEmail.getMittente() + "," + selectedEmail.getDestinatario(), response.getValue().getKey(), response.getValue().getValue(), LocalDateTime.now().format(formatter), selectedEmail.getId());
                clientModel.sendEmail(email);
            }
        });

    }

    /**
     * Utility per la gestione del dialog
     * <p>
     * @param selectedEmail email selezionata
     * @param clientModel modello del client
     * @param LabelUsername label contenente l'username dell'utente
     * @param dialog dialog
     */

    private static void dialogIfPresent(Email selectedEmail, ClientModel clientModel, Label LabelUsername, Dialog<Pair<String, Pair<String, String>>> dialog) {
        dialog.showAndWait().ifPresent(response -> {
            if (!parseDestinatari(selectedEmail.getMittente())) {
                MyAlert.error("Errore", "Email non valida", "Inserire un indirizzo email valido.");
            } else {
                Email email = new Email(LabelUsername.getText(), selectedEmail.getMittente(), response.getValue().getKey(), response.getValue().getValue(), LocalDateTime.now().format(formatter), selectedEmail.getId());
                clientModel.sendEmail(email);
            }
        });
    }

    /**
     * Metodo per segnalare che il server è down
     */

    public static void serverDown() {MyAlert.error("Errore", "Impossibile connettersi al server.", "Il server è down.");}

}
