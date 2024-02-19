package com.email.client;
import com.email.email.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ClientController {

    @FXML
    private ListView<Email> emailListView;

    @FXML
    private Label LabelUsername;

    @FXML
    private Button ButtonNuovaMail;

    @FXML
    private Label LabelMittente;

    @FXML
    private Label LabelDestinatario;

    @FXML
    private Label LabelOggetto;

    @FXML
    private Label LabelData;

    @FXML
    private TextArea LabelTestoEmail;

    @FXML
    private Button ButtonRaTutti;

    @FXML
    private Button ButtonRispondi;

    @FXML
    private Button ButtonElimina;

    @FXML
    private Button ButtonInoltra;

    private ClientModel clientModel;


    @FXML
    public void initialize() {
        // Inizializza il clientModel
        clientModel = new ClientModel();
        LabelTestoEmail.setEditable(false);
        LabelTestoEmail.setWrapText(true);

        emailListView.setItems(clientModel.getEmailList());


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

    }


}