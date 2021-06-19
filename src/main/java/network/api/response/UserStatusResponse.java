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
public class UserStatusResponse extends ResponseData {

    private boolean online;

    @JsonProperty(value = "last_activity")
    private long lastActivity;
}
