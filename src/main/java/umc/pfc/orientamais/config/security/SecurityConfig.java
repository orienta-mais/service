package umc.pfc.orientamais.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import umc.pfc.orientamais.config.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfig corsConfig;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
      .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
      .sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(
        auth ->
          auth
            // Endpoints públicos
            .requestMatchers(
              "/swagger-ui/**",
              "/v3/api-docs/**",
              "/swagger-ui.html",
              "/api/auth/login",
              "/api/auth/refresh-token",
              "/api/auth/forget-password",
              "/api/auth/reset-password",
              "/api/mentor/validate-email",
              "/api/mentor/register",
              "/api/mentored/validate-email",
              "/api/mentored/register,",
              "/api/terms/**")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/admin/**")
            .hasRole("ADMIN")

            // GET /api/mentor/** - ADMIN, MENTOR, MENTORED
            .requestMatchers(HttpMethod.GET, "/api/mentor/**")
            .hasAnyRole("ADMIN", "MENTOR", "MENTORED")

            // PUT/PATCH /api/mentor/** - ADMIN, MENTOR
            .requestMatchers(HttpMethod.PUT, "/api/mentor/**")
            .hasAnyRole("ADMIN", "MENTOR")
            .requestMatchers(HttpMethod.PATCH, "/api/mentor/**")
            .hasAnyRole("ADMIN", "MENTOR")

            // DELETE /api/mentor/** - ADMIN, MENTOR
            .requestMatchers(HttpMethod.DELETE, "/api/mentor/**")
            .hasAnyRole("ADMIN", "MENTOR")

            // GET /api/mentored/** - ADMIN, MENTORED
            .requestMatchers(HttpMethod.GET, "/api/mentored/**")
            .hasAnyRole("ADMIN", "MENTORED")

            // PUT/PATCH /api/mentored/** - ADMIN, MENTORED
            .requestMatchers(HttpMethod.PUT, "/api/mentored/**")
            .hasAnyRole("ADMIN", "MENTORED")
            .requestMatchers(HttpMethod.PATCH, "/api/mentored/**")
            .hasAnyRole("ADMIN", "MENTORED")

            // DELETE /api/mentored/** - ADMIN, MENTORED
            .requestMatchers(HttpMethod.DELETE, "/api/mentored/**")
            .hasAnyRole("ADMIN", "MENTORED")

            // Qualquer outra requisição precisa estar autenticada
            .anyRequest()
            .authenticated())
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}
