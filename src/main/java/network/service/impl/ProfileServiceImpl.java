package network.service.impl;

import com.sun.istack.NotNull;
import network.api.response.*;
import network.model.entity.Person;
import network.model.entity.Post;
import network.repository.util.PersonSpecificationsBuilder;
import network.repository.util.Utils;
import network.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import network.api.request.PostRequest;
import network.api.request.ProfileEditRequest;
import skillbox.javapro11.api.response.*;
import network.repository.PersonRepository;
import network.repository.PostRepository;
import network.service.ProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final AccountService accountService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;

    @Autowired
    public ProfileServiceImpl(
            AccountService accountService,
            PersonRepository personRepository,
            PostRepository postRepository
    ) {
        this.accountService = accountService;
        this.personRepository = personRepository;
        this.postRepository = postRepository;
    }

    public CommonResponseData getCurrentUser() {
        return new CommonResponseData(PersonResponse.fromPerson(accountService.getCurrentPerson(), null), "string");
    }

    public CommonResponseData editCurrentUser(@NotNull ProfileEditRequest profileEditRequest) {
        Person currentPerson = accountService.getCurrentPerson();

        if (profileEditRequest.getFirstName() != null) {
            currentPerson.setFirstName(profileEditRequest.getFirstName());
        }
        if (profileEditRequest.getLastName() != null) {
            currentPerson.setLastName(profileEditRequest.getLastName());
        }
        if (profileEditRequest.getBirthDate() != null) {
            currentPerson.setBirthday(profileEditRequest.getBirthDate().toLocalDate());
        }
        if (profileEditRequest.getPhone() != null) {
            currentPerson.setPhone(profileEditRequest.getPhone());
        }
        if (profileEditRequest.getPhoto() != null) {
            currentPerson.setPhoto(profileEditRequest.getPhoto());
        }

        if (profileEditRequest.getAbout() != null) {
            currentPerson.setAbout(profileEditRequest.getAbout());
        }

        if (profileEditRequest.getTown() != null) {
            currentPerson.setCity(profileEditRequest.getTown());
        }

        if (profileEditRequest.getCountry() != null) {
            currentPerson.setCountry(profileEditRequest.getCountry());
        }

        if (profileEditRequest.getPermissionMessage() != null) {
            currentPerson.setPermissionMessage(profileEditRequest.getPermissionMessage());
        }

        personRepository.save(currentPerson);

        return new CommonResponseData(PersonResponse.fromPerson(currentPerson), "string");
    }

    @Override
    public CommonResponseData deleteCurrentUser() {
        Person currentPerson = accountService.getCurrentPerson();

        personRepository.delete(currentPerson);

        CommonResponseData responseData = new CommonResponseData();
        responseData.setError("string");
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setData(new StatusMessageResponse("ok"));
        return responseData;
    }

    public CommonResponseData findUserById(long id) {
        PersonResponse personResponse = PersonResponse.fromPerson(personRepository.getOne(id));
        return new CommonResponseData(personResponse, "string");
    }

    public CommonListResponse getUserWall(long userId, long offset, int itemPerPage) {
        Person person = personRepository.findById(userId);
        Pageable pageable = Utils.getPageable(offset, itemPerPage);

        Page<Post> postPage = postRepository.findAllByPersonAndIsDeletedFalse(person, pageable);
        //build response
        return new CommonListResponse(
                "string",
                LocalDateTime.now(),
                postPage.getTotalElements(),
                offset,
                itemPerPage,
                new ArrayList<>(PostResponse.fromPostList(postPage.getContent()))
        );
    }

    public CommonResponseData postOnUserWall(long userId, PostRequest postBody) {
        Person author = personRepository.findById(userId);

        Post post = new Post();
        post.setPerson(author);
        post.setTime(LocalDateTime.now());
        post.setTitle(postBody.getTitle());
        post.setText(postBody.getText());
        post.setBlocked(false);

        postRepository.save(post);

        CommonResponseData response = new CommonResponseData();
        response.setError("string");
        response.setTimestamp(LocalDateTime.now());
        response.setData(PostResponse.fromPost(post));

        return response;
    }

    public CommonListResponse searchUser(
            String firstName,
            String lastName,
            Integer ageFrom,
            Integer ageTo,
            String country,
            String city,
            long offset,
            int itemPerPage
    ) {
        Pageable pageable = Utils.getPageable(offset, itemPerPage);

        PersonSpecificationsBuilder builder = new PersonSpecificationsBuilder();

        if (firstName != null && !"".equals(firstName)) {
            builder.with("firstName", ":", firstName);
        }
        if (lastName != null && !"".equals(lastName)) {
            builder.with("lastName", ":", lastName);
        }
        if (ageFrom != null) {
            builder.with("birthday", "<", LocalDate.now().minusYears(ageFrom));
        }
        if (ageTo != null) {
            builder.with("birthday", ">", LocalDate.now().minusYears(ageTo));
        }
        if (country != null && !"".equals(country)) {
            builder.with("country", ":", country);
        }
        if (city != null && !"".equals(city)) {
            builder.with("city", ":", city);
        }
        Specification<Person> spec = builder.build();

        Page<Person> personPage = personRepository.findAll(spec, pageable);

        return new CommonListResponse(
                "string",
                LocalDateTime.now(),
                personPage.getTotalElements(),
                offset,
                itemPerPage,
                new ArrayList<>(PersonResponse.fromPersonList(personPage.getContent()))
        );
    }

    public CommonResponseData blockUser(boolean isBlocked, long userId) {
        Person person = personRepository.findById(userId);
        person.setBlocked(isBlocked);
        personRepository.save(person);

        CommonResponseData responseData = new CommonResponseData();
        responseData.setError("string");
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setData(new StatusMessageResponse("ok"));
        return responseData;
    }

    public LocalDateTime getCorrectPublishLocalDateTime(LocalDateTime publishLocalDateTime) {
        return publishLocalDateTime.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : publishLocalDateTime;
    }
}
