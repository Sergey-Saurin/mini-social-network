package network.controller;

import network.api.request.FriendsIdRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.CommonResponseIsFriends;
import network.api.response.StatusMessageResponse;
import network.model.entity.Person;
import network.model.entity.dto.FriendsIdDTO;
import network.repository.util.Utils;
import network.service.FriendsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


import network.api.response.FriendResponse;
import network.api.response.PersonResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static network.enums.FriendshipStatusCode.*;

@RestController
public class FriendsController {

    private final FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping("/friends")
    public ResponseEntity<CommonListResponse> getFriends(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String name) {

        PageRequest pageable = PageRequest.of(getPage(offset, perPage), perPage);
        Page<Person> persons = friendsService.getFriends(name, FRIEND.name(), pageable);

        return ResponseEntity.ok(new CommonListResponse(
                "",
                Utils.getLongFromLocalDateTime(LocalDateTime.now()),
                persons.getTotalElements(),
                pageable.getOffset(),
                pageable.getPageSize(),
                new ArrayList<>(PersonResponse.fromPersonList(persons.getContent()))
        ));
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<CommonResponseData> deleteFriend(@PathVariable Long id) {

        friendsService.deleteById(id);
        return ResponseEntity.ok(new CommonResponseData(
                "",
                Utils.getLongFromLocalDateTime(LocalDateTime.now()),
                new StatusMessageResponse("ok")));
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<CommonResponseData> addFriend(@PathVariable Long id) {
        friendsService.addFriend(id);
        return ResponseEntity.ok(new CommonResponseData(
                "",
                Utils.getLongFromLocalDateTime(LocalDateTime.now()),
                new StatusMessageResponse("ok")));
    }

    @GetMapping("/friends/request")
    public ResponseEntity<CommonListResponse> getFriendsRequest(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String name) {

        PageRequest pageable = PageRequest.of(getPage(offset, perPage), perPage);
        Page<Person> persons = friendsService.getFriends(name, REQUEST.name(), pageable);

        return ResponseEntity.ok(new CommonListResponse(
                "",
                Utils.getLongFromLocalDateTime(LocalDateTime.now()),
                persons.getTotalElements(),
                pageable.getOffset(),
                pageable.getPageSize(),
                new ArrayList<>(PersonResponse.fromPersonList(persons.getContent())))
        );
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<CommonListResponse> getRecommendations(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer perPage) {

        PageRequest pageable = PageRequest.of(getPage(offset, perPage), perPage);
        Page<Person> recommendations = friendsService.getRecommendations(pageable);

        return ResponseEntity.ok(new CommonListResponse(
                "",
                Utils.getLongFromLocalDateTime(LocalDateTime.now()),
                recommendations.getTotalElements(),
                pageable.getOffset(),
                pageable.getPageSize(),
                new ArrayList<>(PersonResponse.fromPersonList(recommendations.getContent()))));
    }

    @PostMapping("/is/friends")
    public CommonResponseIsFriends isFriends(@RequestBody FriendsIdRequest friendsIdRequest) {
        List<FriendsIdDTO> friendsDTO = friendsService.isFriends(friendsIdRequest.getUserIds());
        return new CommonResponseIsFriends(friendsDTO.stream()
                .map(dto -> new FriendResponse(dto.getUserId(), dto.getStatus()))
                .collect(Collectors.toList()));
    }

    private int getPage(int offset, int perPage) {
        return offset / perPage;
    }
}
