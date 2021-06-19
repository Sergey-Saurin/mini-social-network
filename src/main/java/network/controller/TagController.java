package network.controller;

import network.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import network.api.response.TagResponse;

/**
 * Created by Artem on 21.04.2021.
 */

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping("/")
    public ResponseEntity<?> getTag (@RequestParam String tag, @RequestParam int offset,
                                     @RequestParam(required = false, defaultValue = "20")int itemPerPage){
        return new ResponseEntity<>(tagService.getTags(tag, offset, itemPerPage), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<?> createTag (@RequestBody TagResponse tag){
        return new ResponseEntity<>(tagService.createTag(tag), HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteTag (@RequestParam long tagId){
        return new ResponseEntity<>(tagService.deleteTag(tagId), HttpStatus.OK);
    }
}
