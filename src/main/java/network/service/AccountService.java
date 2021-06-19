package network.service;

import network.api.request.RegisterRequest;
import network.enums.NotificationTypeCode;
import network.model.entity.Person;


public interface AccountService {

    String saveNotificationSetting(NotificationTypeCode notificationTypeCode, Boolean enable);

    Person getCurrentPerson();

    String getMailByToken(String token);

    Person findPersonByEmail(String email);

    Person addNewPerson(RegisterRequest registerRequest);

    boolean sendEmailToPerson(String email);

    String changePersonPassword(String token, String password);

    String changePersonEmail(String email);

    String registerNewUser(RegisterRequest registerRequest);
}
