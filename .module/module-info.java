module Project.Prog {
    requires java.logging;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports com.email;

    exports com.email.client.utils;
    opens com.email.client.utils to javafx.fxml;

    exports com.email.server.utils;
    opens com.email.server.utils to javafx.fxml;

    exports com.email.server.handler;
    opens com.email.server.handler to javafx.fxml;
    exports com.email.client.controllers;
    opens com.email.client.controllers to javafx.fxml;
    exports com.email.client.models;
    opens com.email.client.models to javafx.fxml;
    exports com.email.server.model;
    opens com.email.server.model to javafx.fxml;
    exports com.email.client.application;
    opens com.email.client.application to javafx.fxml;
    exports com.email.server.application;
    opens com.email.server.application to javafx.fxml;

}