package network.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import network.api.response.CommonListResponse;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	@GetMapping
	public CommonListResponse getNotifications(
			@RequestParam(name = "offset", required = false, defaultValue = "0") long offset,
			@RequestParam(name = "itemPerPage", required = false, defaultValue = "20") int itemPerPage
	) {
		return new CommonListResponse();

	}
}