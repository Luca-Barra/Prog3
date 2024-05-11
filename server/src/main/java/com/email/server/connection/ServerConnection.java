package com.email.server.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
    Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public ServerConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendToClient(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    public Object receiveFromClient() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }

    public void close() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }
}
