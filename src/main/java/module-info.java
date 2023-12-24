module com.example.projectprog3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projectprog3 to javafx.fxml;
    exports com.example.projectprog3;
}