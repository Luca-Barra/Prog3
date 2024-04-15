package com.email.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class ServerController {

    private static final ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    @FXML
    private TableView<LogEntry> emailTableView;

    @FXML
    private TableColumn<LogEntry, String> columnUtente;

    @FXML
    private TableColumn<LogEntry, String> columnMessaggio;

    @FXML
    private TableColumn<LogEntry, String> data;

    public void initialize() {
        // Inizializza la tabella e le colonne
        columnUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        columnMessaggio.setCellValueFactory(new PropertyValueFactory<>("messaggio"));
        data.setCellValueFactory(new PropertyValueFactory<>("data"));

        // Collega la lista alla TableView
        emailTableView.setItems(logEntries);

        // Popola la tabella con dati di esempio
        logEntries.addAll(
                new LogEntry("Server", "Server avviato", LocalDateTime.now().toString())
        );
    }

    public static void addLogEntry(String utente, String messaggio, String data) {
        logEntries.add(new LogEntry(utente, messaggio, data));
    }

}
