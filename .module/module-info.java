module Project.Prog {
    requires java.logging;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.email.client to javafx.fxml;
    exports com.email.client;
    exports com.email.client.support;
    opens com.email.client.support to javafx.fxml;


    opens com.email.server to javafx.fxml;
    exports com.email.server;
    exports com.email.server.support;
    opens com.email.server.support to javafx.fxml;

    opens com.email to javafx.fxml;
    exports com.email;
    exports com.email.support;
    opens com.email.support to javafx.fxml;

}