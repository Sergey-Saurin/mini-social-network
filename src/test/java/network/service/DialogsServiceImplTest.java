package network.service;

import network.api.response.*;
import network.enums.ReadStatus;
import network.model.entity.Dialog;
import network.model.entity.Message;
import network.model.entity.Person;
import network.repository.Person2DialogRepository;
import network.service.impl.AccountServiceImpl;
import network.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import network.ServiceTestConfiguration;
import network.api.request.DialogRequest;
import skillbox.javapro11.api.response.*;
import network.enums.PermissionMessage;
import network.model.entity.Person2Dialog;
import network.repository.DialogRepository;
import network.repository.MessageRepository;
import network.repository.PersonRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = ServiceTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Dialog service class test:")
public class DialogsServiceImplTest {

    private final LocalDateTime localDateTime = LocalDateTime.of(2021, 1, 1, 12, 0);
    private final LocalDate localDate = LocalDate.of(2021, 1, 1);
    private int offset = 0;
    private int itemPerPage = 20;
    Person currentPerson;

    @MockBean
    private AccountServiceImpl accountServiceImpl;

    @MockBean
    private ProfileServiceImpl profileService;

    @MockBean
    private Person2DialogRepository person2DialogRepository;

    @Autowired
    private DialogsService dialogsService;

    @MockBean
    private DialogRepository dialogRepository;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private PersonRepository personRepository;

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
    @DisplayName("Getting dialogs")
    void getDialogsTest() {

        //Dialog
        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        List<Dialog> dialogList = new ArrayList<>();
        dialogList.add(dialog);

        PageImpl<Dialog> dialogPage = new PageImpl<>(dialogList);

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Mockito.when(dialogRepository.getDialogsOfPerson(pageable, currentPerson)).thenReturn(dialogPage);

        //Message
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.getLastMessageOfDialog(dialog)).thenReturn(message);


        CommonListResponse cListResponse = dialogsService.getDialogs(offset, itemPerPage, "");
        ResponseData dialogExpects = cListResponse.getData().get(0);
        MessageResponse lastMessageExpects = ((DialogResponse) dialogExpects).getLastMessage();
        String textExpect = lastMessageExpects.getMessageText();

