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

    public ClientConnection() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress("localhost", 12345), 30000);
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendToServer(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    public Object receiveFromServer() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    public void close() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }

}
