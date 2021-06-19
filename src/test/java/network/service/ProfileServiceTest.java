package network.service;

import network.api.request.ProfileEditRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.PersonResponse;
import network.model.entity.Person;
import network.model.entity.Post;
import network.repository.util.PersonSpecificationsBuilder;
import network.repository.util.Utils;
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
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import network.ServiceTestConfiguration;
import network.api.request.PostRequest;
import network.api.response.PostResponse;
import network.enums.PermissionMessage;
import network.repository.PersonRepository;
import network.repository.PostRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(classes = ServiceTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@DisplayName("Profile service class test:")
public class ProfileServiceTest {

    private final LocalDateTime localDateTime = LocalDateTime.of(2021, 1, 1, 12, 0);
    private final LocalDate localDate = LocalDate.of(2021, 1, 1);

    @MockBean
    private AccountServiceImpl accountService;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private PostRepository postRepository;

    @Autowired
    private ProfileServiceImpl profileService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(accountService);

        Person currentPerson = new Person(
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

        Mockito.when(accountService.getCurrentPerson()).thenReturn(currentPerson);
    }

    @Test
    @DisplayName("Getting current user")
    void getCurrentUserTest() {
        CommonResponseData commonResponseData = profileService.getCurrentUser();

        assertThat(commonResponseData)
                .isNotNull()
                .isExactlyInstanceOf(CommonResponseData.class);

        PersonResponse personResponse = (PersonResponse) commonResponseData.getData();
        assertEquals("check first name", "Ivan", personResponse.getFirstName());
        assertEquals("check last name", "Ivanov", personResponse.getLastName());
        assertEquals("check phone", "+7(111)222-33-44", personResponse.getPhone());
        assertEquals("check photo", "photo", personResponse.getPhoto());
        assertEquals("about", "about", personResponse.getAbout());
        assertEquals("check town / city", "Moscow", personResponse.getCity());
        assertEquals("check country", "Russia", personResponse.getCountry());
        assertEquals("check permission massage", PermissionMessage.ALL, personResponse.getMessagesPermission());
    }

    @Test
    @DisplayName("Edit current user")
    void editCurrentUserTest() {

        ProfileEditRequest profileEditRequest = new ProfileEditRequest(
                "Petr",
                "Petrov",
                localDate.atStartOfDay(), // birth date
                "+7(222)333-44-55",
                "photoID",
                null, // null mustn't change about value
                "new town",
                "new country",
                PermissionMessage.FRIEND
        );

        CommonResponseData commonResponseData = profileService.editCurrentUser(profileEditRequest);

        assertThat(commonResponseData)
                .isNotNull()
                .isExactlyInstanceOf(CommonResponseData.class);

        PersonResponse personResponse = (PersonResponse) commonResponseData.getData();
        assertEquals("check first name", profileEditRequest.getFirstName(), personResponse.getFirstName());
        assertEquals("check last name", profileEditRequest.getLastName(), personResponse.getLastName());
        assertEquals("check phone", profileEditRequest.getPhone(), personResponse.getPhone());
        assertEquals("check photo", profileEditRequest.getPhoto(), personResponse.getPhoto());
        assertEquals("check that about not changed", accountService.getCurrentPerson().getAbout(), personResponse.getAbout());
        assertEquals("check town / city", profileEditRequest.getTown(), personResponse.getCity());
        assertEquals("check country", profileEditRequest.getCountry(), personResponse.getCountry());
        assertEquals("check permission massage", profileEditRequest.getPermissionMessage(), personResponse.getMessagesPermission());
    }

    @Test
    @DisplayName("Delete current user")
    void deleteCurrentUserTest() {

        CommonResponseData commonResponseData = profileService.deleteCurrentUser();

        assertThat(commonResponseData)
                .isNotNull()
                .isInstanceOf(CommonResponseData.class)
                .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("Find user by Id")
    void findUserByIdTest() {
        final long id = 1L;
        Person person = accountService.getCurrentPerson();

        Mockito.when(personRepository.getOne(id)).thenReturn(person);

        CommonResponseData userById = profileService.findUserById(id);
        assertThat(userById)
                .isNotNull()
                .isInstanceOf(CommonResponseData.class);

        PersonResponse personResponse = (PersonResponse) userById.getData();
        assertEquals("check first name", "Ivan", personResponse.getFirstName());
        assertEquals("check last name", "Ivanov", personResponse.getLastName());
        assertEquals("check phone", "+7(111)222-33-44", personResponse.getPhone());
        assertEquals("check photo", "photo", personResponse.getPhoto());
        assertEquals("about", "about", personResponse.getAbout());
        assertEquals("check town / city", "Moscow", personResponse.getCity());
        assertEquals("check country", "Russia", personResponse.getCountry());
        assertEquals("check permission massage", PermissionMessage.ALL, personResponse.getMessagesPermission());
    }

    @Test
    @DisplayName("get users wall")
    void getUserWall() {
        Person person = accountService.getCurrentPerson();
        long offset = 0L;
        int itemPerPage = 2;
        Page<Post> postPage = Page.empty();
        Mockito.when(postRepository.findAllByPerson(person, Utils.getPageable(offset, itemPerPage)))
                .thenReturn(postPage);
        Mockito.when(personRepository.findById(person.getId())).thenReturn(person);

        CommonListResponse response = profileService.getUserWall(person.getId(), offset, itemPerPage);

        assertEquals("error field value", "string", response.getError());
        assertEquals("total field value", 0L, response.getTotal());
        assertEquals("offset field value", offset, response.getOffset());
        assertEquals("itemPerPage field value", itemPerPage, response.getPerPage());
        assertEquals("list size", 0, response.getData().size());
    }

    @Test
    @DisplayName("Post on users wall")
    void postOnUserWall() {
        Person person = accountService.getCurrentPerson();
        LocalDateTime nowLDT = LocalDateTime.now(ZoneId.systemDefault());
        long nowL = Timestamp.valueOf(nowLDT).getTime();
        String title = "Title example";
        String text = "Text example";
        PostRequest postRequest = new PostRequest(title, text);

        Mockito.when(personRepository.findById(person.getId())).thenReturn(person);

        CommonResponseData response = profileService.postOnUserWall(person.getId(), postRequest);
        PostResponse postResponse = (PostResponse) response.getData();

        assertEquals("Check author by email", person.getEmail(), postResponse.getAuthor().getEmail());
        assertEquals("Check title", title, postResponse.getTitle());
        assertEquals("Check text", text, postResponse.getPostText());
        assertTrue("Correct post time", !Utils.getLocalDateTimeFromLong(postResponse.getTime()).isBefore(nowLDT));
    }

    @Test
    @DisplayName("Search users")
    void searchUser() {
        long offset = 0L;
        int itemPerPage = 2;
        PersonSpecificationsBuilder builder = new PersonSpecificationsBuilder();
        Specification<Person> spec = builder.build();
        Page<Person> personPage = Page.empty();
        Mockito.when(personRepository.findAll(spec, Utils.getPageable(offset, itemPerPage)))
                .thenReturn(personPage);

        CommonListResponse response = profileService.searchUser(
                null, null, null, null, null, null, offset, itemPerPage);


        assertEquals("error field value", "string", response.getError());
        assertEquals("total field value", 0L, response.getTotal());
        assertEquals("offset field value", offset, response.getOffset());
        assertEquals("itemPerPage field value", itemPerPage, response.getPerPage());
        assertEquals("list size", 0, response.getData().size());
    }

    @Test
    @DisplayName("Block/unblock user")
    void blockUser() {
        Person person = accountService.getCurrentPerson();

        Mockito.when(personRepository.findById(person.getId())).thenReturn(person);

        CommonResponseData blockResponse = profileService.blockUser(true, person.getId());
        CommonResponseData unblockResponse = profileService.blockUser(false, person.getId());

        assertThat(blockResponse).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(unblockResponse).isNotNull().hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("Mapping personList to personResponseList")
    void getPersonResponseListFromPersonList() {
        List<Person> personList = new ArrayList<>();
        Person person = new Person();
        person.setRegistrationDate(LocalDateTime.now());
        person.setLastTimeOnline(LocalDateTime.now());
        person.setBirthday(LocalDate.now());
        personList.add(person);
        personList.add(person);
        List<PersonResponse> personResponseList = PersonResponse.fromPersonList(personList);

        assertTrue("Correct list size", personList.size() == personResponseList.size());
    }

    @Test
    @DisplayName("Mapping postList to postResponseList")
    void getPostResponseListFromPostList() {
        List<Post> postList = new ArrayList<>();
        Person author = accountService.getCurrentPerson();
        Post post = new Post();
        post.setPerson(author);
        post.setTime(LocalDateTime.now());
        postList.add(post);
        postList.add(post);
        List<PostResponse> postResponseList = PostResponse.fromPostList(postList);

        assertTrue("Correct list size", postResponseList.size() == postList.size());
    }
}
