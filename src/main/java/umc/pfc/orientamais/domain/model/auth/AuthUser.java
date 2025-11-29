package umc.pfc.orientamais.domain.model.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import umc.pfc.orientamais.adapters.input.rest.dto.request.UserRegisterModelRequest;

import java.util.UUID;

@Entity
@Table(name = "auth_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements Profile {

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

    public void updatePassword(String newPassword, BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void fillFromRequest(AuthUser user, UserRegisterModelRequest request) {
    }
}