package tattoowebsite.tattobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tattoowebsite.tattobackend.model.AcceptedComment;

@Repository
public interface AcceptedCommentRepository extends JpaRepository<AcceptedComment, Long> {
}
