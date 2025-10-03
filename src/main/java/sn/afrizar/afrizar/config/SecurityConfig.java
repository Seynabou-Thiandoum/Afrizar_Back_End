package sn.afrizar.afrizar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import sn.afrizar.afrizar.security.JwtAuthenticationFilter;
import sn.afrizar.afrizar.security.RateLimitingFilter;
import sn.afrizar.afrizar.security.SecurityExceptionHandler;
import sn.afrizar.afrizar.security.SecurityHeadersFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityExceptionHandler securityExceptionHandler;
    private final RateLimitingFilter rateLimitingFilter;
    private final SecurityHeadersFilter securityHeadersFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Augmenter la force du hachage
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF pour les APIs REST
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Gestion des sessions stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics (pas d'authentification requise)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Endpoints de lecture publique
                .requestMatchers("GET", "/api/produits/**").permitAll()
                .requestMatchers("GET", "/api/categories/**").permitAll()
                
                // Endpoints pour les clients authentifiés
                .requestMatchers("/api/clients/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers("/api/commandes/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers("/api/livraisons/**").hasAnyRole("CLIENT", "ADMIN")
                
                // Endpoints pour les vendeurs
                .requestMatchers("/api/vendeurs/**").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("POST", "/api/produits/**").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("PUT", "/api/produits/**").hasAnyRole("VENDEUR", "ADMIN")
                .requestMatchers("DELETE", "/api/produits/**").hasAnyRole("VENDEUR", "ADMIN")
                
                // Endpoints pour les admins
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/statistiques/**").hasAnyRole("ADMIN", "SUPPORT")
                .requestMatchers("/api/calcul/**").hasAnyRole("ADMIN", "SUPPORT")
                
                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Ajouter les filtres de sécurité
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Gestion des exceptions
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(securityExceptionHandler)
                .accessDeniedHandler(securityExceptionHandler)
            )
            
            // Configuration des en-têtes de sécurité
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny()) // Protection contre le clickjacking
                .contentTypeOptions(contentTypeOptions -> {}) // Protection contre le MIME sniffing
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser les origines spécifiques (à adapter selon vos besoins)
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:3000",
            "http://localhost:3001", 
            "https://*.afrizar.com",
            "https://afrizar.vercel.app"
        ));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // En-têtes exposés
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Autoriser les credentials
        configuration.setAllowCredentials(true);
        
        // Durée de cache des preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
