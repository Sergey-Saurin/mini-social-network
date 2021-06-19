package network.service;

import network.api.request.LikeRequest;
import network.api.response.CommonResponseData;

public interface LikeService {
	CommonResponseData isLiked(Long userId, long itemId, String type);

	CommonResponseData getUsersWhoLiked(long itemId, String type);

	CommonResponseData putLike(LikeRequest likeRequest);

	CommonResponseData deleteLike(long itemId, String type);
}
