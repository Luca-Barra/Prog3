package com.email.client;
import com.email.email.Email;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.email.email.EmailParser.parseDestinatari;
import static com.email.email.EmailParser.parser;

public class ClientController {

    @FXML
    private ListView<Email> emailListView;

    @FXML
    private Label LabelUsername;

    @FXML
    private TextField LabelMittente;

    @FXML
    private TextField LabelDestinatario;

    @FXML
    private TextField LabelOggetto;

    @FXML
    private TextField LabelData;

    @FXML
    private TextArea LabelTestoEmail;

    private ClientModel clientModel = new ClientModel("");


    @FXML
    public void initialize() {

        LabelMittente.setEditable(false);
        LabelDestinatario.setEditable(false);
        LabelOggetto.setEditable(false);
        LabelData.setEditable(false);
        LabelTestoEmail.setEditable(false);
        LabelTestoEmail.setWrapText(true);

        emailListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayEmailDetails(newValue);
            }
        });
    }

    private void displayEmailDetails(Email email) {
        LabelMittente.setText(email.getMittente());
        LabelDestinatario.setText(email.getDestinatario());
        LabelOggetto.setText(email.getOggetto());
        LabelData.setText(email.getData());
        LabelTestoEmail.setText(email.getTesto());
    }

    @FXML
    public void nuovaMail() {
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
            }
        });
    }

    public void rispondi() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
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
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna email selezionata");
            alert.setHeaderText("Seleziona un'email da rispondere.");
            alert.showAndWait();
        }
    }

    @FXML
    public void rispondiATutti() {

        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
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
                    return new Pair<>(selectedEmail.getMittente() + "," + selectedEmail.getDestinatario(), new Pair<>(oggettoField.getText(), testoArea.getText()));
                }
                return null;
            });

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
                }
            });
        }
    }

    @FXML
    public void inoltra() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Inoltra Email");
            dialog.setHeaderText("Inserisci i nuovi destinatari separati da virgola");
            dialog.setContentText("Destinatari:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(destinatari -> {
                if (parseDestinatari(destinatari)) {
                    String[] destinatariArray = destinatari.split(",");
                    List<String> destinatariList = Arrays.asList(destinatariArray);
                    clientModel.forwardEmail(selectedEmail, destinatariList);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore nell'inoltro dell'email");
                    alert.setHeaderText("Uno o più indirizzi email inseriti non sono validi.");
                    alert.showAndWait();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna email selezionata");
            alert.setHeaderText("Seleziona un'email da inoltrare.");
            alert.showAndWait();
        }
    }


    public void elimina() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Conferma eliminazione");
                alert.setHeaderText("Sei sicuro di voler eliminare questa email?");
                alert.setContentText("L'eliminazione dell'email sarà definitiva.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clientModel.deleteEmail(selectedEmail);
                    emailListView.getItems().remove(selectedEmail);
                    emailListView.refresh();
                }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna email selezionata");
            alert.setHeaderText("Seleziona un'email da eliminare.");
            alert.showAndWait();
        }
    }

    public void setLabelUsername(String username){
        LabelUsername.setText(username);
        clientModel.loadEmailsFromLocal("/home/luna/IdeaProjects/Project-Prog-3/src/main/resources/com/email/client/localmailbox/" +
                LabelUsername.getText() + ".txt");
        emailListView.setItems(clientModel.getEmailList());
        clientModel.updateLocalMailboxPeriodically(LabelUsername.getText());
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = new ClientModel(clientModel.getUser());
    }

}