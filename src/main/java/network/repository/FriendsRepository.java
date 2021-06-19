package network.repository;

import network.model.entity.dto.FriendsIdDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import network.model.entity.Friendship;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friendship, Long> {

    @Query(value = "SELECT new skillbox.javapro11.model.entity.dto" +
            ".FriendsIdDTO(f.dstPerson.id, fs.code) FROM FriendshipStatus fs " +
            "INNER JOIN Friendship f ON f.status.id = fs.id " +
            "WHERE f.dstPerson.id = :dstId AND f.srcPerson.id IN :userIds  AND fs.code = 'FRIEND'")
    List<FriendsIdDTO> isFriends(@Param("dstId") long dstId, @Param("userIds") List<Long> userIds);

    @Query("SELECT f FROM Friendship f " +
            "WHERE (f.dstPerson.id = :dstId AND f.srcPerson.id = :srcId) " +
            "OR (f.dstPerson.id = :srcId AND f.srcPerson.id = :dstId)")
    Optional<Friendship> findAllBySrcPersonIdAndDstPersonId(@Param("srcId") long srcPersonId, @Param("dstId") long dstPersonId);
}
