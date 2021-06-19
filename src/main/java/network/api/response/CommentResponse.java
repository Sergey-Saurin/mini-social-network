package network.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import network.model.entity.Comment;
import network.repository.util.Utils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse extends ResponseData {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "parent_id")
  private Long parentId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "comment_text")
  private String commentText;

  private long id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "post_id")
  private Long postId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long time;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "author_id")
  private Long authorId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(value = "is_blocked")
  private Boolean blocked;


  public static List<CommentResponse> fromCommentList(List<Comment> commentList) {
    List<CommentResponse> commentDTOList = new ArrayList<>();
    commentList.forEach(comment -> commentDTOList.add(fromComment(comment)));
    return commentDTOList;
  }

  public static CommentResponse fromComment(Comment comment) {
    return new CommentResponse(
        comment.getParentId() == null ? 0 : comment.getParentId(),
        comment.getCommentText(),
        comment.getId(),
        comment.getPost().getId(),
        Utils.getLongFromLocalDateTime(comment.getTime()),
        comment.getAuthorId(),
        comment.isBlocked()
    );
  }
}
