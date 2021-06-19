package network.service;

import network.ServiceTestConfiguration;
import network.api.request.CommentRequest;
import network.api.response.*;
import network.enums.PermissionMessage;
import network.model.entity.Comment;
import network.model.entity.Person;
import network.model.entity.Post;
import network.repository.CommentRepository;
import network.repository.PostRepository;
import network.repository.util.Utils;
import network.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import network.api.request.PostRequest;
import skillbox.javapro11.api.response.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = ServiceTestConfiguration.class)
class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private AccountServiceImpl accountService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(accountService);

        Person currentPerson = new Person(
                2L,
                "Ivan",
                "Ivanov",
                LocalDateTime.now().minusDays(10), // registration date
                LocalDate.now(), // birth date
                "ivanov@mail.com",
                "+7(111)222-33-44",
                "password",
                "photo",
                "about",
                "Russia",
                "Moscow",
                true,
                PermissionMessage.ALL,
                LocalDateTime.now(), // last online time
                false);

        Mockito.when(accountService.getCurrentPerson()).thenReturn(currentPerson);
    }

    @Test
    @DisplayName("Getting post search")
    public void getPostSearchTest() {
        String text = "test";
        long postId = 1L;
        long dataFrom = 12;
        long dataTo = 26;
        long offset = 0L;
        int itemPerPage = 2;
        Person person = accountService.getCurrentPerson();
        Post post1 = new Post(postId,
                LocalDateTime.now(),
                person,
                "post 1", text,
                false, false,
                new ArrayList<>(), new ArrayList<>());

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post1));

        ArrayList<Post> posts = new ArrayList<>();
        posts.add(post1);

        Pageable pageable = Utils.getPageable(offset, itemPerPage, Sort.by(Sort.Direction.DESC, "time"));
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        LocalDateTime dateFromTime = Utils.getLocalDateTimeFromLong(dataFrom);
        LocalDateTime dateToTime = Utils.getLocalDateTimeFromLong(dataTo);
        given(postRepository.findAllPostsBySearch(pageable, text, dateFromTime, dateToTime)).willReturn(page);

        CommonListResponse response = postService.getPostSearch(text, dataFrom, dataTo, offset, itemPerPage);

        assertTrue(response.getError().isEmpty());
        assertEquals("total field value", 1L, response.getTotal());
        assertEquals("offset field value", offset, response.getOffset());
        assertEquals("itemPerPage field value", itemPerPage, response.getPerPage());
        assertEquals("list size", 1, response.getData().size());
    }

    @Test
    @DisplayName("Getting post")
    public void getPostByIDTest() {
        CommonResponseData responseData = new CommonResponseData(null, "Post not found");
        long postId = 1L;

        CommonResponseData response = postService.getPostByID(postId);

        assertEquals("check data", responseData.getData(), response.getData());
    }

    @Test
    @DisplayName("Edit post")
    public void editPostByIdTest() {
        String text = "test text";
        String title = "test title";
        long postId = 1L;
        long publishData = 2L;
        Person person = accountService.getCurrentPerson();
        Optional<Post> post = Optional.of(new Post(postId,
                LocalDateTime.now(),
                person,
                "post 1", "post text 1",
                false, false,
                new ArrayList<>(), new ArrayList<>()));

        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        PostRequest newPost = new PostRequest();
        newPost.setText(text);
        newPost.setTitle(title);

        CommonResponseData response = postService.editPostById(postId, publishData, newPost);
        PostResponse postResponse = (PostResponse) response.getData();

        assertNull(response.getError());
        assertEquals("check text", text, postResponse.getPostText());
        assertEquals("check title", title, postResponse.getTitle());

    }

    @Test
    @DisplayName("Delete post")
    public void deletePostByIdTest() {
        long postId = 1L;

        CommonResponseData response = postService.deletePostById(postId);

        assertThat(response)
                .isNotNull()
                .isInstanceOf(CommonResponseData.class);
    }

    @Test
    @DisplayName("Recover post")
    public void recoverPostByIdTest() {
        long postId = 1L;
        Person person = accountService.getCurrentPerson();
        Post post = new Post(postId,
                LocalDateTime.now(),
                person,
                "post 1", "post text 1",
                false, true,
                new ArrayList<>(), new ArrayList<>());

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        CommonResponseData responseData = postService.reportPost(postId);

        assertTrue(post.isDeleted());
        assertNotNull(responseData.getData());
        assertNull(responseData.getError());
    }

    @Test
    @DisplayName("Report post")
    public void reportPostTest() {
       long postId = 1L;
       Person person = accountService.getCurrentPerson();
       Post post = new Post(postId,
                LocalDateTime.now(),
                person,
                "post 1", "post text 1",
                false, false,
                new ArrayList<>(), new ArrayList<>());

        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        CommonResponseData responseData = postService.reportPost(postId);

        assertTrue(post.isBlocked());
        assertNotNull(responseData.getData());
        assertEquals("check response", "OK", ((StatusMessageResponse) responseData.getData()).getMessage());
        assertNull(responseData.getError());
    }

    @Test
    @DisplayName("Getting all comment")
    void getComments() {
      long postId = 11;
      int offset = 0;
      int itemPerPage = 1;
      Person person = accountService.getCurrentPerson();
      Optional<Post> post = Optional.of(new Post(postId,
          LocalDateTime.now(),
          person,
          "post 1", "post text 1",
          false, false,
          new ArrayList<>(), new ArrayList<>()));

      Mockito.when(postRepository.findById(postId)).thenReturn(post);
      Comment com1 = new Comment(1, null,
          "comment text 1", post.get(),
          LocalDateTime.now(), person.getId(),
          false, false);
      Comment com2 = new Comment(2, null,
          "comment text 2", post.get(),
          LocalDateTime.now().plusMinutes(2), person.getId(),
          false, false);
      Comment com3 = new Comment(3, null,
          "comment text 3", post.get(),
          LocalDateTime.now().plusMinutes(5), person.getId(),
          false, false);
      ArrayList<Comment> comments = new ArrayList<>();
      comments.add(com1);
      comments.add(com2);
      comments.add(com3);

      Pageable pageable = Utils.getPageable(offset, itemPerPage, Sort.by(Sort.DEFAULT_DIRECTION, "time"));
      Page<Comment> page = new PageImpl<>(comments, pageable, comments.size());
      given(commentRepository.findAllByPostIdAndDeletedFalse(postId, pageable)).willReturn(page);

      CommonListResponse response = postService.getComments(postId, itemPerPage, offset);

      assertTrue(response.getError().isEmpty());
      assertEquals("check count data", 3, response.getData().size());
      CommentResponse data = (CommentResponse) response.getData().get(0);
      assertEquals("check time first comment", Utils.getLongFromLocalDateTime(com1.getTime()), data.getTime());
  }


    @Test
    @DisplayName("Add comment. Error: post not found")
    void addComment1() {
        CommonResponseData responseData = new CommonResponseData(null, "Пост не найден");
        long postId = 123;
        CommentRequest newComment = new CommentRequest();
        newComment.setParentId(null);
        newComment.setCommentText("new comment 1");

        CommonResponseData response = postService.editedComment(postId, null, newComment);
        assertEquals("check data", responseData.getData(), response.getData());
        assertEquals("check error", responseData.getError(), response.getError());
    }


  @Test
  @DisplayName("Edit comment")
  void editedComment() {
    String newText = "new comment text";
    long postId = 11;
    long commentId = 2;
    Person person = accountService.getCurrentPerson();
    Optional<Post> post = Optional.of(new Post(postId,
        LocalDateTime.now(),
        person,
        "post 1", "post text 1",
        false, false,
        new ArrayList<>(), new ArrayList<>()));

    Mockito.when(postRepository.findById(postId)).thenReturn(post);
    Comment commentModel = new Comment(commentId, null,
        "comment text", new Post(),
        LocalDateTime.now(), 2L,
        false, false);
    Mockito.when(commentRepository.getByIdAndPostId(commentId, postId)).thenReturn(commentModel);

    CommentRequest newComment = new CommentRequest();
    newComment.setParentId(null);
    newComment.setCommentText("new comment text");

    CommonResponseData response = postService.editedComment(postId, commentId, newComment);
    CommentResponse responseComment = (CommentResponse) response.getData();

    assertTrue(response.getError().isEmpty());
    assertEquals("check text", newText, responseComment.getCommentText());
  }

  @Test
  @DisplayName("Добавление нового комментария, при условии, что пост существует")
  void addComment2() {
    long postId = 11;
    Person person = accountService.getCurrentPerson();
    Optional<Post> post = Optional.of(new Post(postId,
        LocalDateTime.now(),
        person,
        "post 1", "post text 1",
        false, false,
        new ArrayList<>(), new ArrayList<>()));

    Mockito.when(postRepository.findById(postId)).thenReturn(post);
    CommentResponse defaultComment = CommentResponse.builder()
        .parentId(0L)
        .commentText("new comment 2")
        .blocked(false)
        .postId(postId)
        .authorId(2L)
        .time(Utils.getLongFromLocalDateTime(LocalDateTime.now()))
        .build();

    CommonResponseData responseData = new CommonResponseData(defaultComment, "");

        CommentRequest newComment = new CommentRequest();
        newComment.setParentId(null);
        newComment.setCommentText("new comment 2");

    CommonResponseData response = postService.editedComment(postId, null, newComment);
    CommentResponse responseComment = (CommentResponse) response.getData();

    assertEquals("check parent comment", defaultComment.getParentId(), responseComment.getParentId());
    assertEquals("check text comment", defaultComment.getCommentText(), responseComment.getCommentText());
    assertEquals("check author comment", defaultComment.getAuthorId(), responseComment.getAuthorId());
    assertEquals("check post comment", defaultComment.getPostId(), responseComment.getPostId());
    assertEquals("check error", responseData.getError(), response.getError());
  }

  @Test
  @DisplayName("Удаление коммментария. Ошибка")
  void deleteCommentError() {
    long postId = 11;
    long commentId = 988777;
    CommonResponseData responseData = postService.deleteComment(postId, commentId);
    assertNull(responseData.getData());
    assertEquals("error", "Комментарий не найден", responseData.getError());
  }

  @Test
  @DisplayName("Удаление коммментария. Успешно")
  void deleteCommentOk() {
    long postId = 11;
    long commentId = 2;
    Comment commentModel = new Comment(commentId, null,
        "comment text", new Post(),
        LocalDateTime.now(), 2L,
        false, false);
    Mockito.when(commentRepository.getByIdAndPostId(commentId, postId)).thenReturn(commentModel);

    CommonResponseData responseData = postService.deleteComment(postId, commentId);
    assertNotNull(responseData.getData());
    assertTrue(responseData.getError().isEmpty());
    assertEquals("check response", commentId, ((CommentResponse) responseData.getData()).getId());
  }

  @Test
  @DisplayName("Жалоба на коммент")
  void reportComment() {
    long postId = 11;
    long commentId = 2;
    Comment commentModel = new Comment(commentId, null,
        "comment text", new Post(),
        LocalDateTime.now(), 2L,
        false, false);
    Mockito.when(commentRepository.getByIdAndPostId(commentId, postId)).thenReturn(commentModel);
    CommonResponseData responseData = postService.reportComment(postId, commentId);

    assertTrue(commentModel.isBlocked());
    assertNotNull(responseData.getData());
    assertEquals("check response", "ok", ((StatusMessageResponse) responseData.getData()).getMessage());
    assertTrue(responseData.getError().isEmpty());
  }

  @Test
  @DisplayName("Восстановление коммента")
  void recoverComment() {
    long postId = 11;
    long commentId = 2;
    Comment commentModel = new Comment(commentId, null,
        "comment text", new Post(),
        LocalDateTime.now(), 2L,
        false, false);
    Mockito.when(commentRepository.getByIdAndPostId(commentId, postId)).thenReturn(commentModel);

    CommonResponseData responseData = postService.recoverComment(postId, commentId);

    assertFalse(commentModel.isDeleted());
    assertNotNull(responseData.getData());
    assertNull(responseData.getError());
  }
}