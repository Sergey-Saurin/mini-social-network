package network.service;

import com.sun.istack.NotNull;
import network.api.request.PostRequest;
import network.api.request.ProfileEditRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;

import java.time.LocalDateTime;

public interface ProfileService {

    CommonResponseData getCurrentUser();

    CommonResponseData editCurrentUser(@NotNull ProfileEditRequest profileEditRequest);

    CommonResponseData deleteCurrentUser();

    CommonResponseData findUserById(long id);

    CommonListResponse getUserWall(long userId, long offset, int itemPerPage);

    CommonResponseData postOnUserWall(long userId, PostRequest postBody);

    CommonListResponse searchUser(
            String firstName,
            String lastName,
            Integer ageFrom,
            Integer ageTo,
            String country,
            String city,
            long offset,
            int itemPerPage
    );

    CommonResponseData blockUser(boolean isBlocked, long userId);

    LocalDateTime getCorrectPublishLocalDateTime(LocalDateTime publishLocalDateTime);

}
