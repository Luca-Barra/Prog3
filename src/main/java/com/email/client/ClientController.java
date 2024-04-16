package com.email.client;
import com.email.email.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

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
        emailListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setText(item.getTesto());
                    setMaxHeight(20);
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(20); // adjust the value as needed
                    imageView.setFitHeight(20); // adjust the value as needed
                    imageView.setPreserveRatio(true);
                    if (item.isRead()) {
                        imageView.setImage(new Image("read.png"));
                        setGraphic(imageView);
                        setStyle("-fx-font-weight: normal;");
                    } else {
                        imageView.setImage(new Image("unread.png"));
                        setGraphic(imageView);
                        setStyle("-fx-font-weight: bold;");
                    }
                    setOnMouseClicked(event -> {
                        if(event.getClickCount() == 2) { // Check if it's a double-click
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Dettagli Email");
                            alert.setHeaderText("Dettagli Email");
                            alert.setContentText("Mittente: " + item.getMittente() + "\n"
                                    + "Destinatario: " + item.getDestinatario() + "\n"
                                    + "Oggetto: " + item.getOggetto() + "\n"
                                    + "Data: " + item.getData() + "\n"
                                    + "Testo: " + item.getTesto());
                            alert.showAndWait();
                        }
                    });
                }
            }
        });
    }

    private void displayEmailDetails(Email email) {
        if(!email.isRead())
            clientModel.markAsRead(email);
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
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nessuna email selezionata");
            alert.setHeaderText("Seleziona un'email a cui rispondere.");
            alert.showAndWait();
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

    public void refresh() {
        clientModel.refreshEmails();
    }

    public void setLabelUsername(String username){
        LabelUsername.setText(username);
        clientModel.loadEmailsFromLocal("/home/luna/IdeaProjects/Project-Prog-3/src/main/resources/com/email/client/localmailbox/" +
                LabelUsername.getText() + ".txt");
        System.out.println("/home/luna/IdeaProjects/Project-Prog-3/src/main/resources/com/email/client/localmailbox/" +
                LabelUsername.getText() + ".txt");
        emailListView.setItems(clientModel.getEmailList());
        clientModel.updateLocalMailboxPeriodically();
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = new ClientModel(clientModel.getUser());
    }

}