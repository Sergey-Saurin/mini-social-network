package network.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse extends ResponseData {

    private long id;

    @JsonProperty(value = "type_id")
    private long typeId;

    @JsonProperty(value = "sent_time")
    private long sentTime;

    @JsonProperty(value = "entity_id")
    private long entityId;

    private String info;
}
