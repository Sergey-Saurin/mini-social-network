package network.controller;

import network.api.request.CommentRequest;
import network.api.response.CommonResponseData;
import network.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import network.api.request.PostRequest;

/**
 * Created by Artem on 03.04.2021.
 */

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping("/")
    public ResponseEntity<?>  getPostSearch (@RequestParam ("text") String text,
                                             @RequestParam ("date_from") long dateFrom,
                                             @RequestParam ("date_to") long dateTo,
                                             @RequestParam (required = false, defaultValue = "0") long offset,
                                             @RequestParam (required = false, defaultValue = "20") Integer itemPerPage
                                             ){
        return new ResponseEntity<>(postService.getPostSearch(text, dateFrom, dateTo, offset,
                                                                itemPerPage), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") long postId){
        return new ResponseEntity<>(postService.getPostByID(postId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editPostById(@PathVariable("id") long postId,
                             @RequestBody PostRequest post){
        CommonResponseData commonResponseData = postService.editPostById(postId, System.currentTimeMillis(), post);
        return new ResponseEntity<>(commonResponseData, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePostById(@PathVariable("id") long postId){
        return new ResponseEntity<>(postService.deletePostById(postId), HttpStatus.OK);
    }

    @PutMapping("/{id}/recover")
    public ResponseEntity<?> recoverPostById(@PathVariable("id") long postId){
        return new ResponseEntity<>(postService.recoverPostById(postId), HttpStatus.OK);
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> reportPost(@PathVariable("id") long postId){
        return new ResponseEntity<>(postService.reportPost(postId), HttpStatus.OK);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable(name = "id") long postId, @RequestParam int offset, @RequestParam int itemPerPage){
        return new ResponseEntity<>(postService.getComments(postId, itemPerPage, offset), HttpStatus.OK);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createComment(@PathVariable(name = "id") long postId, @RequestBody CommentRequest comment){
        return new ResponseEntity<>(postService.editedComment(postId, null, comment), HttpStatus.OK);
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<?> editComment(@PathVariable(name = "id") long postId,
                                         @PathVariable(name = "comment_id") long idComment, @RequestBody CommentRequest comment){
        return new ResponseEntity<>(postService.editedComment(postId, idComment, comment), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable(name = "id") long postId,@PathVariable(name = "comment_id") long idComment){
        return new ResponseEntity<>(postService.deleteComment(postId, idComment), HttpStatus.OK);
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    public ResponseEntity<?> recoverComment(@PathVariable(name = "id") long postId,@PathVariable(name = "comment_id") long idComment){
        return new ResponseEntity<>(postService.recoverComment(postId, idComment), HttpStatus.OK);
    }

    @PostMapping("/{id}/comments/{comment_id}/report")
    public ResponseEntity<?> reportComment(@PathVariable(name = "id") long postId,@PathVariable(name = "comment_id") long idComment){
        return new ResponseEntity<>(postService.reportComment(postId, idComment) ,HttpStatus.OK);
    }
}
