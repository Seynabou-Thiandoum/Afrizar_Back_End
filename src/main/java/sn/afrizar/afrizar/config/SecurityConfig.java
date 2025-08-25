package sn.afrizar.afrizar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics (pas d'authentification requise)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Endpoints pour les clients (authentification requise)
                .requestMatchers("/api/produits/**").permitAll() // Lecture libre des produits
                .requestMatchers("/api/categories/**").permitAll() // Lecture libre des catégories
                
                // Endpoints pour les vendeurs
                .requestMatchers("/api/vendeurs/**").hasAnyRole("VENDEUR", "ADMIN")
                
                // Endpoints pour les admins
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/statistiques/**").hasAnyRole("ADMIN", "SUPPORT")
                
                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable()); // Pour H2 console
        
        return http.build();
    }
}
