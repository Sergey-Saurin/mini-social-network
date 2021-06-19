package network.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.model.entity.Person;
import network.security.userdetails.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import network.api.request.AuthRequest;
import network.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    private String USER_EMAIL = "mail@mail.ru";
    private String USER_PASS = "111t111";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    PersonRepository personRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Аутентификация выполнена успешно
     */
    @Test
    public void testShouldLogInUser() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(new AuthRequest(USER_EMAIL, USER_PASS));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("FULL"));
        User user = new User(USER_EMAIL, passwordEncoder.encode(USER_PASS), authorities);

        given(userDetailsServiceImpl.loadUserByUsername(USER_EMAIL)).willReturn(user);

        Person person = new Person();
        LocalDateTime now = LocalDateTime.now();
        person.setBirthday(now.toLocalDate());
        person.setLastTimeOnline(now);
        person.setRegistrationDate(now);
        Mockito.when(personRepository.findByEmail(USER_EMAIL)).thenReturn(person);

        mockMvc
                .perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Аутентификация не выполнена, не верный пароль, не кодируется
     */
    @Test
    public void testShouldFailLogInUserWithBadCredentials() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(new AuthRequest(USER_EMAIL, USER_PASS));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("FULL"));

        User user = new User(USER_EMAIL, USER_PASS, authorities);
        given(userDetailsServiceImpl.loadUserByUsername(USER_EMAIL)).willReturn(user);
        String message = "";
        try {
            mockMvc
                    .perform(post("/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(payload)
                            .accept(APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            message = new RuntimeException(e).getMessage();
        }

        assertThat(message, is("java.lang.RuntimeException: org.springframework.security.authentication.BadCredentialsException: Bad credentials"));

    }
}