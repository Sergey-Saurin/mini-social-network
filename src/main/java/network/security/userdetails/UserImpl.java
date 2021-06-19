package network.security.userdetails;

import network.model.entity.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class UserImpl extends User {

    public UserImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public static User fromUser(Person person) {
        return new User(
                person.getEmail(),
                person.getPassword(),
                true, true, true, true,
                Collections.singleton(new SimpleGrantedAuthority("FULL")));
    }
}