package umc.pfc.orientamais.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "auth_user")
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthUserRole role;

    public AuthUser(String email, String password, AuthUserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
