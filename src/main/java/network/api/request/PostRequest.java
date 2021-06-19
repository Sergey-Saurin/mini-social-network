package network.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Sizenko Egor on 02.04.2021.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PostRequest {
	private String title;
	@JsonProperty("post_text")
	private String text;
}
