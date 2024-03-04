package com.email.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class LoginController {

    public javafx.scene.control.PasswordField PasswordField;
    public TextField UserField;
    public Label LabelError;
    private ClientModel clientModel;

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String user = UserField.getText();
        String password = PasswordField.getText();
        TryLogin tryLogin = new TryLogin(user, password);
        if(tryLogin.check()){
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
            clientModel = new ClientModel(user);
            FXMLLoader log = new FXMLLoader();
            log.setLocation(ClientApplication.class.getResource("client-view.fxml"));
            Scene logScene = new Scene(log.load(), 900, 600);
            ClientController clientController = log.getController();
            clientController.setClientModel(clientModel);
            clientController.setLabelUsername(clientModel.getUser());
            stage.setOnCloseRequest(event -> {
                System.exit(0);
            });
            stage.setTitle("Client");
            stage.setScene(logScene);
            stage.show();
        } else LoginView.negative(LabelError);
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }
}
