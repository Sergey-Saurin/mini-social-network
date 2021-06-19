package network.controller;
import network.api.response.CommonListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/platform")
public class PlatformController {
    @GetMapping("/languages")
    public CommonListResponse getLanguages() {
        return new CommonListResponse("", LocalDateTime.now(), null);
    }
}
