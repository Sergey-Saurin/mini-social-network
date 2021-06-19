package network.service;

import network.api.request.DialogRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.ResponseArrayUserIds;
import network.model.entity.Dialog;
import network.model.entity.Person;

/**
 * Created by timur_guliev on 27.04.2021.
 */
public interface DialogsService {
    CommonResponseData createDialog(DialogRequest dialogRequest);

    CommonResponseData deleteDialog(long id);

    Dialog createNewDialog(Person ownerDialog);

    CommonResponseData joinToDialog(long idDialog, String link);

    CommonResponseData sendMessage(long idDialog, String messageText);

    CommonResponseData editMessage(long idDialog, long idMessage, String messageText);

    CommonResponseData readMessage(long idDialog, long idMessage);

    CommonResponseData changeStatusActivity(long idDialog, long idUser);

    CommonResponseData deleteUsersInDialog(long idDialog, String[] usersIds);

    CommonListResponse getDialogs(Integer offset, Integer itemPerPage, String query);

    CommonResponseData getQuantityUnreadMessageOfPerson();

    ResponseArrayUserIds addUserIntoDialog(long id, DialogRequest dialogRequest);

    CommonResponseData getInviteDialog(long idDialog);

    CommonListResponse getMessageOfDialog(long idDialog, Integer offset, Integer itemPerPage, String query);

    CommonResponseData deleteMessage( long idMessage, long idDialog);

    CommonResponseData recoverMessage(long idMessage, long idDialog);

    CommonResponseData getStatusAndLastActivity(long idPerson, long idDialog);
}
