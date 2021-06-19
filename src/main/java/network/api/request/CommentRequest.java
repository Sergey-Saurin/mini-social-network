package network.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequest {
  @JsonProperty(value="parent_id")
  private Long parentId;
  @JsonProperty(value="comment_text")
  private String commentText;
}
