package network.service.impl;

import network.api.response.*;
import network.enums.ReadStatus;
import network.model.entity.Dialog;
import network.model.entity.Message;
import network.model.entity.Person;
import network.repository.Person2DialogRepository;
import network.repository.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import network.api.request.DialogRequest;
import skillbox.javapro11.api.response.*;
import network.model.entity.Person2Dialog;
import network.repository.DialogRepository;
import network.repository.MessageRepository;
import network.repository.PersonRepository;
import network.service.DialogsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by timur_guliev on 27.04.2021.
 */
@Service
public class DialogsServiceImpl implements DialogsService {

    private final AccountServiceImpl accountServiceImpl;
    private final DialogRepository dialogRepository;
    private final Person2DialogRepository person2DialogRepository;
    private final PersonRepository personRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public DialogsServiceImpl(AccountServiceImpl accountServiceImpl, DialogRepository dialogRepository, Person2DialogRepository person2DialogRepository,
                              PersonRepository personRepository, MessageRepository messageRepository) {
        this.accountServiceImpl = accountServiceImpl;
        this.dialogRepository = dialogRepository;
        this.person2DialogRepository = person2DialogRepository;
        this.personRepository = personRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public CommonResponseData createDialog(DialogRequest dialogRequest) {
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        DialogResponse dialogData = new DialogResponse();
        Dialog newDialog = createNewDialog(currentPerson);
        createPerson2Dialog(currentPerson, newDialog);
        createPerson2Dialog(personRepository.findById(dialogRequest.getUsersIds()[0]), newDialog);
        dialogData.setId(newDialog.getId());
        createMessage("message", currentPerson, newDialog.getId());
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData deleteDialog(long id) {
        Dialog dialog = dialogRepository.findById(id);
        if (dialog == null)
            return new CommonResponseData(null, "диалог не найден");
        dialog.setDeleted(false);
        dialogRepository.save(dialog);
        DialogResponse dialogData = new DialogResponse();
        dialogData.setId(id);
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public Dialog createNewDialog(Person ownerDialog){
        Dialog dialog = new Dialog();
        dialog.setOwner(ownerDialog);
        return dialogRepository.save(dialog);
    }

    @Override
    public CommonResponseData deleteUsersInDialog(long idDialog, String[] usersIds) {
        for (int i = 0; i < usersIds.length; i++) {
            person2DialogRepository.deletePersInDialog(idDialog, Long.parseLong(usersIds[i]));
        }
        DialogUserShortListResponse dialogData = new DialogUserShortListResponse();
        dialogData.setUserIds(Arrays.stream(usersIds).map(Long::parseLong).collect(Collectors.toList()));
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData joinToDialog(long idDialog, String link) {
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        List<Long> usersIds = new ArrayList<>();
        usersIds.add(currentPerson.getId());
        createPerson2Dialog(currentPerson, dialogRepository.findById(idDialog));
        DialogUserShortListResponse dialogData = new DialogUserShortListResponse();
        dialogData.setUserIds(usersIds);
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData sendMessage(long idDialog, String messageText) {
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        createMessage(messageText, currentPerson, idDialog);
        MessageResponse dialogData = getMessageResponse(messageText, currentPerson);
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData editMessage(long idDialog, long idMessage, String messageText) {
        Message message = messageRepository.findById(idMessage);
        message.setText(messageText);
        messageRepository.save(message);
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        MessageResponse dialogData = getMessageResponse(messageText, currentPerson);
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData readMessage(long idDialog, long idMessage) {
        Message message = messageRepository.findById(idMessage);
        message.setReadStatus(ReadStatus.READ);
        messageRepository.save(message);
        MessageResponse dialogData = new MessageResponse();
        dialogData.setMessage("ok");
        return new CommonResponseData(dialogData, "");
    }

    @Override
    public CommonResponseData changeStatusActivity(long idDialog, long idUser) {
        StatusMessageResponse statusMessageResponse = new StatusMessageResponse();
        statusMessageResponse.setMessage("ok");
        return new CommonResponseData(statusMessageResponse, "");
    }

    private MessageResponse getMessageResponse(String messageText, Person currentPerson) {
        MessageResponse dialogData = new MessageResponse();
        dialogData.setId(currentPerson.getId());
        dialogData.setTime(Utils.getLongFromLocalDateTime(LocalDateTime.now()));
        dialogData.setAuthorId(currentPerson.getId());
        dialogData.setMessageText(messageText);
        dialogData.setReadStatus("SENT");
        return dialogData;
    }

    private void createMessage(String messageText, Person currentPerson, long idDialog) {
        Dialog dialog = dialogRepository.findById(idDialog);
        List<Person> persons = personRepository.findPersonByDialog(idDialog);
        List<Message> messages = new ArrayList<>();
        persons.stream().filter(p -> p.getId() != currentPerson.getId()).forEach(pp -> {
                Message newMessage = new Message();
                newMessage.setAuthor(currentPerson);
                newMessage.setText(messageText);
                newMessage.setTime(LocalDateTime.now());
                newMessage.setReadStatus(ReadStatus.SENT);
                newMessage.setRecipient(pp);
                newMessage.setDialog(dialog);
                messages.add(newMessage);
            });
        messageRepository.saveAll(messages);
    }

    private void createPerson2Dialog(Person ownerDialog, Dialog dialog) {
        Person2Dialog person2Dialog = new Person2Dialog();
        person2Dialog.setDialog(dialog);
        person2Dialog.setPerson(ownerDialog);
        person2DialogRepository.save(person2Dialog);
    }

    @Override
    public CommonListResponse getDialogs(Integer offset, Integer itemPerPage, String query) {
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        Page<Dialog> dialogPage;
        if (query.equals(""))
        {
            dialogPage = dialogRepository.getDialogsOfPerson(pageable, currentPerson);
        }else {
            dialogPage = dialogRepository.getDialogsOfPersonWithQuery(pageable, query, currentPerson);
        }

        CommonListResponse cListResponse = new CommonListResponse();
        cListResponse.setError("");
        cListResponse.setTimestamp(LocalDateTime.now());
        //All items with query filter
        cListResponse.setTotal(dialogPage.getTotalElements());
        cListResponse.setOffset(offset);
        cListResponse.setPerPage(itemPerPage);

        List<ResponseData> dialogResponses = new ArrayList<>();
        for (Dialog dialog : dialogPage) {
            DialogResponse dialogResponse = new DialogResponse();
            dialogResponse.setId(dialog.getId());
            //All unread messages in the dialog
            dialogResponse.setUnreadCount(messageRepository.getUnreadCountOfDialog(dialog));
            Message lastMessage = messageRepository.getLastMessageOfDialog(dialog);
            if (lastMessage != null){
                MessageResponse messageResponse = new MessageResponse();
                messageResponse.setId(lastMessage.getId());
                messageResponse.setTime(Utils.getLongFromLocalDateTime(lastMessage.getTime()));
                messageResponse.setAuthorId(lastMessage.getAuthor().getId());
                messageResponse.setRecipientId(lastMessage.getRecipient().getId());
                messageResponse.setRecipient(PersonResponse.fromPerson(lastMessage.getRecipient()));
                messageResponse.setMessageText(lastMessage.getText());
                messageResponse.setReadStatus(lastMessage.getReadStatus().toString());
                dialogResponse.setLastMessage(messageResponse);
            }
            dialogResponses.add(dialogResponse);
        }
        cListResponse.setData(dialogResponses);

        return cListResponse;
    }

    @Override
    public CommonResponseData getQuantityUnreadMessageOfPerson() {
        Person currentPerson = accountServiceImpl.getCurrentPerson();
        int quantityUnreadMessage = messageRepository.getUnreadCountOfPerson(currentPerson);

        CommonResponseData commonResponseData = new CommonResponseData();
        commonResponseData.setError("string");
        commonResponseData.setTimestamp(LocalDateTime.now());

        CountMessageResponse countMessageResponse = new CountMessageResponse(quantityUnreadMessage);
        commonResponseData.setData(countMessageResponse);
        return commonResponseData;
    }

    @Override
    public ResponseArrayUserIds addUserIntoDialog(long id, DialogRequest dialogRequest) {
        ResponseArrayUserIds respons = new ResponseArrayUserIds();
        respons.setTimestamp(LocalDateTime.now());
        Dialog dialog = dialogRepository.findById(id);
        if(dialog == null && dialogRequest.getUsersIds().length > 0) {
            respons.setError("диалог не найден");
            return respons;
        }
        List<Person2Dialog> listP2D = new ArrayList<>();
        List<Long> idForResponse = new ArrayList<>();
        for(long personId : dialogRequest.getUsersIds()) {
            Person personIntoDialog = personRepository.findById(personId);
            idForResponse.add(personId);
            if (personIntoDialog != null) {
                Person2Dialog person2Dialog = new Person2Dialog();
                person2Dialog.setPerson(personIntoDialog);
                person2Dialog.setDialog(dialog);
                listP2D.add(person2Dialog);
            }
            person2DialogRepository.saveAll(listP2D);
        }
        respons.setUsersIds(idForResponse);
        return respons;
    }

    @Override
    public CommonResponseData getInviteDialog(long idDialog) {
        CommonResponseData commonResponseData = new CommonResponseData();
        String invite = dialogRepository.getInviteByDialog(idDialog);
        if (invite.isEmpty()) {
            commonResponseData.setError("не удалось сформировать ссылку");
            return commonResponseData;
        }
        commonResponseData.setTimestamp(LocalDateTime.now());
        LinkResponse linkResponse = new LinkResponse(invite);
        commonResponseData.setData(linkResponse);

        return commonResponseData;
    }

    @Override
    public CommonListResponse getMessageOfDialog(long idDialog, Integer offset, Integer itemPerPage, String query) {
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
//        Page<Message> listMessage = messageRepository.getMessageOfDialog(pageable, query, idDialog);
        Page<Message> listMessage = messageRepository.getMessageOfDialog(pageable, idDialog);

        CommonListResponse cListResponse = new CommonListResponse();
        cListResponse.setError("");
        cListResponse.setTimestamp(LocalDateTime.now());

        cListResponse.setTotal(listMessage.getTotalElements());
        cListResponse.setOffset(offset);
        cListResponse.setPerPage(itemPerPage);

        List<ResponseData> messageResponses = new ArrayList<>();
        for (Message message : listMessage) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setId(message.getId());
            messageResponse.setTime(Utils.getLongFromLocalDateTime(message.getTime()));
            messageResponse.setAuthorId(message.getAuthor().getId());
            messageResponse.setRecipientId(message.getRecipient().getId());
            messageResponse.setRecipient(PersonResponse.fromPerson(message.getRecipient()));
            messageResponse.setMessageText(message.getText());
            messageResponse.setReadStatus(message.getReadStatus().toString());

            messageResponses.add(messageResponse);
        }
        cListResponse.setData(messageResponses);

        return cListResponse;
    }

    @Override
    public CommonResponseData deleteMessage(long idMessage, long idDialog) {
        CommonResponseData commonResponseData = new CommonResponseData();
        commonResponseData.setTimestamp(LocalDateTime.now());
        Message message = messageRepository.findByIdAndDialog(idMessage, idDialog);
        if (message != null) {
            message.setDeleted(true);
            messageRepository.save(message);
        } else {
            commonResponseData.setError("сообщение не найдено");
            return commonResponseData;
        }
        IdMessageResponse idMessageResponse = new IdMessageResponse(idMessage);
        commonResponseData.setData(idMessageResponse);

        return commonResponseData;
    }

    @Override
    public CommonResponseData recoverMessage(long idMessage, long idDialog) {
        CommonResponseData commonResponseData = new CommonResponseData();
        commonResponseData.setTimestamp(LocalDateTime.now());
        Message message = messageRepository.findByIdAndDialog(idMessage, idDialog);
        if (message != null) {
            message.setDeleted(false);
            messageRepository.save(message);
        }else {
            commonResponseData.setError("сообщение не найдено");
            return commonResponseData;
        }
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setId(message.getId());
        messageResponse.setTime(Utils.getLongFromLocalDateTime(message.getTime()));
        messageResponse.setAuthorId(message.getAuthor().getId());
        messageResponse.setRecipientId(message.getRecipient().getId());
        messageResponse.setMessageText(message.getText());
        messageResponse.setReadStatus(message.getReadStatus().toString());
        commonResponseData.setData(messageResponse);

        return commonResponseData;
    }

    @Override
    public CommonResponseData getStatusAndLastActivity(long idPerson, long idDialog) {
        CommonResponseData commonResponseData = new CommonResponseData();
        commonResponseData.setTimestamp(LocalDateTime.now());
        UserStatusResponse userStatusResponse = new UserStatusResponse();
        //Сессия не храниться, статус неизвестен
        boolean status = false;
        Dialog dialog = dialogRepository.findById(idDialog);
        Person person = personRepository.findById(idPerson);

        if(dialog != null && person != null) {
            LocalDateTime lastActivityInTheDialogFromPerson = messageRepository.getTheTimeOfTheLastMessageOfTheDialogFromThePerson(dialog, person);
            userStatusResponse.setOnline(status);
            userStatusResponse.setLastActivity(Utils.getLongFromLocalDateTime(lastActivityInTheDialogFromPerson));
        }else {
            commonResponseData.setError("статус неизвестен");
            return commonResponseData;
        }
        commonResponseData.setData(userStatusResponse);

        return commonResponseData;
    }

}





















