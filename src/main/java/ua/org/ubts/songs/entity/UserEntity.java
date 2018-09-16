package ua.org.ubts.songs.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity<Long> {

	@NotEmpty
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotEmpty
	@Column(name = "password", nullable = false)
	private String password;

	@NotEmpty
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@NotEmpty
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "phone")
	private String phone;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_role",
			joinColumns = @JoinColumn(
					name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
					name = "role_id", referencedColumnName = "id"))
	private List<RoleEntity> roles;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private SubscriptionEntity subscription;

}
