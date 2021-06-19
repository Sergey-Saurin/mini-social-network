package network.service.impl;

import lombok.RequiredArgsConstructor;
import network.api.response.*;
import network.model.entity.Comment;
import network.model.entity.Person;
import network.model.entity.Post;
import network.repository.util.Utils;
import network.service.AccountService;
import network.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import network.api.request.CommentRequest;
import network.api.request.PostRequest;
import skillbox.javapro11.api.response.*;
import network.repository.CommentRepository;
import network.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final AccountService accountService;

  @Override
  public CommonListResponse getPostSearch(String text, long dateFrom, long dateTo, long offset, int limit) {

    Pageable page = Utils.getPageable(offset,limit, Sort.by(Sort.Direction.DESC, "time"));
    LocalDateTime dateFromTime = Utils.getLocalDateTimeFromLong(dateFrom);
    LocalDateTime dateToTime = Utils.getLocalDateTimeFromLong(dateTo);
    Page<Post> posts = postRepository.findAllPostsBySearch(page, text, dateFromTime, dateToTime);
    return new CommonListResponse ("", LocalDateTime.now(), posts.getTotalElements(),
            offset, limit, new ArrayList<>(PostResponse.fromPostList(posts.getContent())));
  }

  @Override
  public CommonResponseData getPostByID(long postId) {
    Person person = accountService.getCurrentPerson();
    CommonResponseData response = new CommonResponseData();
    Optional<Post> optionalPost = postRepository.findById(postId);
    if (optionalPost.isEmpty()) {
      response.setError(person + "\nPost id = " + postId + " not found.");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }
    Post post = optionalPost.get();
    postRepository.save(post);
    response.setData(PostResponse.fromPost(post));
    return response;
  }

  @Override
  public CommonResponseData editPostById(long postId, long publishData, PostRequest postRequest) {
    Person person = accountService.getCurrentPerson();
    CommonResponseData response = new CommonResponseData();
    Optional<Post> optionalPost = postRepository.findById(postId);
    if (optionalPost.isEmpty()) {
      response.setError(person + "\nPost id = " + postId + " not found.");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }

    Post post = optionalPost.get();
    if (post.getPerson().getId() != person.getId()) {
      response.setError("You have no rights");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }
    post.setText(postRequest.getText());
    post.setTitle(postRequest.getTitle());
    response.setData(PostResponse.fromPost(post));
    postRepository.save(post);
    return response;
  }

  @Override
  public CommonResponseData deletePostById(long postId) {
    Person person = accountService.getCurrentPerson();
    CommonResponseData response = new CommonResponseData();
    Optional<Post> optionalPost = postRepository.findById(postId);
    if (optionalPost.isEmpty()) {
      response.setError(person + "\nPost id = " + postId + " not found.");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }
    Post post = optionalPost.get();
    post.setDeleted(true);
    postRepository.save(post);
    response.setData(PostResponse.builder().id(post.getId()).build());
    return response;
  }

  @Override
  public CommonResponseData recoverPostById(long postId) {
    CommonResponseData response = new CommonResponseData();
    Optional<Post> postOptional = postRepository.findById(postId);
    if (postOptional.isEmpty()) {
      response.setError("Post not found");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }
    Post post = postOptional.get();
    post.setDeleted(false);
    postRepository.save(post);
    response.setData(PostResponse.fromPost(post));
    return response;
  }

  @Override
  public CommonResponseData reportPost(long postId) {
    CommonResponseData response = new CommonResponseData();
    Optional<Post> postOptional = postRepository.findById(postId);
    if (postOptional.isEmpty()) {
      response.setError("Post not found");
      response.setTimestamp(LocalDateTime.now());
      return response;
    }
    Post post = postOptional.get();
    post.setBlocked(true);
    postRepository.save(post);
    response.setData(new StatusMessageResponse("OK"));
    return response;
  }

  @Override
  public CommonListResponse getComments(long postId, int limit, int offset) {
    if (!postRepository.findById(postId).isPresent()) {
      return new CommonListResponse("Пост не найден", LocalDateTime.now(), null);
    }
    Pageable pageable = Utils.getPageable(offset, limit, Sort.by(Sort.DEFAULT_DIRECTION, "time"));
    Page<Comment> commentPage = commentRepository.findAllByPostIdAndDeletedFalse(postId, pageable);

    return new CommonListResponse("", LocalDateTime.now(), commentPage.getTotalElements(),
        offset, limit, new ArrayList<>(CommentResponse.fromCommentList(commentPage.getContent())));
  }

  @Override
  public CommonResponseData editedComment(long postId, Long idComment, CommentRequest comment) {
    Person person = accountService.getCurrentPerson();
    Optional<Post> postOptional = postRepository.findById(postId);
    if (!postOptional.isPresent()) {
      return new CommonResponseData(null, "Пост не найден");
    }
    Post post = postOptional.get();
    Comment newComment;
    if (idComment != null) {
      newComment = commentRepository.getByIdAndPostId(idComment, postId);
      if (newComment == null) {
        return new CommonResponseData(null, "Комментарий не найден");
      }
      if (newComment.getAuthorId() != person.getId()) {
        return new CommonResponseData(null, "У вас нет прав");
      }
    } else {
      newComment = new Comment();
      newComment.setPost(post);
      newComment.setTime(LocalDateTime.now());
      newComment.setAuthorId(person.getId());
      if (comment.getParentId() != null) {
        if (!commentRepository.findById(comment.getParentId()).isPresent()) {
          return new CommonResponseData(null, "Комментарий не найден");
        }
        newComment.setParentId(comment.getParentId());
      }
    }

    newComment.setCommentText(comment.getCommentText());

    commentRepository.save(newComment);

    return new CommonResponseData(CommentResponse.fromComment(newComment), "");
  }

  @Override
  public CommonResponseData deleteComment(long postId, long idComment) {
    Person person = accountService.getCurrentPerson();
    Comment comment = commentRepository.getByIdAndPostId(idComment, postId);
    if (comment == null) {
      return new CommonResponseData(null, "Комментарий не найден");
    }
    if (comment.getAuthorId() != person.getId()) {
      return new CommonResponseData(null, "У вас нет прав");
    }

    comment.setDeleted(true);
    commentRepository.save(comment);

    return new CommonResponseData(CommentResponse.builder().id(comment.getId()).build(), "");
  }

  @Override
  public CommonResponseData reportComment(long postId, long idComment) {
    Comment comment = commentRepository.getByIdAndPostId(idComment, postId);
    if (comment == null) {
      return new CommonResponseData(null, "Комментарий не найден");
    }
    comment.setBlocked(true);
    commentRepository.save(comment);
    return new CommonResponseData(new StatusMessageResponse("ok"), "");
  }

  @Override
  public CommonResponseData recoverComment(long postId, long idComment) {
    CommonResponseData response = new CommonResponseData();
    Comment comment = commentRepository.getByIdAndPostId(idComment, postId);
    if (comment == null) {
      return new CommonResponseData(null, "Комментарий не найден");
    }
    comment.setDeleted(false);
    commentRepository.save(comment);

    response.setData(CommentResponse.fromComment(comment));
    return response;
  }
}
