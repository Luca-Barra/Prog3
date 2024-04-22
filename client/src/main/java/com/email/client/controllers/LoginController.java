package com.email.client.controllers;

import com.email.client.application.ClientApplication;
import com.email.client.models.ClientModel;
import com.email.client.models.LoginModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class LoginController {

    public javafx.scene.control.PasswordField PasswordField;
    public TextField UserField;
    public Label LabelError;

    /**
     * Metodo che gestisce il login dell'utente.
     * <p>
     * @param actionEvent evento che scaturisce l'azione (click sul pulsante di login)
     * @throws IOException eccezione lanciata in caso di errore durante il caricamento della vista del client
     */

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String user = UserField.getText();
        String password = PasswordField.getText();
        LoginModel tryLogin = new LoginModel(user, password);
        if(tryLogin.check && tryLogin.check()){
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
            ClientModel clientModel = new ClientModel(user);
            FXMLLoader log = new FXMLLoader();
            log.setLocation(ClientApplication.class.getResource("/com/email/client/UI/view/client-view.fxml"));
            Scene logScene = new Scene(log.load(), 900, 600);
            logScene.getStylesheets().add(Objects.requireNonNull(ClientApplication.class.getResource("/com/email/client/UI/css/client.css")).toExternalForm());
            ClientController clientController = log.getController();
            clientController.initModel(clientModel);
            clientController.setLabelUsername(clientModel.getUser());
            stage.setOnCloseRequest(event -> System.exit(0));
            stage.setTitle("Client");
            stage.setScene(logScene);
            stage.show();
        }
    }

}
