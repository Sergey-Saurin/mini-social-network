package network.service;

public interface EmailSenderService {

    void sendSimpleMessage(String toEmail, String text, String subject);
}
