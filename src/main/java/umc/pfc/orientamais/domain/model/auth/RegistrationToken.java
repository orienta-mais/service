package umc.pfc.orientamais.domain.model.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_token")
public class RegistrationToken {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthUserRole role;

    public RegistrationToken(String email, String token, LocalDateTime expiration, AuthUserRole role) {
        this.email = email;
        this.token = token;
        this.expiration = expiration;
        this.role = role;
    }
}