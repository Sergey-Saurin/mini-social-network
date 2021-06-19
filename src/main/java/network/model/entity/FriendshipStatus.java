package network.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "friendships_status")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    public FriendshipStatus(LocalDateTime time, String name, String code) {
        this.time = time;
        this.name = name;
        this.code = code;
    }
}