        assertEquals("result", textExpect, "text");
    }

    @Test
    @DisplayName("Getting dialogs by query")
    void getDialogsWithQueryTest() {

        //Dialog
        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        List<Dialog> dialogList = new ArrayList<>();
        dialogList.add(dialog);

        PageImpl<Dialog> dialogPage = new PageImpl<>(dialogList);

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        //Message
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.getLastMessageOfDialog(dialog)).thenReturn(message);

        Mockito.when(dialogRepository.getDialogsOfPersonWithQuery(pageable, "tex", currentPerson)).thenReturn(dialogPage);


        CommonListResponse cListResponse = dialogsService.getDialogs(offset, itemPerPage, "tex");
        ResponseData dialogExpects = cListResponse.getData().get(0);
        MessageResponse lastMessageExpects = ((DialogResponse) dialogExpects).getLastMessage();
        String textExpect = lastMessageExpects.getMessageText();

        assertEquals("result", textExpect, "text");
    }

    @Test
    @DisplayName("Quantity unread message of person")
    void getQuantityUnreadMessageOfPersonTest() {

        Mockito.when(messageRepository.getUnreadCountOfPerson(currentPerson)).thenReturn(20);

        CommonResponseData commonResponseData = dialogsService.getQuantityUnreadMessageOfPerson();
        ResponseData responseData = commonResponseData.getData();
        int count = ((CountMessageResponse) responseData).getCount();

        assertEquals("result", count, 20);
    }

    @Test
    @DisplayName("add User Into Dialog")
    void addUserIntoDialogTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Mockito.when(dialogRepository.findById(1)).thenReturn(dialog);

        DialogRequest dialogRequest = new DialogRequest();
        int[] userIds = new int[1];
        userIds[0] = 1;
        dialogRequest.setUsersIds(userIds);

        Mockito.when(personRepository.findById(1)).thenReturn(currentPerson);

        List<Person2Dialog> listP2D = new ArrayList<>();
        Mockito.when(person2DialogRepository.saveAll(listP2D)).thenReturn(null);

        ResponseArrayUserIds responseArrayUserIds = dialogsService.addUserIntoDialog(1, dialogRequest);
        int responseData = Math.toIntExact(responseArrayUserIds.getUsersIds().get(0));

        assertEquals("result", responseData, 1);
    }

    @Test
    @DisplayName("get Invite Dialog")
    void getInviteDialogTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Mockito.when(dialogRepository.getInviteByDialog(1)).thenReturn("invite");

        CommonResponseData commonResponseData = dialogsService.getInviteDialog(1);
        ResponseData responseData = commonResponseData.getData();
        String linkResponse = ((LinkResponse) responseData).getLink();

        assertEquals("result", linkResponse, "invite");
    }

    @Test
    @DisplayName("get Message Of Dialog")
    void getMessageOfDialogTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);

        PageImpl<Message> messagePage = new PageImpl<>(messageList);

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
//        Mockito.when(messageRepository.getMessageOfDialog(pageable, "", 1)).thenReturn(messagePage);

        CommonListResponse cListResponse = dialogsService.getMessageOfDialog(1, offset, itemPerPage, "");
        ResponseData responseData = cListResponse.getData().get(0);
        String textExpect = ((MessageResponse) responseData).getMessageText();

        assertEquals("result", textExpect, "text");
    }

    @Test
    @DisplayName("delete Message")
    void deleteMessageTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.findByIdAndDialog(1, 1)).thenReturn(message);
        Mockito.when(messageRepository.save(message)).thenReturn(null);

        CommonResponseData commonResponseData = dialogsService.deleteMessage(1, 1);
        ResponseData responseData = commonResponseData.getData();
        int expect = (int) ((IdMessageResponse) responseData).getMessageId();

        assertEquals("result", expect, 1);
    }

    @Test
    @DisplayName("recover Message")
    void recoverMessageTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.findByIdAndDialog(1, 1)).thenReturn(message);
        Mockito.when(messageRepository.save(message)).thenReturn(null);

        CommonResponseData commonResponseData = dialogsService.recoverMessage(1, 1);
        ResponseData responseData = commonResponseData.getData();
        String expect = ((MessageResponse) responseData).getMessageText();

        assertEquals("result", expect, "text");
    }

    @Test
    @DisplayName("get Status And Last Activity")
    void getStatusAndLastActivityTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Mockito.when(dialogRepository.findById(1)).thenReturn(dialog);
        Mockito.when(personRepository.findById(1)).thenReturn(currentPerson);
        Mockito.when(messageRepository.getTheTimeOfTheLastMessageOfTheDialogFromThePerson(dialog, currentPerson)).thenReturn(localDateTime);

        CommonResponseData commonResponseData = dialogsService.getStatusAndLastActivity(1, 1);
        ResponseData responseData = commonResponseData.getData();
        Long expect = ((UserStatusResponse) responseData).getLastActivity();

        assertEquals("result", expect, localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli());
    }

    @Test
    @DisplayName("create Dialog with Person (by Id)")
    void createDialogTest() {
        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Mockito.when(dialogsService.createNewDialog(currentPerson)).thenReturn(dialog);

        DialogRequest dialogRequest = new DialogRequest();
        int[] userIds = new int[1];
        userIds[0] = 1;
        dialogRequest.setUsersIds(userIds);

        Mockito.when(personRepository.findById(1)).thenReturn(currentPerson);

        CommonResponseData commonResponseData = dialogsService.createDialog(dialogRequest);
        DialogResponse responseData = (DialogResponse) commonResponseData.getData();
        int expect = (int) responseData.getId();

        assertEquals("result", expect, 1);
    }

    @Test
    @DisplayName("delete Dialog")
    void deleteDialogTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Mockito.when(dialogRepository.findById(1)).thenReturn(dialog);

        CommonResponseData commonResponseData = dialogsService.deleteDialog(1);
        DialogResponse dialogResponse = (DialogResponse) commonResponseData.getData();
        long expect = dialogResponse.getId();

        assertEquals("result", expect, 1L);
    }

    @Test
    @DisplayName("delete Users in Dialog")
    void deleteUsersInDialogTest() {

        String[] userIds = {"1", "2", "3"};

        CommonResponseData commonResponseData = dialogsService.deleteUsersInDialog(1, userIds);
        DialogUserShortListResponse expectData = (DialogUserShortListResponse) commonResponseData.getData();
        List<Long> expect = expectData.getUserIds();

        DialogUserShortListResponse dialogData = new DialogUserShortListResponse();
        List<Long> actualUserIds = new ArrayList<>();
        actualUserIds.add(1L);
        actualUserIds.add(2L);
        actualUserIds.add(3L);
        dialogData.setUserIds(actualUserIds);
        assertEquals("result", expect, actualUserIds);
    }

    @Test
    @DisplayName("Join to Dialog using the invitation link")
    void joinToDialogTest() {
        List<Long> actualUserIds = new ArrayList<>();
        actualUserIds.add(1L);

        CommonResponseData commonResponseData = dialogsService.joinToDialog(1, "link");
        DialogUserShortListResponse expectData = (DialogUserShortListResponse) commonResponseData.getData();
        List<Long> expect = expectData.getUserIds();

        assertEquals("result", expect, actualUserIds);
    }

    @Test
    @DisplayName("Send Message")
    void sendMessageTest() {

        String message = "text";

        CommonResponseData commonResponseData = dialogsService.sendMessage(1, "text");
        MessageResponse expectData = (MessageResponse) commonResponseData.getData();
        String expect = expectData.getMessageText();

        assertEquals("result", expect, message);
    }

    @Test
    @DisplayName("Edit Message")
    void editMessageTest() {

        String messageText = "newText";

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.findById(1)).thenReturn(message);

        CommonResponseData commonResponseData = dialogsService.editMessage(1, 1, messageText);
        MessageResponse expectData = (MessageResponse) commonResponseData.getData();
        String expect = expectData.getMessageText();

        assertEquals("result", expect, messageText);
    }

    @Test
    @DisplayName("Read Message")
    void readMessageTest() {

        Dialog dialog = new Dialog(1, currentPerson, false, "invite");
        Message message = new Message(1, localDateTime, currentPerson, currentPerson, "text", dialog, ReadStatus.SENT, false);
        Mockito.when(messageRepository.findById(1)).thenReturn(message);

        CommonResponseData commonResponseData = dialogsService.readMessage(1, 1);
        MessageResponse expectData = (MessageResponse) commonResponseData.getData();
        String expectMessage = expectData.getMessage();

        assertEquals("result", expectMessage, "ok");

    }

    @Test
    @DisplayName("Change status activity")
    void changeStatusActivityTest() {

        CommonResponseData commonResponseData = dialogsService.changeStatusActivity(1, 1);
        StatusMessageResponse expectData = (StatusMessageResponse) commonResponseData.getData();
        String expectMessage = expectData.getMessage();

        assertEquals("result", expectMessage, "ok");
    }


}


















