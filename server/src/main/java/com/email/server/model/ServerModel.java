package com.email.server.model;

import com.email.client.utils.MyAlert;
import com.email.server.utils.LogEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerModel {

    public static final ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();

    @FXML
    private TableView<LogEntry> emailTableView;

    @FXML
    private TableColumn<LogEntry, String> columnUtente;

    @FXML
    private TableColumn<LogEntry, String> columnMessaggio;

    @FXML
    private TableColumn<LogEntry, String> data;

    private static final Logger logger = Logger.getLogger(ServerModel.class.getName());

    private static ArrayList<String> registeredUsers;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void initialize() {

        columnUtente.setCellValueFactory(new PropertyValueFactory<>("utente"));
        columnUtente.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
        columnMessaggio.setCellValueFactory(new PropertyValueFactory<>("messaggio"));
        data.setCellValueFactory(new PropertyValueFactory<>("data"));
        data.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-style: italic;");
                }
            }
        });

        emailTableView.setRowFactory(tv -> {
            TableRow<LogEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    LogEntry rowData = row.getItem();
                    MyAlert.info("Dettagli messaggio",
                            "Messaggio di " + rowData.getUtente(),
                            "Data: " + rowData.getData() +
                            "\n\n------------------------------------------------------------------\n" +
                            rowData.getMessaggio() +
                                    "\n------------------------------------------------------------------\n\n");

                }
            });
            return row;
        });

        registeredUsers = new ArrayList<>();
        loadRegisteredUsers();

        emailTableView.setItems(logEntries);

        logEntries.addAll(
                new LogEntry("Server", "Server avviato", LocalDateTime.now().format(formatter))
        );
    }

    public static synchronized void addLogEntry(String utente, String messaggio, String data) {
        logEntries.add(new LogEntry(utente, messaggio, data));
    }

    public static void saveLogs() {
        String filename = "server/src/main/resources/com/email/server/logs/server-logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt";
        try {
            System.out.println("Salvataggio dei log in corso...");
            Path path = Paths.get(filename);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            writer.write("\n-------------------------------------------------\n");
            writer.write("| Logs salvato il " + LocalDateTime.now().format(formatter) + " |");
            writer.write("\n-------------------------------------------------\n");
            for (LogEntry logEntry : logEntries) {
                writer.write(logEntry.getUtente() + ";" + logEntry.getMessaggio() + ";" + logEntry.getData());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            logger.severe("Errore durante il salvataggio dei log: " + e.getMessage());
        }
    }

    public static void loadRegisteredUsers() {
        try(InputStream inputStream = ServerModel.class.getResourceAsStream("/com/email/server/data/registeredUsers.txt")) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        registeredUsers.add(line);
                    }
                }
            } else {
                logger.severe("registeredUsers.txt non trovato o non accessibile");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura di registeredUsers.txt", e);
        }

    }

    public static boolean checkUser(String user){
        System.out.println("User: " + user);
        return registeredUsers.contains(user);
    }

}
