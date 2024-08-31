package tattoowebsite.tattobackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tattoowebsite.tattobackend.model.Comment;
import tattoowebsite.tattobackend.service.CommentService;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:5500"}) // Permitir múltiplas origens
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    @PostMapping
    public Comment saveComment(@RequestBody Comment comment) {
        return commentService.saveComment(comment);
    }

    @DeleteMapping("/{id}")
    public void rejectComment(@PathVariable Long id) {
        commentService.rejectComment(id);
    }
}
