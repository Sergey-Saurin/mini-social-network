package network.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.api.response.CommonResponseData;
import network.api.response.LikeResponse;
import network.api.response.ListLikeResponse;
import network.controller.LikeController;
import network.enums.LikeType;
import network.repository.PersonRepository;
import network.security.jwt.JwtTokenProvider;
import network.security.userdetails.UserDetailsServiceImpl;
import network.service.LikeService;
import network.service.PersonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import network.api.request.LikeRequest;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@WithMockUser
public class LikeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@MockBean
	private PersonRepository personRepository;

	@MockBean
	private PersonService personService;

	@MockBean
	private LikeService likeService;

	@Test
	public void testIsLiked() throws Exception {
		CommonResponseData existingLikeResponse =
				new CommonResponseData(new LikeResponse(true), "string");
		CommonResponseData nonExistentLikeResponse =
				new CommonResponseData(new LikeResponse(false), "string");

		Mockito.when(likeService.isLiked(1L, 2, LikeType.COMMENT.getType()))
				.thenReturn(existingLikeResponse);
		Mockito.when(likeService.isLiked(1L, 2, LikeType.POST.getType()))
				.thenReturn(nonExistentLikeResponse);

		mockMvc.perform(get("/liked")
				.param("user_id", "1")
				.param("item_id", "2")
				.param("type", LikeType.COMMENT.getType())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print());

		mockMvc.perform(get("/liked")
				.param("user_id", "1")
				.param("item_id", "2")
				.param("type", LikeType.POST.getType())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	@Test
	public void getUserWhoLikedTest() throws Exception {
		List<Long> usersId = new ArrayList<>();
		usersId.add(1L);
		usersId.add(2L);
		CommonResponseData response =
				new CommonResponseData(new ListLikeResponse(usersId.size(), usersId), "string");

		Mockito.when(likeService.getUsersWhoLiked(1L, LikeType.POST.getType()))
				.thenReturn(response);

		mockMvc.perform(get("/likes")
				.param("item_id", "1")
				.param("type", LikeType.POST.getType())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	@Test
	public void putLikeTest() throws Exception {
		List<Long> usersId = new ArrayList<>();
		usersId.add(1L);
		usersId.add(2L);
		CommonResponseData response =
				new CommonResponseData(new ListLikeResponse(usersId.size(), usersId), "string");
		LikeRequest likeRequest = new LikeRequest(1L, LikeType.POST.getType());

		Mockito.when(likeService.putLike(likeRequest)).thenReturn(response);
		System.out.println(likeService.putLike(likeRequest));

		mockMvc.perform(put("/likes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(likeRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	@Test
	public void deleteLikeTest() throws Exception {
		CommonResponseData response =
				new CommonResponseData(new ListLikeResponse(2, null), "string");

		Mockito.when(likeService.deleteLike(1L, LikeType.POST.getType())).thenReturn(response);

		mockMvc.perform(delete("/likes")
				.param("item_id", "1")
				.param("type", LikeType.POST.getType())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print());
	}
}
