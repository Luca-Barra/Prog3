package com.email.client;

import com.email.email.Email;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.email.email.EmailParser.parseDestinatari;

public class NewMailview {

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Email non valida");
                alert.setContentText("Inserire un indirizzo email valido.");
                alert.showAndWait();
            } else {
                Email email = new Email(LabelUsername.getText(), destinatario, oggetto, testo, LocalDateTime.now().toString());
                System.out.println(email.getMittente() + " " + email.getDestinatario() + " " + email.getOggetto() + " " + email.getTesto() + " " + email.getData() + " ");
                clientModel.sendEmail(email);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email inviata");
                alert.setHeaderText("Email inviata con successo");
                alert.setContentText("L'email è stata inviata con successo.");
                alert.showAndWait();
            }
        });
    }

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
        destinatarioField.setDisable(true);
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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(selectedEmail.getMittente(), new Pair<>(oggettoField.getText(), testoArea.getText()));
            }
            return null;
        });

        dialogIfPresent(selectedEmail, clientModel, LabelUsername, dialog);
    }

    public static void RispostaATutti(Email selectedEmail, ClientModel clientModel, Label LabelUsername) {
        Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Rispondi a tutti");
        dialog.setHeaderText("Rispondi a tutti i destinatari dell'email selezionata");

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

        Platform.runLater(testoArea::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(selectedEmail.getMittente() + "," + selectedEmail.getDestinatario(), new Pair<>(oggettoField.getText(), testoArea.getText()));
            }
            return null;
        });

        dialogIfPresent(selectedEmail, clientModel, LabelUsername, dialog);

    }

    private static void dialogIfPresent(Email selectedEmail, ClientModel clientModel, Label LabelUsername, Dialog<Pair<String, Pair<String, String>>> dialog) {
        dialog.showAndWait().ifPresent(response -> {
            if (!parseDestinatari(selectedEmail.getMittente())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Email non valida");
                alert.setContentText("Inserire un indirizzo email valido.");
                alert.showAndWait();
            } else {
                Email email = new Email(LabelUsername.getText(), selectedEmail.getMittente(), response.getValue().getKey(), response.getValue().getValue(), LocalDateTime.now().toString());
                clientModel.sendEmail(email);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email inviata");
                alert.setHeaderText("Email inviata con successo");
                alert.setContentText("L'email è stata inviata con successo.");
                alert.showAndWait();
            }
        });
    }

    public static void serverDown() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Impossibile connettersi al server.");
        alert.setContentText("Il server è down.");
        alert.showAndWait();
    }

}
