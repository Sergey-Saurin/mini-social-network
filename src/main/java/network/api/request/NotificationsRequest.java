package network.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import network.enums.NotificationTypeCode;

@Data
@AllArgsConstructor
public class NotificationsRequest {

    @JsonProperty("notification_type")
    private NotificationTypeCode notificationTypeCode;
    private Boolean enable;
}
