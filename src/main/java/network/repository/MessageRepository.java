package network.repository;

import network.model.entity.Dialog;
import network.model.entity.Message;
import network.model.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findById(long id);

    @Query( "SELECT COUNT(m) " +
            "FROM Message m " +
            "WHERE m.readStatus = 'SENT'" +
            "AND m.dialog IN(SELECT d " +
                            "FROM Person2Dialog p2d " +
                            "JOIN p2d.dialog d " +
                            "WHERE p2d.person = :currentPerson " +
                            "GROUP BY d) ")
    int getUnreadCountOfPerson(Person currentPerson);

    @Query( "SELECT m " +
            "FROM Message m " +
            "WHERE m.dialog = :dialog " +
            "AND m.time IN (SELECT MAX(m2.time) as time " +
                            "FROM Message m2 " +
                            "WHERE m2.dialog = :dialog)")
    Message getLastMessageOfDialog(Dialog dialog);

    @Query( "SELECT m.time " +
            "FROM Message m " +
            "WHERE m.dialog = :dialog AND m.author = :currentPerson " +
            "AND m.time IN (SELECT MAX(m2.time) as time " +
                            "FROM Message m2 " +
                            "WHERE m2.dialog = :dialog)")
    LocalDateTime getTheTimeOfTheLastMessageOfTheDialogFromThePerson(Dialog dialog, Person currentPerson);

    @Query( "SELECT COUNT(m) " +
            "FROM Message m " +
            "WHERE m.dialog = :dialog AND m.readStatus = 'SENT'")
    int getUnreadCountOfDialog(Dialog dialog);

    @Query( "SELECT m " +
            "FROM Message m " +
            "WHERE m.dialog.id = :idDialog")
    Page<Message> getMessageOfDialog(Pageable pageable, long idDialog);

    @Query("SELECT m " +
            "FROM Message m " +
            "WHERE m.id = :idMessage AND m.dialog.id = :idDialog")
    Message findByIdAndDialog(long idMessage, long idDialog);
}









