package com.email.client.support;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MyAlert {

    /**
     * This method is used to show an error alert
     * <p>
     * @param title the title of the alert
     * @param header the header of the alert
     * @param content the content of the alert
     */

    public static void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * This method is used to show a warning alert
     * <p>
     * @param title the title of the alert
     * @param header the header of the alert
     * @param content the content of the alert
     */

    public static void warning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * This method is used to show an information alert
     * <p>
     * @param title the title of the alert
     * @param header the header of the alert
     * @param content the content of the alert
     */

    public static void info(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * This method is used to show a confirmation alert
     * <p>
     * @param title the title of the alert
     * @param header the header of the alert
     * @param content the content of the alert
     * @return the button type of the alert
     */

    public static Optional<ButtonType> confirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

}
