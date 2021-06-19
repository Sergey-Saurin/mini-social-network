package network.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import network.model.entity.Person2Dialog;

/**
 * Created by timurg on 01.05.2021.
 */
public interface Person2DialogRepository extends JpaRepository<Person2Dialog, Long> {

    @Transactional
    @Query(
            value = "DELETE INTO person2dialog WHERE person_id = :person_id, dialog_id = :dialog_id",
            nativeQuery = true)
    void deletePersInDialog(@Param("dialog_id") long dialogId, @Param("person_id") long personId);
}
