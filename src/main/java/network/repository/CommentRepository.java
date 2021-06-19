package network.repository;

import network.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  Comment getByIdAndPostId(long commentId, long postId);
  Page<Comment> findAllByPostIdAndDeletedFalse(long postId, Pageable pageable);
}
