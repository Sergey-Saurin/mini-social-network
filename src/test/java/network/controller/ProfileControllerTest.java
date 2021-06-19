package network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import network.api.response.StatusMessageResponse;
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
import network.api.request.PostRequest;
import network.api.request.ProfileEditRequest;
import network.api.response.CommonListResponse;
import network.api.response.CommonResponseData;
import network.api.response.PersonResponse;
import network.enums.PermissionMessage;
import network.service.ProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Profile controller test")
public class ProfileControllerTest {

    private final LocalDate localDate = LocalDate.of(2021, 1, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @Test
    @DisplayName("Get current user")
    @SneakyThrows
    @WithMockUser
    void getCurrentUserTest() {
        PersonResponse personResponse = new PersonResponse(
                1L,
                "Ivan",
                "Ivanov",
                0L,
                0L,
                "email",
                "phone",
                "photo",
                "about",
                "city",
                "country",
                PermissionMessage.ALL,
                0L,
                false,
                null);
        Mockito.when(profileService.getCurrentUser()).thenReturn(new CommonResponseData(personResponse, "string"));

        this.mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("string"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.first_name").value("Ivan"))
                .andExpect(jsonPath("$.data.last_name").value("Ivanov"))
                .andExpect(jsonPath("$.data.email").value("email"))
                .andExpect(jsonPath("$.data.phone").value("phone"))
                .andExpect(jsonPath("$.data.photo").value("photo"))
                .andExpect(jsonPath("$.data.about").value("about"))
                .andExpect(jsonPath("$.data.city").value("city"))
                .andExpect(jsonPath("$.data.country").value("country"))
                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));
    }

    @Test
    @DisplayName("Edit current user")
    @SneakyThrows
    @WithMockUser
    void editCurrentUserTest() {


        ProfileEditRequest profileEditRequest = new ProfileEditRequest(
                "Petr",
                "Petrov",
                localDate.atStartOfDay(),
                "phone",
                "newPhoto",
                "newAbout",
                "town",
                "country",
                PermissionMessage.FRIEND);

        this.mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileEditRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @SneakyThrows
    @DisplayName("Delete current user")
    void deleteCurrentUserTest() {

        CommonResponseData responseData = new CommonResponseData();
        responseData.setError("string");
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setData(new StatusMessageResponse("ok"));

        Mockito.when(profileService.deleteCurrentUser()).thenReturn(responseData);

        this.mockMvc.perform(delete("/users/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @SneakyThrows
    @DisplayName("Get user by Id")
    void getUserById() {

        PersonResponse personResponse = new PersonResponse(
                1L,
                "Ivan",
                "Ivanov",
                0L,
                0L,
                "email",
                "phone",
                "photo",
                "about",
                "city",
                "country",
                PermissionMessage.ALL,
                0L,
                false,
                null);
        Mockito.when(profileService.findUserById(1L)).thenReturn(new CommonResponseData(personResponse, "string"));

        this.mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("string"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.first_name").value("Ivan"))
                .andExpect(jsonPath("$.data.last_name").value("Ivanov"))
                .andExpect(jsonPath("$.data.email").value("email"))
                .andExpect(jsonPath("$.data.phone").value("phone"))
                .andExpect(jsonPath("$.data.photo").value("photo"))
                .andExpect(jsonPath("$.data.about").value("about"))
                .andExpect(jsonPath("$.data.city").value("city"))
                .andExpect(jsonPath("$.data.country").value("country"))
                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data.is_blocked").value("false"));
    }

    @Test
    @DisplayName("Get users wall")
    @WithMockUser
    void getPostsUserWall() throws Exception {
        long offset = 0L;
        int itemPerPage = 2;
        CommonListResponse response = new CommonListResponse();
        response.setError("string");
        response.setPerPage(itemPerPage);
        response.setOffset(offset);

        Mockito.when(profileService.getUserWall(1L, offset, itemPerPage))
                .thenReturn(response);

        mockMvc.perform(get("/users/1/wall")
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add post on user wall")
    @WithMockUser
    void addPostUserWall() throws Exception {

        PostRequest postRequest = new PostRequest();
        String postRequestJSON = objectMapper.writeValueAsString(postRequest);
        Mockito.when(profileService.postOnUserWall(1, postRequest))
                .thenReturn(new CommonResponseData());

        mockMvc.perform(post("/users/1/wall")
                .param("publish_date", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestJSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Search user request")
    @WithMockUser
    void searchUser() throws Exception {
        CommonListResponse response = new CommonListResponse();
        long offset = 0L;
        int itemPerPage = 2;
        response.setError("string");
        response.setOffset(offset);
        response.setPerPage(itemPerPage);

        Mockito.when(profileService.searchUser(
                null, null, null, null, null, null, offset, itemPerPage))
                .thenReturn(response);

        mockMvc.perform(get("/users/search")
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Block user by ID")
    @WithMockUser
    void blockUserById() throws Exception {
        CommonResponseData responseData = new CommonResponseData();
        responseData.setError("string");
        responseData.setData(new StatusMessageResponse("ok"));

        Mockito
                .when(profileService.blockUser(true, 1L))
                .thenReturn(responseData);

        mockMvc.perform(put("/users/block/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("string"))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @DisplayName("Unblock user by ID")
    @WithMockUser
    void unblockUserById() throws Exception {
        CommonResponseData responseData = new CommonResponseData();
        responseData.setError("string");
        responseData.setData(new StatusMessageResponse("ok"));

        Mockito
                .when(profileService.blockUser(false, 1L))
                .thenReturn(responseData);

        mockMvc.perform(delete("/users/block/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("string"))
                .andExpect(jsonPath("$.data.message").value("ok"));
    }
}
