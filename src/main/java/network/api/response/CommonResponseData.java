package network.api.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponseData extends CommonResponse {
    private ResponseData data;

    public CommonResponseData(ResponseData data, String errorMassage) {
        super(errorMassage);
        this.data = data;
    }

    public CommonResponseData(String error, long timestamp, ResponseData data) {
        super(error, timestamp);
        this.data = data;
    }
}
