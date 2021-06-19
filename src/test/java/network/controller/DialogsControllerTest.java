package network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.model.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import network.api.request.DialogRequest;
import network.enums.PermissionMessage;
import network.service.DialogsService;
import network.service.impl.AccountServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dialog controller test")
public class DialogsControllerTest {

    private final LocalDateTime localDateTime = LocalDateTime.of(2021, 1, 1, 12, 0);
    private final LocalDate localDate = LocalDate.of(2021, 1, 1);
    private int offset = 0;
    private int itemPerPage = 20;
    Person currentPerson;

    @MockBean
    private AccountServiceImpl accountServiceImpl;

    @MockBean
    private DialogsService dialogsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(accountServiceImpl);

        currentPerson = new Person(
                1L,
                "Ivan",
                "Ivanov",
                localDateTime, // registration date
                localDate, // birth date
                "ivanov@mail.com",
                "+7(111)222-33-44",
                "password",
                "photo",
                "about",
                "Russia",
                "Moscow",
                true,
                PermissionMessage.ALL,
                localDateTime, // last online time
                false);

        Mockito.when(accountServiceImpl.getCurrentPerson()).thenReturn(currentPerson);


    }

    @Test
    @DisplayName("Dialogs list")
    @WithMockUser
    void getDialogsListTest() throws Exception {

        //With parameters
        mockMvc.perform(get("/dialogs")
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage))
                .param("query", ""))
                .andDo(print())
                .andExpect(status().isOk());
        //No parameters
        mockMvc.perform(get("/dialogs"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create dialog")
    @WithMockUser
    void createDialogTest() throws Exception {

        DialogRequest dialogRequest = new DialogRequest();
        int[] userIds = new int[1];
        userIds[0] = 1;
        dialogRequest.setUsersIds(userIds);

        mockMvc.perform(post("/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("unreaded")
    @WithMockUser
    void getCountUnreadMessagesTest() throws Exception {

        mockMvc.perform(get("/dialogs/unreaded"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete dialog")
    @WithMockUser
    void deleteDialogTest() throws Exception {


        mockMvc.perform(delete("/dialogs/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("add User Into Dialog")
    @WithMockUser
    void addUserIntoDialogTest() throws Exception {

        DialogRequest dialogRequest = new DialogRequest();
        int[] userIds = new int[1];
        userIds[0] = 1;
        dialogRequest.setUsersIds(userIds);
        mockMvc.perform(put("/dialogs/1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete User From Dialog")
    @WithMockUser
    void deleteUserFromDialogTest() throws Exception {

        String[] usersIds = new String[1];
        usersIds[0] = "1";

        mockMvc.perform(delete("/dialogs/1/users")
                .param("users_ids", usersIds))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get invite")
    @WithMockUser
    void getInviteDialogTest() throws Exception {

        mockMvc.perform(get("/dialogs/1/users/invite"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("join User to Dialog")
    @WithMockUser
    void joinToDialogTest() throws Exception {

        DialogRequest dialogRequest = new DialogRequest();
        String link = "link";
        dialogRequest.setLink(link);
        mockMvc.perform(put("/dialogs/1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get History Of Messages")
    @WithMockUser
    void getHistoryOfMessagesTest() throws Exception {

        //With parameters
        mockMvc.perform(get("/dialogs/1/messages")
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage))
                .param("query", ""))
                .andDo(print())
                .andExpect(status().isOk());
        //No parameters
        mockMvc.perform(get("/dialogs/1/messages"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("send Message")
    @WithMockUser
    void sendMessagesTest() throws Exception {

        DialogRequest dialogRequest = new DialogRequest();
        String message = "text";
        dialogRequest.setMessageText(message);
        mockMvc.perform(post("/dialogs/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("delete Message")
    @WithMockUser
    void deleteMessageTest() throws Exception {

        mockMvc.perform(delete("/dialogs/1/messages/1"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("edit Message")
    @WithMockUser
    void editMessageTest() throws Exception {

        DialogRequest dialogRequest = new DialogRequest();
        String message = "text";
        dialogRequest.setMessageText(message);
        mockMvc.perform(put("/dialogs/1/messages/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dialogRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("recover Message")
    @WithMockUser
    void recoverMessageTest() throws Exception {

        mockMvc.perform(put("/dialogs/1/messages/1/recover"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("read Message")
    @WithMockUser
    void readMessageTest() throws Exception {

        mockMvc.perform(put("/dialogs/1/messages/1/read"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get Status")
    @WithMockUser
    void getStatusTest() throws Exception {

        mockMvc.perform(get("/dialogs/1/activity/1"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("change status Message")
    @WithMockUser
    void changeStatusTest() throws Exception {

        mockMvc.perform(post("/dialogs/1/activity/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
