package com.email.client.controllers;
import com.email.client.models.ClientModel;
import com.email.client.utils.MyAlert;
import com.email.Email;
import com.email.client.utils.NewMailView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

import static com.email.utils.EmailParser.parseDestinatari;

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

    /**
     * Metodo che inizializza la finestra di visualizzazione delle email.
     * <p>
     * Inizializza i campi di testo per la visualizzazione delle email e imposta il listener per la selezione delle email,
     * usando i javafx.beans.property per aggiornare i campi di testo con i dettagli dell'email selezionata.
     * <p>
     * Imposta la cell factory per la ListView delle email, in modo da visualizzare le email con un'icona diversa e
     * un font diverso (grassetto o normale) a seconda che siano state lette o meno.
     * <p>
     * Infine, imposta un listener per il doppio click su una email, che mostra una finestra di dialogo con i dettagli
     * dell'email selezionata.
     */

    @FXML
    public void initialize() {

        initLabels();

        initEmailListView();

    }

    /**
     * Metodo che inizializza i campi di testo per la visualizzazione delle email.
     * <p>
     * Inizializza i campi di testo per la visualizzazione delle email.
     */

    private void initLabels() {
        LabelMittente.setEditable(false);
        LabelDestinatario.setEditable(false);
        LabelOggetto.setEditable(false);
        LabelData.setEditable(false);
        LabelTestoEmail.setEditable(false);
        LabelTestoEmail.setWrapText(true);
    }

    /**
     * Metodo che inizializza la ListView delle email.
     * <p>
     * Inizializza la ListView delle email.
     */

    private void initEmailListView() {
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

    /**
     * Metodo che visualizza i dettagli dell'email selezionata.
     * <p>
     * Visualizza i dettagli dell'email selezionata nei campi di testo predisposti per la visualizzazione.
     * <p>
     * Se l'email non è stata letta, la marca come letta.
     * <p>
     * @param email l'email selezionata
     */

    private void displayEmailDetails(Email email) {
        if(!email.isRead())
            clientModel.markAsRead(email);
        LabelMittente.setText(email.getMittente());
        LabelDestinatario.setText(email.getDestinatario());
        LabelOggetto.setText(email.getOggetto());
        LabelData.setText(email.getData());
        LabelTestoEmail.setText(email.getTesto());
    }

    /**
     * Metodo che permette di scrivere una nuova email.
     * <p>
     * Apre una finestra di dialogo per la scrittura di una nuova email.
     */

    @FXML
    public void nuovaMail() {
        NewMailController controller = new NewMailController(clientModel);
        controller.nuovaMail();
    }

    /**
     * Metodo che permette di rispondere a una email.
     * <p>
     * Seleziona l'email a cui rispondere e apre una finestra di dialogo per la scrittura di una risposta.
     */

    public void rispondi() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            NewMailController controller = new NewMailController(clientModel);
            controller.risposta(selectedEmail);
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email a cui rispondere.", "");
        }
    }

    /**
     * Metodo che permette di rispondere a tutti i destinatari di una email.
     * <p>
     * Seleziona l'email a cui rispondere e apre una finestra di dialogo per la scrittura di una risposta.
     */

    @FXML
    public void rispondiATutti() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            NewMailController controller = new NewMailController(clientModel);
            controller.rispostaATutti(selectedEmail, clientModel.getUser(), NewMailView.excludeCurrentUser(clientModel.getUser(), selectedEmail.getMittente() + "," + selectedEmail.getDestinatario()));
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email a cui rispondere.", "");
        }
    }

    /**
     * Metodo che permette di inoltrare una email.
     * <p>
     * Seleziona l'email da inoltrare e apre una finestra di dialogo per l'inserimento dei nuovi destinatari.
     * <p>
     * Se gli indirizzi email inseriti non sono validi, mostra un messaggio di errore.
     */

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
                    clientModel.forwardEmail(selectedEmail, destinatari);
                } else {
                    MyAlert.error("Errore nell'inoltro dell'email", "Uno o più indirizzi email inseriti non sono validi.", "");
                }
            });
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email da inoltrare.", "");
        }
    }

    /**
     * Metodo che permette di eliminare una email.
     * <p>
     * Seleziona l'email da eliminare e mostra una finestra di dialogo per la conferma dell'eliminazione.
     * <p>
     * Se l'eliminazione viene confermata, elimina l'email selezionata.
     */

    public void elimina() {
        Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
                Optional<ButtonType> result = MyAlert.confirmation("Conferma eliminazione", "Sei sicuro di voler eliminare questa email?", "L'eliminazione dell'email sarà definitiva.");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clientModel.deleteEmail(selectedEmail);
                }
        } else {
            MyAlert.warning("Nessuna email selezionata", "Seleziona un'email da eliminare.", "");
        }
    }

    /**
     * Metodo che permette di aggiornare la lista delle email.
     * <p>
     * Aggiorna la lista delle email.
     */

    public void refresh() {clientModel.refreshEmails();}

    /**
     * Metodo che permette di inizializzare il controller.
     * <p>
     * Inizializza il controller e fa partire il thread per l'aggiornamento periodico della casella di posta.
     * <p>
     * @param cm il client model
     * @throws IllegalStateException se il controller è già stato inizializzato
     */

    public void initModel(ClientModel cm) {
        if(this.clientModel == null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.clientModel = cm;
        LabelUsername.setText(this.clientModel.getUser());

        ObservableList<Email> items = FXCollections.observableArrayList();
        this.clientModel.getEmailList().addListener((ListChangeListener<Email>) c -> {

            while (c.next()) {
                if (c.wasAdded()) {
                    Platform.runLater(() ->
                            items.addAll(c.getAddedSubList()));
                }
                if (c.wasRemoved()) {
                    Platform.runLater(() ->
                            items.removeAll(c.getRemoved()));
                }
            }
        });

        emailListView.setItems(items);

        this.clientModel.loadEmailsFromLocal("com/email/client/local-mailbox/" +
                LabelUsername.getText() + ".txt");

        this.clientModel.updateLocalMailboxPeriodically();

        addAlerts();

    }

    /**
     * Metodo che permette di aggiungere gli alert.
     * <p>
     * Aggiunge gli alert per la gestione degli errori e delle notifiche.
     */

    private void addAlerts() {
        clientModel.addPropertyChangeListener(evt -> Platform.runLater(() -> {
            switch (evt.getPropertyName()) {
                case "Errore nell'invio dell'email":
                    MyAlert.error("Errore nell'invio dell'email", "Destinatario inesistente", "Inserire un indirizzo email registrato.");
                    break;
                case "Email inviata":
                    MyAlert.info("Email inviata", "Email inviata con successo", "L'email è stata inviata con successo.");
                    break;
                case "Errore nell'invio dell'email - Socket":
                    MyAlert.error("Errore nell'invio dell'email", "Errore di connessione", "Controllare la connessione e riprovare.");
                    break;
                case "Errore nell'inoltro dell'email":
                    MyAlert.error("Errore nell'inoltro dell'email", "Destinatario inesistente", "Inserire un indirizzo email registrato.");
                    break;
                case "Email inoltrata":
                    MyAlert.info("Email inoltrata", "Email inoltrata con successo", "L'email è stata inoltrata con successo.");
                    break;
                case "Errore nell'inoltro dell'email - Socket":
                    MyAlert.error("Errore nell'inoltro dell'email", "Errore di connessione", "Controllare la connessione e riprovare.");
                    break;
                case "Email eliminata":
                    MyAlert.info("Email eliminata", "Email eliminata con successo", "L'email è stata eliminata con successo.");
                    break;
                case "Errore nell'eliminazione dell'email":
                    MyAlert.error("Errore nell'eliminazione dell'email", "Errore nell'eliminazione dell'email", "Riprova.");
                    break;
                case "Errore nell'eliminazione dell'email - Socket":
                    MyAlert.error("Errore nell'eliminazione dell'email", "Errore di connessione", "Controllare la connessione e riprovare.");
                    break;
                case "Email ricevute":
                    MyAlert.info("Aggiornamento casella di posta", "Casella di posta aggiornata", "La casella di posta è stata aggiornata con successo.");
                    break;
            }
        }));
    }


}