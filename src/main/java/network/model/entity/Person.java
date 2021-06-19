package network.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import network.api.request.RegisterRequest;
import network.enums.PermissionMessage;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "birth_date")
    private LocalDate birthday;

    private String email; // should be not null? should be unique?

    private String phone;

    @Column(nullable = false)
    private String password;

    private String photo;

    @Column(length = 2048)
    private String about;

    private String country;

    private String city;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    @Enumerated(EnumType.STRING)
    @Column(name = "messages_permission", columnDefinition = "perm_message", nullable = false)
    @ColumnTransformer(read = "messages_permission::varchar", write = "?::perm_message")
    private PermissionMessage permissionMessage;

    @Column(name = "last_online_time", nullable = false)
    private LocalDateTime lastTimeOnline;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    public Person(RegisterRequest registerRequest) {
        this.email = registerRequest.getEmail();
        this.password = registerRequest.getPasswd1();
        this.firstName = registerRequest.getFirstName();
        this.lastName = registerRequest.getLastName();
        this.registrationDate = LocalDateTime.now();
        this.lastTimeOnline = LocalDateTime.now();
        this.birthday = null;
        this.isApproved = true;
        this.permissionMessage = PermissionMessage.valueOf("ALL");
        this.isBlocked = false;
    }

    // only mandatory fields
    public Person(String firstName, String password) {
        this.password = password;
        this.firstName = firstName;
        this.registrationDate = LocalDateTime.now();
        this.lastTimeOnline = LocalDateTime.now();
        this.isApproved = true;
        this.permissionMessage = PermissionMessage.valueOf("ALL");
        this.isBlocked = false;
    }

    public Person(String firstName, String password, String email) {
        this.password = new BCryptPasswordEncoder().encode(password);
        this.firstName = firstName;
        this.email = email;
        this.registrationDate = LocalDateTime.now();
        this.lastTimeOnline = LocalDateTime.now();
        this.isApproved = true;
        this.permissionMessage = PermissionMessage.valueOf("ALL");
        this.isBlocked = false;
    }
}
