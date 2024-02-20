package com.email.server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            // Leggi la richiesta dal client
            Object request = in.readObject();

            // Gestisci la richiesta e genera la risposta
            Object response = handleRequest(request);

            // Invia la risposta al client
            out.writeObject(response);
            out.flush(); // Assicurati che tutti i dati siano stati inviati
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo per gestire la richiesta e generare la risposta
    private Object handleRequest(Object request) {
        // Implementa la logica per gestire la richiesta e generare la risposta qui
        return "Response to the client";
    }
}
