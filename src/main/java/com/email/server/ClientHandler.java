package com.email.server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            Object request = in.readObject();

            Object response = handleRequest(request);

            out.writeObject(response);
            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore durante la comunicazione con il client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.severe("Errore durante la chiusura del socket: " + e.getMessage());
            }
        }
    }

    // Metodo per gestire la richiesta e generare la risposta
    private Object handleRequest(Object request) {
        // Implementa la logica per gestire la richiesta e generare la risposta qui
        return "Response to the client";
    }
}
