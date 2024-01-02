package com.email.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Objects;


public class TryLogin {
    private static final Logger logger = Logger.getLogger(TryLogin.class.getName());
    String user;
    String password;
    HashMap<String, String> data = new HashMap<>();

    public TryLogin(String user, String password) {
        this.user = user;
        this.password = password;
        this.fill();
    }

    private void fill() {
        System.out.println(getClass().getClassLoader().getResourceAsStream("logininfo.txt"));
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("logininfo.txt")) {
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
                logger.severe("logininfo.txt not found or not accessible");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading logininfo.txt", e);
        }
    }

    public boolean check(){
        if (!data.containsKey(user)) {System.err.println("Utente non esistente"); return false;}

        if(Objects.equals(data.get(user), password)){
            System.out.println("Password corretta");
            return true;
        } else System.err.println("Password scorretta");
        return false;

    }

}
