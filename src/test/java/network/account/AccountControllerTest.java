package network.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.api.request.NotificationsRequest;
import network.api.request.SetPasswordRequest;
import network.model.entity.Person;
import network.security.userdetails.UserDetailsServiceImpl;
import network.service.AccountService;
import network.service.PersonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import network.api.request.PasswordRequest;
import network.api.request.RegisterRequest;
import network.api.response.CommonResponse;
import network.controller.AccountController;
import network.enums.NotificationTypeCode;
import network.repository.PersonRepository;
import network.security.jwt.JwtTokenProvider;


@WebMvcTest(AccountController.class)
@WithMockUser
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @Test
    public void testPersonRegister() throws Exception {
        RegisterRequest newPersonRegisterRequest = new RegisterRequest(
                "moymail@mail.ru",
                "password", "password",
                "Vasya", "Petrov", "code");
        Person newPerson = new Person(newPersonRegisterRequest);

        Person savedPerson = new Person();
        savedPerson.setEmail("moymail@mail.ru");
        savedPerson.setEmail("password");
        savedPerson.setFirstName("Vasya");
        savedPerson.setLastName("Petrov");

        CommonResponse response = new CommonResponse("");

        Mockito.when(accountService.addNewPerson(newPersonRegisterRequest))
                .thenReturn(savedPerson);

        mockMvc.perform(
                post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPersonExists() throws Exception {
        RegisterRequest newPersonRegisterRequest = new RegisterRequest(
                "moymail@mail.ru",
                "password", "password",
                "Vasya", "Petrov", "code");
        Person newPerson = new Person(newPersonRegisterRequest);

        Person personFromDB = accountService.findPersonByEmail("moymail@mail.ru");
        String msg = "";
        if (personFromDB != null) {
            msg = "User with email moymail@mail.ru already exists";
        }
        CommonResponse response = new CommonResponse(msg);

        mockMvc.perform(
                post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPasswordRecovery() throws Exception {
        String email = "moymail@mail.ru";

        mockMvc.perform(
                put("/account/password/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPasswordChange() throws Exception {
        SetPasswordRequest passwordRequest =
                new SetPasswordRequest("token", new PasswordRequest("password"));

        mockMvc.perform(
                put("/account/password/set")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testEmailChange() throws Exception {
        String email = "moymail@mail.ru";

        mockMvc.perform(
                put("/account/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(email))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAccountNotifications() throws Exception {
        NotificationsRequest notificationsRequest =
                new NotificationsRequest(NotificationTypeCode.COMMENT_COMMENT, true);

        mockMvc.perform(
                put("/account/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationsRequest))
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andDo(MockMvcResultHandlers.print());
    }
}
