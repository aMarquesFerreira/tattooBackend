package tattoowebsite.tattobackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tattoowebsite.tattobackend.model.AcceptedComment;
import tattoowebsite.tattobackend.model.Comment;
import tattoowebsite.tattobackend.service.AcceptedCommentService;

import java.util.List;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:4200"})
@RestController
@RequestMapping("/api/accepted-comments")
public class AcceptedCommentController {

    @Autowired
    private AcceptedCommentService acceptedCommentService;

    @GetMapping
    public List<AcceptedComment> getAllAcceptedComments() {
        return acceptedCommentService.getAllAcceptedComments();
    }

    @PostMapping
    public AcceptedComment acceptComment(@RequestBody Comment comment) {
        return acceptedCommentService.acceptComment(comment);
    }

    @DeleteMapping("/{id}")
    public void deleteAcceptedComment(@PathVariable Long id) {
        acceptedCommentService.deleteAcceptedComment(id);
    }
}
