package com.email.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(ServerController.class.getName());

    public void initialize() {

        columnUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        columnMessaggio.setCellValueFactory(new PropertyValueFactory<>("messaggio"));
        data.setCellValueFactory(new PropertyValueFactory<>("data"));

        emailTableView.setItems(logEntries);

        logEntries.addAll(
                new LogEntry("Server", "Server avviato", LocalDateTime.now().toString())
        );
    }

    public static synchronized void addLogEntry(String utente, String messaggio, String data) {
        logEntries.add(new LogEntry(utente, messaggio, data));
    }

    public static void saveLogs() {
        String filename = "src/main/resources/serverlogs.txt";
        try {
            System.out.println("Salvataggio dei log in corso...");
            Path path = Paths.get(filename);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write("\n");
            for (LogEntry logEntry : logEntries) {
                writer.write(logEntry.getUtente() + ";" + logEntry.getMessaggio() + ";" + logEntry.getData());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio dei log: " + e.getMessage());
        }
    }

}
