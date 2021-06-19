package network.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListMessageResponse extends ResponseData {
    private long count;
    private List<MessageResponse> messages;
}
