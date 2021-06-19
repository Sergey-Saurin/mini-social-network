package network.controller;

import network.api.response.CommonResponseData;
import network.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import network.api.request.LikeRequest;

@RestController
public class LikeController {

	private final LikeService likeService;

	@Autowired
	public LikeController(LikeService likeService) {
		this.likeService = likeService;
	}

	@GetMapping("/liked")
	public CommonResponseData isLiked(
			@RequestParam(name = "user_id", required = false) Long userId,
			@RequestParam("item_id") long itemId,
			@RequestParam("type") String type
	) {
		return likeService.isLiked(userId, itemId, type);
	}

	@GetMapping("/likes")
	public CommonResponseData getUsersWhoLiked(
			@RequestParam("item_id") long itemId,
			@RequestParam("type") String type
	) {
		return likeService.getUsersWhoLiked(itemId, type);
	}

	@PutMapping("/likes")
	public CommonResponseData putLike(@RequestBody LikeRequest likeRequest) {
		return likeService.putLike(likeRequest);
	}

	@DeleteMapping("/likes")
	public CommonResponseData deleteLike(
			@RequestParam("item_id") long itemId,
			@RequestParam("type") String type
	) {
		return likeService.deleteLike(itemId, type);
	}

}
