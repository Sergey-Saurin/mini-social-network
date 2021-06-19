package network.likes;

import network.Application;
import network.api.response.CommonResponseData;
import network.api.response.ListLikeResponse;
import network.enums.LikeType;
import network.repository.*;
import network.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import network.api.request.LikeRequest;
import network.api.response.LikeResponse;
import skillbox.javapro11.repository.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(classes = Application.class)
@WithUserDetails("my@mail.ru")
@Sql(value = {"classpath:scripts/add-data-for-likeservice-test.sql"},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:scripts/delete-test-data-after-likeservice-test.sql"},
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class LikeServiceImplTest {

	@Autowired
	private LikeServiceImpl likeService;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Test
	public void isLikedTest() throws Exception {
		long personId = 1L;
		long postId = 1L;
		long commentId = 1L;
		long noExistingPostId = 2L;

		CommonResponseData response = likeService.isLiked(personId, postId, LikeType.POST.getType());
		assertThat(response).isNotNull();
		LikeResponse likeResponse = (LikeResponse) response.getData();
		assertTrue("Correct response value", likeResponse.isLikes());

		response = likeService.isLiked(null, commentId, LikeType.COMMENT.getType());
		assertThat(response).isNotNull();
		likeResponse = (LikeResponse) response.getData();
		assertTrue("Correct response value", likeResponse.isLikes());

		response = likeService.isLiked(personId, noExistingPostId, LikeType.POST.getType());
		assertThat(response).isNotNull();
		likeResponse = (LikeResponse) response.getData();
		assertFalse("Correct response value when it is no liked", likeResponse.isLikes());
	}

	@Test
	public void getUsersWhoLikedTest() {
		long itemId = 1L;

		CommonResponseData response = likeService.getUsersWhoLiked(itemId, LikeType.POST.getType());
		assertThat(response).isNotNull();
		ListLikeResponse listLikeResponse = (ListLikeResponse) response.getData();
		assertTrue("List size is correct",
				listLikeResponse.getLikes() == listLikeResponse.getUsersId().size());
		assertTrue("Likes count is correct", listLikeResponse.getLikes() == 1);
	}

	@Test
	@Sql(value = {"classpath:scripts/add-data-for-likeservice-test.sql", "classpath:scripts/delete-likes.sql"},
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(value = {"classpath:scripts/delete-test-data-after-likeservice-test.sql"},
			executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void putLikeTest() {
		LikeRequest postLikeRequest = new LikeRequest(1, LikeType.POST.getType());
		LikeRequest commentLikeRequest = new LikeRequest(1, LikeType.COMMENT.getType());

		CommonResponseData response = likeService.putLike(postLikeRequest);
		assertThat(response).isNotNull();
		ListLikeResponse listLikeResponse = (ListLikeResponse) response.getData();
		assertTrue("List size is correct",
				listLikeResponse.getLikes() == listLikeResponse.getUsersId().size());
		assertTrue("Likes count is correct", listLikeResponse.getLikes() == 1);

		response = likeService.putLike(commentLikeRequest);
		assertThat(response).isNotNull();
		listLikeResponse = (ListLikeResponse) response.getData();
		assertTrue("List size is correct",
				listLikeResponse.getLikes() == listLikeResponse.getUsersId().size());
		assertTrue("Likes count is correct", listLikeResponse.getLikes() == 1);
	}

	@Test
	public void deleteLikeTest() {
		CommonResponseData postLikeDeleteResponse = likeService.deleteLike(1, LikeType.POST.getType());
		assertThat(postLikeDeleteResponse).isNotNull();
		ListLikeResponse listLikeResponse = (ListLikeResponse) postLikeDeleteResponse.getData();
		assertTrue("Likes count is correct", listLikeResponse.getLikes() == 0);
		assertTrue("Response is without list of id", listLikeResponse.getUsersId() == null);

		CommonResponseData commentLikeDeleteResponse = likeService.deleteLike(1, LikeType.COMMENT.getType());
		assertThat(commentLikeDeleteResponse).isNotNull();
		listLikeResponse = (ListLikeResponse) commentLikeDeleteResponse.getData();
		assertTrue("Likes count is correct", listLikeResponse.getLikes() == 0);
		assertTrue("Response is without list of id", listLikeResponse.getUsersId() == null);
	}
}
