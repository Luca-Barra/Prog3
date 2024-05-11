package com.email.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientConnection {

    Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    /**
     * Costruttore della classe ClientConnection.
     * <p>
     * @throws IOException se si verificano errori di I/O
     */

    public ClientConnection() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", 12345), 30000);
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Metodo per inviare un oggetto al server.
     * <p>
     * @param object oggetto da inviare al server
     * @throws IOException se si verificano errori di I/O
     */

    public void sendToServer(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    /**
     * Metodo per ricevere un oggetto dal server.
     * <p>
     * @return oggetto ricevuto dal server
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se la classe dell'oggetto ricevuto non è stata trovata
     */

    public Object receiveFromServer() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    /**
     * Metodo per chiudere la connessione con il server.
     * <p>
     * @throws IOException se si verificano errori di I/O
     */

    public void close() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }

}
