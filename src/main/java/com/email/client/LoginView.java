package com.email.client;


import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class LoginView {

    public static void negative() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Errore di autenticazione");
        alert.setContentText("Username o password errati.");
        alert.showAndWait();
    }

}