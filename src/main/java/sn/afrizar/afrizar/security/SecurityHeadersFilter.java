package sn.afrizar.afrizar.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // En-têtes de sécurité HTTP
        addSecurityHeaders(response);
        
        filterChain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Protection contre le clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Protection contre le MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Protection XSS
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Politique de référent
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Politique de permissions
        response.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=(), payment=(), usb=(), magnetometer=(), gyroscope=(), speaker=()");
        
        // Content Security Policy (CSP)
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com; " +
            "img-src 'self' data: https:; " +
            "connect-src 'self' https:; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self'");
        
        // Strict Transport Security (HSTS) - seulement pour HTTPS
        if (isHttpsRequest()) {
            response.setHeader("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains; preload");
        }
        
        // En-tête pour indiquer que le serveur ne supporte pas les anciennes versions
        response.setHeader("X-Powered-By", ""); // Supprimer l'en-tête X-Powered-By
        
        // En-tête pour indiquer la version de l'API
        response.setHeader("X-API-Version", "1.0");
        
        // En-tête pour la cache control
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    private boolean isHttpsRequest() {
        // En production, vérifier si la requête est en HTTPS
        // Pour le développement, on peut retourner false
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Ne pas appliquer les en-têtes de sécurité sur les endpoints de documentation
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/api-docs") ||
               path.startsWith("/h2-console");
    }
}

