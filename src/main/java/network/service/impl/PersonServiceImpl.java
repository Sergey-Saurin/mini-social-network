package network.service.impl;

import network.model.entity.Person;
import network.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import network.api.request.RegisterRequest;
import network.repository.PersonRepository;


@Service
public class PersonServiceImpl implements PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonServiceImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Person findPersonByEmail(String email) {
        LOGGER.info("findPersonByEmail " + email);
        return personRepository.findByEmail(email);
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person add(RegisterRequest registerRequest) {
        Person newPerson = new Person(registerRequest);
        newPerson.setPassword(passwordEncoder.encode(registerRequest.getPasswd1()));
        LOGGER.info("new Person in DB: " + registerRequest.getEmail());
        return personRepository.save(newPerson);
    }

    @Override
    public String changePassword(String email, String password) {
        String message = ""; // for checking error if necessary
        Person curPerson = findPersonByEmail(email);
        LOGGER.info("curPerson in DB: " + curPerson);
        LOGGER.info("new password: " + password);
        curPerson.setPassword(passwordEncoder.encode(password));
        save(curPerson);
        return message;
    }

    @Override
    public String changeEmail(Person curPerson, String email) {
        String message = "";// for checking error if necessary
        curPerson.setEmail(email);
        save(curPerson);
        LOGGER.info("new email: " + email);
        return message;
    }

    @Override
    public Person findById(long id) {
        return personRepository.findById(id);
    }
}
