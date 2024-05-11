package com.email.client.models;

import com.email.client.connection.ClientConnection;
import com.email.client.utils.MyAlert;

import java.io.*;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoginModel {
    private static final Logger logger = Logger.getLogger(LoginModel.class.getName());
    String user;
    String password;
    public boolean check = false;
    HashMap<String, String> data = new HashMap<>();

    /**
     * Costruttore della classe LoginModel
     * <p>
     * @param user username
     * @param password password
     */

    public LoginModel(String user, String password) throws IOException {
        this.user = user;
        this.password = password;
        this.fill();
    }

    /**
     * Metodo per la connessione al server
     * <p>
     * Il metodo si connette al server e tenta di caricare i dati di login da un file.
     * Se la risposta del server è "OK" allora i dati vengono caricati correttamente.
     */

    private void fill() throws IOException {
        ClientConnection connection = null;
            try {
                connection = new ClientConnection();
                connection.sendToServer("LOGIN");
                String serverResponse = connection.receiveFromServer().toString();

                if (serverResponse.equals("OK")) {
                    connection.sendToServer(user);
                    check = true;
                    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("login-info.txt")) {
                        if (inputStream != null) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    String[] parts = line.split(",");
                                    if (parts.length == 2) {
                                        String key = parts[0].trim();
                                        String value = parts[1].trim();
                                        data.put(key, value);
                                    } else {
                                        logger.warning("Invalid data format in the file: " + line);
                                    }
                                }
                            }
                        } else {
                            logger.severe("login-info.txt not found or not accessible");
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Error reading login-info.txt", e);
                    }
                }
            } catch (ConnectException e) {
                System.out.println("Impossibile connettersi al server.");
                MyAlert.warning("Errore di connessione", "Impossibile connettersi al server", "Il server è down.");
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Error during login", e);
            }
            finally {
                if (connection != null) {
                    connection.close();
                }
            }
    }

    /**
     * Metodo per il controllo dei dati di login
     * <p>
     * Il metodo controlla se l'utente esiste e se la password è corretta.
     * <p>
     * @return true se l'utente esiste e la password è corretta, false altrimenti
     */

    public boolean check(){
        if (!data.containsKey(user)) {
            System.err.println("Utente non esistente");
            MyAlert.error("Errore", "Errore di autenticazione", "Utente non esistente.");
            return false;
        }

        if(Objects.equals(data.get(user), password)){
            System.out.println("Password corretta");
            return true;
        } else {
            System.err.println("Password errata");
            MyAlert.error("Errore", "Errore di autenticazione", "Username o password errati.");
        }
        return false;

    }

}