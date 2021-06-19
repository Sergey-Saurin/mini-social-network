package network.service.impl;

import network.model.entity.Person;
import network.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import network.controller.AccountController;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private PersonServiceImpl personServiceImpl;

    @Autowired
    private EmailSenderServiceImpl emailSenderService;

    @Override
    public boolean sendMessage(String email) {
        if (email.isBlank()){
            LOGGER.info("email is blank");
            return false;
        }
        Person personFromDB = personServiceImpl.findPersonByEmail(email);
        if (personFromDB == null){
            LOGGER.info("Person with email " + email + " not found");
            return false;
        }

        emailSenderService.sendSimpleMessage(email, "text", "subject");
        return true;
    }
}
