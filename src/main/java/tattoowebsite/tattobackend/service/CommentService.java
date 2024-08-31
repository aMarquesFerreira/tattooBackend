package tattoowebsite.tattobackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tattoowebsite.tattobackend.model.Comment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CommentService {

    private final AtomicLong idGenerator = new AtomicLong();

    @Autowired
    private ExcelService excelService;

    public List<Comment> getAllComments() {
        try {
            return excelService.readComments();
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Comment saveComment(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(idGenerator.incrementAndGet());
        }
        try {
            excelService.writeComment(comment);
            return comment;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar o comentário: " + e.getMessage());
        }
    }

    public void rejectComment(Long id) {
        try {
            excelService.deleteComment(id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao rejeitar o comentário: " + e.getMessage());
        }
    }
}
