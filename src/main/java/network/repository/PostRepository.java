package network.repository;

import network.model.entity.Person;
import network.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findAllByPerson(Person person, Pageable pageable);

	Page<Post> findAllByPersonAndIsDeletedFalse(Person person, Pageable pageable);

	@Query(value = "SELECT * \n" +
			"FROM post \n" +
			"WHERE lower(post_text) LIKE lower('%'||?2||'%') \n" +
			" and time between ?3 and ?4 \n" +
			" and is_deleted = false", nativeQuery = true)
	Page<Post> findAllPostsBySearch(Pageable page, String text, LocalDateTime dateFrom, LocalDateTime dateTo);


	@Query("SELECT po FROM Post po WHERE person.id IN " +
			"(SELECT t FROM Person p LEFT JOIN Friendship f ON f.dstPerson.id = p.id  " +
			"INNER JOIN FriendshipStatus fs ON f.status.id = fs.id " +
			"INNER JOIN Person t ON f.srcPerson.id = t.id WHERE p.id = :id AND fs.code = :code)")
    Page<Post> findAllNews(@Param("id") long id, @Param("code") String code, Pageable page);
}
