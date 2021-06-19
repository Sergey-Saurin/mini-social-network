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
public class DialogResponse extends ResponseData {

    private long id;

    @JsonProperty(value = "unread_count")
    private int unreadCount;

    @JsonProperty(value = "last_message")
    private MessageResponse lastMessage;
}
