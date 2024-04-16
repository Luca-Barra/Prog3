module com.email {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.net.http;

    opens com.example.projectprog to javafx.fxml;
    opens com.email.client to javafx.fxml;
    opens com.email.email to javafx.fxml;
    opens com.email.server to javafx.fxml;
    exports com.example.projectprog;
    exports com.email.email;
    exports com.email.server;
    exports com.email.client;
    exports com.email.client.support;
    opens com.email.client.support to javafx.fxml;
    exports com.email.email.support;
    opens com.email.email.support to javafx.fxml;
    exports com.email.server.support;
    opens com.email.server.support to javafx.fxml;
}