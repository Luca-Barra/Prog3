package com.email.server;

import com.email.email.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServerController {

    private ServerModel serverModel;
    private ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    @FXML
    private TableView<LogEntry> emailTableView;

    @FXML
    private TableColumn<LogEntry, String> columnUtente;

    @FXML
    private TableColumn<LogEntry, String> columnMessaggio;

    public void setServerModel(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    public void initialize() {
        // Inizializza la tabella e le colonne
        columnUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        columnMessaggio.setCellValueFactory(new PropertyValueFactory<>("messaggio"));

        // Collega la lista alla TableView
        emailTableView.setItems(logEntries);

        // Popola la tabella con dati di esempio
        logEntries.addAll(
                new LogEntry("Server", "Server avviato")
        );
    }

    public void addLogEntry(LogEntry logEntry) {
        logEntries.add(logEntry);
    }

}
