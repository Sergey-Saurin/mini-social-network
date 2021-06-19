package network.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Egor Sizenko
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "notification")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "type_id", nullable = false)
	private NotificationType type;

	@Column(name = "sent_time", nullable = false)
	private LocalDateTime sentTime;

	//TODO how can I identify what entity is it if different types may have the same ID?
	@Column(name = "entity_id", nullable = false)
	private long entityId;

	@Column(nullable = false)
	private String info;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	@Column(nullable = false)
	private String contact;
}
