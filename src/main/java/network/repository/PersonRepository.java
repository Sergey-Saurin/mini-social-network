package network.repository;

import network.model.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Person findByEmail(String email);

    Person findById(long id);

    Person findByPassword(String passwordNew);

    @Query(value = "select p.*\n" +
            "from person p, person2dialog d\n" +
            "where  d.person_id = p.id and  d.dialog_id = ?1", nativeQuery = true)
    List<Person> findPersonByDialog(long dialogId);

    @Query("SELECT p FROM Person p WHERE p.id IN " +
            "(SELECT p.id FROM Person p LEFT JOIN Friendship f ON f.srcPerson.id = p.id " +
            "INNER JOIN FriendshipStatus fs ON f.status.id = fs.id " +
            "INNER JOIN Person t ON f.dstPerson.id = t.id WHERE t.id = :id AND fs.code = :code) " +
            "OR p.id IN " +
            "(SELECT a.id FROM Person a LEFT JOIN Friendship fa ON fa.dstPerson.id = a.id " +
            "INNER JOIN FriendshipStatus fas ON fa.status.id = fas.id " +
            "INNER JOIN Person b ON fa.srcPerson.id = b.id WHERE b.id = :id AND fas.code = :code)")
    Page<Person> findAllFriends(@Param("id") long id, @Param("code") String code, Pageable pageable);

    @Query("SELECT c FROM Person a " +
            "INNER JOIN Friendship f ON f.dstPerson.id = a.id " +
            "INNER JOIN Person b ON f.srcPerson.id = b.id " +
            "INNER JOIN FriendshipStatus fs ON fs.id = f.status.id " +
            "INNER JOIN Friendship ff ON ff.dstPerson.id = b.id " +
            "INNER JOIN Person c ON ff.srcPerson.id = c.id " +
            "INNER JOIN FriendshipStatus ffs ON ffs.id = ff.status.id " +
            "WHERE a.id = :id AND a.id != c.id GROUP BY c.id HAVING COUNT(c.id) >= 2 ")
    Page<Person> getRecommendations(@Param("id") long id, Pageable pageable);
}
