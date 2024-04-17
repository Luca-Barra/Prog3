package com.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage = new Stage();
        FXMLLoader log = new FXMLLoader();
        log.setLocation(ClientApplication.class.getResource("UI/login-view.fxml"));
        Scene logScene = new Scene(log.load(), 900, 600);
        logScene.getStylesheets().add(Objects.requireNonNull(ClientApplication.class.getResource("UI/login.css")).toExternalForm());
        stage.setTitle("Login");
        stage.setScene(logScene);
        stage.show();
    }
}
