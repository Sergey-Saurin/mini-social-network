package network.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import network.model.entity.Person;
import network.repository.util.Utils;
import network.enums.PermissionMessage;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse extends ResponseData {

    private long id;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "reg_date")
    private Long registrationDate;

    @JsonProperty(value = "birth_date")
    private Long birthDate;

    private String email;

    private String phone;

    private String photo;

    private String about;

    private String city;

    private String country;

    @JsonProperty(value = "messages_permission")
    private PermissionMessage messagesPermission;

    @JsonProperty(value = "last_online_time")
    private Long lastOnlineTime;

    @JsonProperty(value = "is_blocked")
    private boolean isBlocked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    public static PersonResponse fromPerson(Person person) {
        return  fromPerson(person, null);
    }

    public static PersonResponse fromPerson(Person person, String token) {
        return new PersonResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                Utils.getLongFromLocalDateTime(person.getRegistrationDate()),
                Utils.getLongFromLocalDate(person.getBirthday()),
                person.getEmail(),
                person.getPhone(),
                person.getPhoto(),
                person.getAbout(),
                person.getCity(),
                person.getCountry(),
                person.getPermissionMessage(),
                Utils.getLongFromLocalDateTime(person.getLastTimeOnline()),
                person.isBlocked(),
                token
        );
    }

    public static List<PersonResponse> fromPersonList(List<Person> personList) {
        List<PersonResponse> personResponseList = new ArrayList<>();
        personList.forEach(person -> personResponseList.add(fromPerson(person)));
        return personResponseList;
    }
}
