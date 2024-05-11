package com.email.client.utils;

import com.email.Email;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class NewMailView {

    /**
     * Metodo che crea una finestra per una nuova mail.
     * <p>
     * @return La finestra di dialogo.
     */

    public static Dialog<Pair<String, Pair<String, String>>> nuovaMail() {
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

        dialog.setOnShown(event -> destinatarioField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(destinatarioField.getText(), new Pair<>(oggettoField.getText(), testoArea.getText()));
            }
            return null;
        });

        return dialog;
    }

    /**
     * Metodo che crea una finestra per rispondere a una mail.
     * <p>
     * @param selectedEmail La mail a cui rispondere.
     * <p>
     * @return La finestra di dialogo.
     */

    public static Dialog<Pair<String, Pair<String, String>>> risposta(Email selectedEmail) {
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
        destinatarioField.setEditable(false);
        TextField oggettoField = new TextField("R: " + selectedEmail.getOggetto());
        oggettoField.setEditable(false);
        TextArea testoArea = new TextArea();
        testoArea.setPromptText("Rispondi all'email qui...");

        grid.add(new Label("Destinatari:"), 0, 0);
        grid.add(destinatarioField, 1, 0);
        grid.add(new Label("Oggetto:"), 0, 1);
        grid.add(oggettoField, 1, 1);
        grid.add(new Label("Testo:"), 0, 2);
        grid.add(testoArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setOnShown(event -> testoArea.requestFocus());

        String reply = selectedEmail.getMittente() + " ha scritto:" +
                "\n\n----------------------------------------\n"
                + selectedEmail.getTesto() + "\nIn data: " + selectedEmail.getData() +
                "\n------------------------------------------\n\n";

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confermaButton) {
                return new Pair<>(selectedEmail.getMittente(), new Pair<>(dialog.getTitle(), reply + testoArea.getText()));
            }
            return null;
        });

        return dialog;

    }

    /**
     * Metodo che crea una finestra per rispondere a tutti.
     * <p>
     * @param selectedEmail La mail a cui rispondere.
     * @param user L'utente che sta utilizzando il servizio
     * <p>
     * @return La finestra di dialogo.
     */

    public static Dialog<Pair<String, Pair<String, String>>> rispostaATutti(Email selectedEmail, String user){
        Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Rispondi a tutti");
        dialog.setHeaderText("Rispondi a tutti i destinatari");
        String exclude = excludeCurrentUser(user, selectedEmail.getMittente() + "," + selectedEmail.getDestinatario());

        ButtonType confermaButton = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField destinatarioField = new TextField(exclude);
        destinatarioField.setEditable(false);
        TextField oggettoField = new TextField("R: " + selectedEmail.getOggetto());
        oggettoField.setEditable(false);
        TextArea testoArea = new TextArea();
        testoArea.setPromptText("Rispondi all'email qui...");

        grid.add(new Label("Destinatari:"), 0, 0);
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

        return dialog;
    }

    /**
     * Metodo per evitare di rispondere a sé stessi quando si risponde a tutti.
     * <p>
     * @param userEmail L'email del mittente.
     * @param recipients Le email dei destinatari.
     * <p>
     * @return La stringa con i nuovi destinatari.
     */

    public static String excludeCurrentUser(String userEmail, String recipients) {
        StringBuilder excludedRecipients = new StringBuilder();
        String[] recipientList = recipients.split(",");
        for (String recipient : recipientList) {
            String trimmedRecipient = recipient.trim();
            if (!trimmedRecipient.equalsIgnoreCase(userEmail.trim())) {
                if (!excludedRecipients.isEmpty()) {
                    excludedRecipients.append(", ");
                }
                excludedRecipients.append(trimmedRecipient);
            }
        }
        return excludedRecipients.toString();
    }

}
