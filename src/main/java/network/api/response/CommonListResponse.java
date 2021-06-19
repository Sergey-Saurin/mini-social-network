package network.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonListResponse extends CommonResponse {
    private long total;
    private long offset;
    private int perPage;
    private List<ResponseData> data;


    public CommonListResponse(String error, LocalDateTime timestamp, List<ResponseData> data) {
        this.setError(error);
        this.setTimestamp(timestamp);
        this.data = data;
    }

    public CommonListResponse(String error, LocalDateTime timestamp, long total, long offset, int perPage,
                              List<ResponseData> data) {
        this.setError(error);
        this.setTimestamp(timestamp);
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }

    public CommonListResponse(String error, long timestamp,long total, long offset, int perPage, List<ResponseData> data) {
        super(error, timestamp);
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}
