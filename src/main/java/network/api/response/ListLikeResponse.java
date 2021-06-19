package network.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListLikeResponse extends ResponseData {

    private int likes;

    @JsonProperty(value = "users")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> usersId;
}
