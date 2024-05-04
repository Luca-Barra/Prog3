package com.email.server.controller;

import com.email.client.utils.MyAlert;
import com.email.server.model.ServerModel;
import com.email.server.utils.LogEntry;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServerController {

    @FXML
    private TableView<LogEntry> logTableView;

    @FXML
    private TableColumn<LogEntry, String> columnUtente;

    @FXML
    private TableColumn<LogEntry, String> columnMessaggio;

    @FXML
    private TableColumn<LogEntry, String> data;


    /**
     * Metodo che inizializza la tabella dei log
     */

    @FXML
    public void initialize() {

        initUtenteColumn();
        initMessaggioColumn();
        initDateColumn();
        initLogTable();

        loadUsers();

    }

    /**
     * Metodo che inizializza la colonna utente
     */

    private void initUtenteColumn() {
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
    }

    /**
     * Metodo che inizializza la colonna messaggio
     */

    private void initMessaggioColumn() {columnMessaggio.setCellValueFactory(new PropertyValueFactory<>("messaggio"));}

    /**
     * Metodo che inizializza la colonna data
     */

    private void initDateColumn() {
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
    }

    /**
     * Metodo che inizializza la tabella email
     */

    private void initLogTable() {
        logTableView.setRowFactory(tv -> {
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
        logTableView.setItems(ServerModel.logEntries);

    }

    /**
     * Metodo che carica gli utenti registrati
     */

    private void loadUsers() {ServerModel.loadRegisteredUsers();}

}
