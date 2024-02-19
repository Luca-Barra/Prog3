package com.email.client;
import com.email.email.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientController {

    @FXML
    private ListView<Email> emailListView;

    @FXML
    private Label LabelUsername;

    @FXML
    private Button ButtonNuovaMail;

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

    @FXML
    private Button ButtonRaTutti;

    @FXML
    private Button ButtonRispondi;

    @FXML
    private Button ButtonElimina;

    @FXML
    private Button ButtonInoltra;

    private ClientModel clientModel = new ClientModel();


    @FXML
    public void initialize() {

        LabelMittente.setEditable(false);
        LabelDestinatario.setEditable(false);
        LabelOggetto.setEditable(false);
        LabelData.setEditable(false);
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

    public void setLabelUsername(String username){
        LabelUsername.setText(username);
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }
}