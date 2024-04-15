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
        NewMailview.NuovaMail(clientModel, LabelUsername);
    }

    public void rispondi() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
            NewMailview.Risposta(selectedEmail, clientModel, LabelUsername);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna email selezionata");
            alert.setHeaderText("Seleziona un'email a cui rispondere.");
            alert.showAndWait();
        }
    }

    @FXML
    public void rispondiATutti() {

        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            NewMailview.RispostaATutti(selectedEmail, clientModel, LabelUsername);

        }
    }

    @FXML
    public void inoltra() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Inoltra Email");
            dialog.setHeaderText("Inserisci i nuovi destinatari (separati da virgola)");
            dialog.setContentText("Destinatari:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(destinatari -> {
                if (parseDestinatari(destinatari)) {
                    clientModel.forwardEmail(selectedEmail);
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