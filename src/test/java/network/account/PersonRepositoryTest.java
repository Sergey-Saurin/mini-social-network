package network.account;

import network.model.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import network.repository.PersonRepository;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreatePerson(){
        Person person = new Person("Vlad", "123456", "test@mail.ru");
        Person newPerson = personRepository.save(person);

        Person existsUser = entityManager.find(Person.class, newPerson.getId());

        assertThat(existsUser.getFirstName()).isEqualTo(person.getFirstName());
    }

    @Test
    public void testFindByEmail(){
        String email = "mail@mail.ru";
        Person curPerson = personRepository.findByEmail(email);
        assertThat(curPerson).isNotNull();
    }

    @Test
    public void testFindById(){
        Person curPerson = personRepository.findById(1L);

        assertThat(curPerson).isNotNull();
        assertThat(curPerson.getFirstName()).isEqualTo("Petr");
    }
}
