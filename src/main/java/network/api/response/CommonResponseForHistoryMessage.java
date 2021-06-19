package network.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class CommonResponseForHistoryMessage extends CommonResponseData {
    /**
     * Make this method deprecated cause there is @link CommonResponseWithDataList.class is the same, but use generic.
     * It allow to reuse that method.
     * sivtcev
     */
    private List<PersonResponse> profiles;
}
