package network.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LikeRequest {
	@JsonProperty("item_id")
	private long itemId;
	private String type;
}
