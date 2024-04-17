package com.email.server.support;

import com.email.Email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MailSupport {

    public synchronized static List<Email> getEmails(String mailboxFileName) throws IOException {
        List<Email> emailList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(mailboxFileName))) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                String[] parts = sb.toString().split(";");
                if (parts.length == 5) {
                    Email email = new Email(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    emailList.add(email);
                    sb = new StringBuilder();
                } else {
                    sb.append("\n");
                }
            }
        }
        return emailList;
    }

    public static String getMailboxFileName(String user) {
        return "/home/luna/IdeaProjects/Project-Prog-3/server/src/main/resources/com/email/server/servermailbox/SENT/" + user + ".txt";
    }

}
