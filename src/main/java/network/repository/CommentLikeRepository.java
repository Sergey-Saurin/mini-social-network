package network.repository;

import network.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import network.model.entity.CommentLike;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	@Query(value = "SELECT cl FROM CommentLike cl WHERE cl.person=:person AND cl.comment.id=:commentId")
	CommentLike findByPersonAndComment(Person person, long commentId);

	@Query(value = "SELECT cl.person.id FROM CommentLike cl WHERE cl.comment.id=:commentId")
	List<Long> getAllUsersIdWhiLikePost(long commentId);

	@Modifying
	@Query("DELETE FROM CommentLike cl WHERE cl.comment.id=:commentId")
	void deleteByCommentId(long commentId);
}
