package network.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by timurg on 04.05.2021.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DialogUserShortListResponse extends ResponseData{
    @JsonProperty(value = "user_ids")
    private List<Long> userIds;
}