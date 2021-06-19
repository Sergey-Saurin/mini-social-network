package network.account;

import network.model.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import network.api.request.RegisterRequest;
import network.repository.PersonRepository;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountServiceImplTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testAddNewPerson(){
        RegisterRequest registerRequest = new RegisterRequest("mymail@mail.ru",
                "password", "password", "Petr", "Petrov", "123456");
        Person newPerson = new Person(registerRequest);
        personRepository.save(newPerson);

        assertThat(newPerson).isNotNull();
        assertThat(newPerson.getEmail()).isEqualTo("mymail@mail.ru");
    }

    @Test
    public void testEmailChange() {
        String emailOld = "mymail@mail.ru";
        String emailNew = "moymail@mail.ru";

        RegisterRequest registerRequest = new RegisterRequest(emailOld,
                "password", "password", "Petr", "Petrov", "123456");
        Person newPerson = new Person(registerRequest);
        personRepository.save(newPerson);

        Person savedPerson = personRepository.findByEmail(emailOld);
        savedPerson.setEmail(emailNew);

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getEmail()).isNotNull();
        assertThat(savedPerson.getEmail()).isEqualTo(emailNew);
    }

    @Test
    public void testPasswordChange() {
        String mail = "moymail@mail.ru";
        String pasOld = "pasOld";
        String pasNew = "pasNew";

        RegisterRequest registerRequest = new RegisterRequest(mail,
                pasOld, pasOld, "Petr", "Petrov", "123456");
        Person newPerson = new Person(registerRequest);
        personRepository.save(newPerson);

        Person savedPerson = personRepository.findByEmail(mail);
        savedPerson.setPassword(pasNew);

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getPassword()).isNotNull();
        assertThat(savedPerson.getPassword()).isEqualTo(pasNew);
    }
}
