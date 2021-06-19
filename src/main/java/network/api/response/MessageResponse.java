package network.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse extends ResponseData{

    private long id;

    private Long time;

    @JsonProperty(value = "author_id")
    private long authorId;

    @JsonProperty(value = "recipient_id")
    private long recipientId;

    @JsonProperty(value = "message_text")
    private String messageText;

    @JsonProperty(value = "read_status")
    private String readStatus;

    private String message;
    private PersonResponse recipient;
}
