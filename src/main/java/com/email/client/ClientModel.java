package com.email.client;
import com.email.email.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientModel {
    private ObservableList<Email> emailList;
    private String user;

    public ClientModel() {

        emailList = FXCollections.observableArrayList();

        loadEmailsFromServer();
    }

    private void loadEmailsFromServer() {
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente1@example.com", "destinatario@example.com", "Oggetto 1", "Testo dell'email 1"));
        emailList.add(new Email("mittente2@example.com", "destinatario@example.com", "Oggetto 2", "Testo dell'email 2"));
        emailList.add(new Email("mittente3@example.com", "destinatario@example.com", "Oggetto 3", "Testo dell'email 3"));
        emailList.add(new Email("mittente6@example.com", "destinatario@example.com", "Oggetto 3", "Arefdddddddddddddddddd" +
                "fdddddddddddfdddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "dfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                "rwewerrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                "3e4rrtrtrtrtrtrtrtrtrtrtrttsdddddddddddddddd" +
                "sdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
                "sssssssssssssssssssssss3ewwerweerer" +
                "erer4e"));
    }

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    public void sendEmail(String destinatario, String oggetto, String testo) {
        Email email = new Email("mittente@example.com", destinatario, oggetto, testo);
        emailList.add(email);
    }

    public void setUser(String username) {
        this.user = username;
    }

    public String getUser() {
        return user;
    }
}
