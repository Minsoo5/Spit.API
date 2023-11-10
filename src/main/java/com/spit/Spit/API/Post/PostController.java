package com.spit.Spit.API.Post;

import com.spit.Spit.API.Account.Account;
import com.spit.Spit.API.Account.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostServices postServices;
    private final AccountService accountServices;

    public PostController(PostServices postServices, AccountService accountServices) {
        this.postServices = postServices;
        this.accountServices = accountServices;
    }

    @PostMapping("/add")
    public ResponseEntity<String> createPost(@Valid @RequestBody CreatePostDTO postDTO) {

        Optional<Account> exist = accountServices.getAccountById(postDTO.getAccountId());

        if(exist.isEmpty()) {
            String message = "Associated account does not exist.";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        Post post = new Post();
        post.setAccount(exist.get());
        post.setMessage(postDTO.getMessage());

        String message = postServices.createPost(post);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> exist = postServices.getPostById(id);
        return exist.map(post -> new ResponseEntity<>(post, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/handle/{handle}")
    public ResponseEntity<List<GetPostDTO>> getPostsByHandle(@PathVariable String handle) {
        Optional<Account> exist = accountServices.getAccountByHandle(handle);

        if(exist.isPresent()) {
            List<GetPostDTO> posts = postServices.getPostsByHandleDESC(handle);

            return new ResponseEntity<>(posts, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts() {
        return new ResponseEntity<>(postServices.getAllPost(), HttpStatus.OK);
    }

    @GetMapping("/desc/all")
    public ResponseEntity<List<GetPostDTO>> getAllPostsDESC() {
        return new ResponseEntity<>(postServices.getAllPostDESC(), HttpStatus.OK); }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable Long id) {

        Optional<Post> exist = postServices.getPostById(id);

        if(exist.isPresent()) {
            postServices.deletePostById(id);
            String message = "Post with id " +id+ " has been deleted.";
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
