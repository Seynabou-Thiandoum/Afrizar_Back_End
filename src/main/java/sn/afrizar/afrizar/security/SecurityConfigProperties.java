package sn.afrizar.afrizar.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigProperties {
    
    // Configuration JWT
    private Jwt jwt = new Jwt();
    
    // Configuration Rate Limiting
    private RateLimit rateLimit = new RateLimit();
    
    // Configuration Password Policy
    private PasswordPolicy passwordPolicy = new PasswordPolicy();
    
    @Data
    public static class Jwt {
        private String secret = "afrizarSecretKeyForJWT2024VerySecureAndLongEnoughForSecurity";
        private Long expiration = 86400000L; // 24 heures
        private Long refreshExpiration = 604800000L; // 7 jours
    }
    
    @Data
    public static class RateLimit {
        private int authRequestsPerMinute = 5;
        private int generalRequestsPerMinute = 100;
        private int apiRequestsPerMinute = 200;
        private int maxCacheSize = 10000;
        private int cacheExpirationMinutes = 10;
    }
    
    @Data
    public static class PasswordPolicy {
        private int minLength = 8;
        private int maxLength = 128;
        private boolean requireUppercase = true;
        private boolean requireLowercase = true;
        private boolean requireDigit = true;
        private boolean requireSpecialChar = true;
        private int maxConsecutiveChars = 2;
        private boolean checkCommonPasswords = true;
    }
}

