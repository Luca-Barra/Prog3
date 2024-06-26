package com.email.client.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientApplication extends Application {

    /**
     * Metodo per avviare l'applicazione
     * <p>
     * @param args argomenti passati da riga di comando
     */

    public static void main(String[] args) {
        launch();
    }

    /**
     * Metodo per avviare la schermata di login
     * <p>
     * @param stage stage della schermata
     * @throws Exception eccezione generata in caso di errore
     */

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("ClientApplication started");
        stage = new Stage();
        FXMLLoader log = new FXMLLoader();
        log.setLocation(ClientApplication.class.getResource("/com/email/client/UI/view/login-view.fxml"));
        Scene logScene = new Scene(log.load(), 900, 600);
        logScene.getStylesheets().add(Objects.requireNonNull(ClientApplication.class.getResource("/com/email/client/UI/css/login.css")).toExternalForm());
        stage.setTitle("Login");
        stage.setScene(logScene);
        stage.show();
    }
}
