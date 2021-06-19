package network.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseWithDataList<T> extends CommonResponseData{
    List<T> data;
}

