package network.controller;

import network.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feeds")
public class FeedController {

    @Autowired
    private FeedService feedService;


    @GetMapping("")
    public ResponseEntity<?> getNewsList(@RequestParam(required = false, defaultValue = "") String name,
                                         @RequestParam(required = false, defaultValue = "0") long offset,
                                         @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) {
        return new ResponseEntity<>(feedService.getNewsList(name, offset, itemPerPage), HttpStatus.OK);
    }
}
