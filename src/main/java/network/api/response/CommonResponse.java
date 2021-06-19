package network.api.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import network.repository.util.Utils;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private String error;
    private long timestamp;

    public CommonResponse(String errorMessage) {
        this.error = errorMessage;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = Utils.getLongFromLocalDateTime(timestamp);
    }

}
