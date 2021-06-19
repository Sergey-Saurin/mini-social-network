package network.service.impl;

import network.model.entity.Person;
import network.security.jwt.JwtParam;
import network.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import network.api.request.RegisterRequest;
import network.enums.NotificationTypeCode;
import network.security.jwt.JwtTokenProvider;

@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private PersonServiceImpl personServiceImpl;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String saveNotificationSetting(NotificationTypeCode notificationTypeCode, Boolean enable) {
        String message = "";
        Person curPerson = getCurrentPerson();
        // пока нет данных о notificationSettings
//        notificationSettingService.save(new NotificationSetting(notificationTypeCode, enable, curPerson));
        return message;
    }

    @Override
    public Person getCurrentPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        LOGGER.info("authorized user " + user.getUsername());
        return personServiceImpl.findPersonByEmail(user.getUsername());
    }

    @Override
    public String getMailByToken(String token) {
        String tokenClean = token.replace(JwtParam.TOKEN_BEARER_PREFIX, "");
        LOGGER.info("tokenClean: " + tokenClean);
        String email = jwtTokenProvider.getUserEmailFromToken(tokenClean);
        LOGGER.info("getMailByToken(): " + email);
        return email;
    }

    @Override
    public Person findPersonByEmail(String email) {
        return personServiceImpl.findPersonByEmail(email);
    }

    @Override
    public Person addNewPerson(RegisterRequest registerRequest) {
        return personServiceImpl.add(registerRequest);
    }

    @Override
    public boolean sendEmailToPerson(String email) {
        return emailServiceImpl.sendMessage(email);
    }

    @Override
    public String changePersonPassword(String token, String password) {
        LOGGER.info("token: " + token);
        String email = getMailByToken(token);
        LOGGER.info("changePersonPassword() for email: " + email);
        return personServiceImpl.changePassword(email, password);
    }

    @Override
    public String changePersonEmail(String newMail) {
        Person curPerson = getCurrentPerson();
        return personServiceImpl.changeEmail(curPerson, newMail);
    }

    @Override
    public String registerNewUser(RegisterRequest registerRequest) {
        if (!registerRequest.getPasswd1().equals(registerRequest.getPasswd2())) {
            return String.format("Passwords are not equals");
        }

        String email = registerRequest.getEmail();
        Person curPerson =  findPersonByEmail(email);
        if (curPerson != null) {
            return String.format("User with email %s already exists", email);
        }
        else{
            addNewPerson(registerRequest);
            return String.format("New user in DB with email %s", email);
        }
    }
}
