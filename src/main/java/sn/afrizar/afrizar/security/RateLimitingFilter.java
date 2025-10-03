package sn.afrizar.afrizar.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Cache pour stocker les buckets par IP
    private final Cache<String, Bucket> bucketCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(10000)
            .build();

    // Configuration des limites de taux
    private static final int AUTH_REQUESTS_PER_MINUTE = 5; // Tentatives de connexion
    private static final int GENERAL_REQUESTS_PER_MINUTE = 100; // Requêtes générales
    private static final int API_REQUESTS_PER_MINUTE = 200; // Requêtes API

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String requestPath = request.getRequestURI();
        
        // Déterminer le type de requête et la limite appropriée
        Bucket bucket = getBucketForRequest(clientIp, requestPath);
        
        if (bucket.tryConsume(1)) {
            // Ajouter les en-têtes de rate limiting
            response.setHeader("X-RateLimit-Limit", String.valueOf(bucket.getAvailableTokens()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            
            filterChain.doFilter(request, response);
        } else {
            // Limite de taux dépassée
            log.warn("Rate limit dépassé pour IP: {} sur le chemin: {}", clientIp, requestPath);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60"); // Retry après 60 secondes
            
            String errorResponse = String.format(
                "{\"error\":\"Trop de requêtes\",\"message\":\"Limite de taux dépassée. Veuillez réessayer dans %d secondes.\",\"retryAfter\":60}",
                bucket.getAvailableTokens()
            );
            
            response.getWriter().write(errorResponse);
        }
    }

    private Bucket getBucketForRequest(String clientIp, String requestPath) {
        return bucketCache.get(clientIp + ":" + getRequestType(requestPath), key -> {
            String[] parts = key.split(":", 2);
            String requestType = parts[1];
            
            Bandwidth limit = getBandwidthForRequestType(requestType);
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    private Bandwidth getBandwidthForRequestType(String requestType) {
        switch (requestType) {
            case "auth":
                return Bandwidth.classic(AUTH_REQUESTS_PER_MINUTE, 
                    Refill.intervally(AUTH_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
            case "api":
                return Bandwidth.classic(API_REQUESTS_PER_MINUTE, 
                    Refill.intervally(API_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
            default:
                return Bandwidth.classic(GENERAL_REQUESTS_PER_MINUTE, 
                    Refill.intervally(GENERAL_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        }
    }

    private String getRequestType(String requestPath) {
        if (requestPath.startsWith("/api/auth/")) {
            return "auth";
        } else if (requestPath.startsWith("/api/")) {
            return "api";
        } else {
            return "general";
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Ne pas appliquer le rate limiting sur les endpoints de santé
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") || 
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs");
    }
}
