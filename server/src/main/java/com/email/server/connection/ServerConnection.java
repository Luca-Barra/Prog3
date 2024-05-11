package com.email.server.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    /**
     * Costruttore della classe ServerConnection
     * <p>
     * @param clientSocket Socket del client con cui il server si è connesso
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */

    public ServerConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Metodo per inviare un oggetto al client
     * <p>
     * @param object Oggetto da inviare al client
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */

    public void sendToClient(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    /**
     * Metodo per ricevere un oggetto dal client
     * <p>
     * @return Oggetto ricevuto dal client
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     * @throws ClassNotFoundException Eccezione lanciata in caso di errore di classi
     */

    public Object receiveFromClient() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    /**
     * Metodo per chiudere la connessione con il client
     * <p>
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */

    public void close() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }

    /**
     * Metodo per ottenere l'indirizzo IP e la porta del client
     * <p>
     * @return Indirizzo IP e porta del client
     */

    @Override
    public String toString() {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
}
