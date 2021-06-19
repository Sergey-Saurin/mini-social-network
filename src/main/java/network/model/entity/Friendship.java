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
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;

@Entity
@Table(name = "friendships")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", nullable = false)
    private FriendshipStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "src_person_id", nullable = false)
    private Person srcPerson;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dst_person_id", nullable = false)
    private Person dstPerson;

    public Friendship(FriendshipStatus status, Person srcPerson, Person dstPerson) {
        this.status = status;
        this.srcPerson = srcPerson;
        this.dstPerson = dstPerson;
    }
}
