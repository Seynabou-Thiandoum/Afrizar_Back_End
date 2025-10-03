package sn.afrizar.afrizar.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class SecurityAuditLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logAuthenticationSuccess(String email, String ipAddress) {
        log.info("SECURITY_AUDIT: AUTHENTICATION_SUCCESS - User: {} - IP: {} - Time: {}", 
                email, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logAuthenticationFailure(String email, String ipAddress, String reason) {
        log.warn("SECURITY_AUDIT: AUTHENTICATION_FAILURE - User: {} - IP: {} - Reason: {} - Time: {}", 
                email, ipAddress, reason, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logRegistrationSuccess(String email, String ipAddress, String role) {
        log.info("SECURITY_AUDIT: REGISTRATION_SUCCESS - User: {} - Role: {} - IP: {} - Time: {}", 
                email, role, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logRegistrationFailure(String email, String ipAddress, String reason) {
        log.warn("SECURITY_AUDIT: REGISTRATION_FAILURE - User: {} - IP: {} - Reason: {} - Time: {}", 
                email, ipAddress, reason, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logAccessDenied(String resource, String method, String ipAddress) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null && auth.isAuthenticated() ? auth.getName() : "ANONYMOUS";
        
        log.warn("SECURITY_AUDIT: ACCESS_DENIED - User: {} - Resource: {} - Method: {} - IP: {} - Time: {}", 
                user, resource, method, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logSuspiciousActivity(String activity, String ipAddress, String details) {
        log.error("SECURITY_AUDIT: SUSPICIOUS_ACTIVITY - Activity: {} - IP: {} - Details: {} - Time: {}", 
                activity, ipAddress, details, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logPasswordChange(String email, String ipAddress) {
        log.info("SECURITY_AUDIT: PASSWORD_CHANGE - User: {} - IP: {} - Time: {}", 
                email, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logAccountLockout(String email, String ipAddress, String reason) {
        log.error("SECURITY_AUDIT: ACCOUNT_LOCKOUT - User: {} - IP: {} - Reason: {} - Time: {}", 
                email, ipAddress, reason, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logTokenValidation(String email, String ipAddress, boolean valid) {
        if (valid) {
            log.debug("SECURITY_AUDIT: TOKEN_VALIDATION_SUCCESS - User: {} - IP: {} - Time: {}", 
                    email, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        } else {
            log.warn("SECURITY_AUDIT: TOKEN_VALIDATION_FAILURE - User: {} - IP: {} - Time: {}", 
                    email, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        }
    }

    public void logRateLimitExceeded(String ipAddress, String endpoint) {
        log.warn("SECURITY_AUDIT: RATE_LIMIT_EXCEEDED - IP: {} - Endpoint: {} - Time: {}", 
                ipAddress, endpoint, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public void logSecurityEvent(String eventType, String details, String ipAddress) {
        log.info("SECURITY_AUDIT: {} - Details: {} - IP: {} - Time: {}", 
                eventType, details, ipAddress, LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    }

    public String getClientIpAddress(HttpServletRequest request) {
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
}
