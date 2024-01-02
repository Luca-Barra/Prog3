package com.email.client;


import javafx.scene.control.Label;

public class LoginView {

    public static void negative(Label labelError) {
        labelError.setText("Credenziali errate");
    }

}