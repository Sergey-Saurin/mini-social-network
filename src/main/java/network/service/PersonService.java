package network.service;

import network.api.request.RegisterRequest;
import network.model.entity.Person;

public interface PersonService {

    Person findPersonByEmail(String email);

    Person save(Person person);

    Person add(RegisterRequest registerRequest);

    String changePassword(String email, String password);

    String changeEmail(Person curPerson, String email);

    Person findById(long srcId);
}
