package network.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LikeType {
	POST("Post"),
	COMMENT("Comment");

	private final String type;
}
