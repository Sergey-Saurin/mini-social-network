package network.model.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;


    @Column(name = "parent_id", columnDefinition = "bigint")
    private Long parentId;

    @Column(name = "comment_text", columnDefinition = "text")
    @NotNull
    private String commentText;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotNull
    private LocalDateTime time;

    @Column(name = "author_id")
    @NotNull
    private long authorId;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "is_deleted")
    private boolean deleted;
}
