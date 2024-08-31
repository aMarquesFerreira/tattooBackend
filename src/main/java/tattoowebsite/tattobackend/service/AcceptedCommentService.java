package tattoowebsite.tattobackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tattoowebsite.tattobackend.model.Comment;
import tattoowebsite.tattobackend.model.AcceptedComment;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

@Service
public class AcceptedCommentService {

    @Autowired
    private ExcelService excelService;

    public List<AcceptedComment> getAllAcceptedComments() {
        try {
            // Convertendo os coment�rios para AcceptedComment
            return excelService.readAcceptedComments();
        } catch (IOException e) {
            e.printStackTrace();
            // Retornar uma lista vazia ou uma resposta apropriada em caso de erro
            return List.of();
        }
    }

    @Transactional
    public AcceptedComment acceptComment(Comment comment) {
        try {
            // Convertendo Comment para AcceptedComment
            AcceptedComment acceptedComment = new AcceptedComment();
            acceptedComment.setId(comment.getId());
            acceptedComment.setName(comment.getName());
            acceptedComment.setRating(comment.getRating());
            acceptedComment.setDescription(comment.getDescription());
            acceptedComment.setPhoto(comment.getPhoto());

            excelService.writeAcceptedComment(acceptedComment);
            excelService.deleteComment(comment.getId());

            return acceptedComment;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao aceitar o coment�rio: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAcceptedComment(Long id) {
        try {
            excelService.deleteAcceptedComment(id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar o coment�rio aceito: " + e.getMessage());
        }
    }
}
