package com.email.server;

import com.email.email.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerModel {
    private List<Email> emailList;
    private ExecutorService executorService;

    public ServerModel() {
        emailList = new ArrayList<>();
        executorService = Executors.newCachedThreadPool();
    }

    public void addEmail(Email email) {
        emailList.add(email);
        forwardEmail(email);
    }

    private void forwardEmail(Email email) {
        executorService.execute(() -> {
            System.out.println("Inoltro dell'email: " + email);

            List<String> destinatari = email.getDestinatari();
            for (String destinatario : destinatari) {
                System.out.println("Inoltra email a: " + destinatario);
            }
        });
    }


    public void shutdown() {
        executorService.shutdown();
    }
}
