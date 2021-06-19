package network.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse extends ResponseData {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;
    private String tag;
}
