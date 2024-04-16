package com.email.client;
import com.email.client.support.MyAlert;
import com.email.email.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

import static com.email.email.support.EmailParser.parseDestinatari;

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
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
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
                        if(event.getClickCount() == 2) {
                            MyAlert.info("Dettagli Email", "Dettagli Email", "Mittente: " + item.getMittente() + "\n"
                                    + "Destinatario: " + item.getDestinatario() + "\n"
                                    + "Oggetto: " + item.getOggetto() + "\n"
                                    + "Data: " + item.getData() + "\n"
                                    + "Testo: " + item.getTesto());
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
        NewMailView.NuovaMail(clientModel, LabelUsername);
    }

    public void rispondi() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            NewMailView.Risposta(selectedEmail, clientModel, LabelUsername);
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email a cui rispondere.", "");
        }
    }

    @FXML
    public void rispondiATutti() {

        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            NewMailView.RispostaATutti(selectedEmail, clientModel, LabelUsername);
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email a cui rispondere.", "");
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
                    MyAlert.error("Errore nell'inoltro dell'email", "Uno o più indirizzi email inseriti non sono validi.", "");
                }
            });
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email da inoltrare.", "");
        }
    }


    public void elimina() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
                Optional<ButtonType> result = MyAlert.confirmation("Conferma eliminazione", "Sei sicuro di voler eliminare questa email?", "L'eliminazione dell'email sarà definitiva.");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clientModel.deleteEmail(selectedEmail);
                    emailListView.getItems().remove(selectedEmail);
                    emailListView.refresh();
                }
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email da eliminare.", "");
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