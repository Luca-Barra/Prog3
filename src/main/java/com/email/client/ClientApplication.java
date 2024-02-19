package com.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ClientModel clientModel = new ClientModel();
        stage = new Stage();
        FXMLLoader log = new FXMLLoader();
        log.setLocation(ClientApplication.class.getResource("login-view.fxml"));
        Scene logScene = new Scene(log.load(), 900, 600);
        LoginController loginController = log.getController();
        loginController.setClientModel(clientModel);
        stage.setTitle("Login");
        stage.setScene(logScene);
        stage.show();
    }
}
