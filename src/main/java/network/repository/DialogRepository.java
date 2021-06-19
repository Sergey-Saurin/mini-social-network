package network.repository;

import network.model.entity.Dialog;
import network.model.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by timurg on 28.04.2021.
 */
@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {

    Dialog findById(long id);

    //    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO dialog (owner_id, is_deleted) VALUES (:owner_id, true) RETURNING id",
            nativeQuery = true)
    long insertDialog(@Param("owner_id") long owner_id);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE dialog g SET g.is_deleted = false WHERE g.id = :id",
            nativeQuery = true)
    void deleteDialog(@Param("id") long id);

    @Query("SELECT d " +
            "FROM Message m " +
                "JOIN m.dialog d " +
            "WHERE m.text LIKE %:query% " +
            "AND m.dialog IN(SELECT d " +
                            "FROM Person2Dialog p2d " +
                            "JOIN p2d.dialog d " +
                            "WHERE p2d.person = :currentPerson " +
                            "GROUP BY d) " +
            "GROUP BY d")
    Page<Dialog> getDialogsOfPersonWithQuery(Pageable pageable, String query, Person currentPerson);

    @Query("SELECT d " +
            "FROM Person2Dialog p2d " +
                "JOIN p2d.dialog d " +
            "WHERE p2d.person = :currentPerson " +
            "GROUP BY d")
    Page<Dialog> getDialogsOfPerson(Pageable pageable, Person currentPerson);

    @Query( "SELECT d.inviteCode " +
            "FROM Dialog d " +
            "WHERE d.id = :idDialog ")
    String getInviteByDialog(long idDialog);

}















