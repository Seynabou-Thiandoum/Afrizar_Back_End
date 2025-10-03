package sn.afrizar.afrizar.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PasswordPolicyValidator {
    
    // Patterns pour la validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    
    // Mots de passe communs à éviter
    private static final List<String> COMMON_PASSWORDS = List.of(
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "1234567890", "password1",
        "qwerty123", "dragon", "master", "hello", "freedom", "whatever",
        "qazwsx", "trustno1", "654321", "jordan", "harley", "password1",
        "1234", "robert", "matthew", "jordan", "asshole", "daniel"
    );
    
    public PasswordValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Vérification de la longueur minimale
        if (password == null || password.length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caractères");
        }
        
        // Vérification de la longueur maximale
        if (password != null && password.length() > 128) {
            errors.add("Le mot de passe ne peut pas dépasser 128 caractères");
        }
        
        if (password != null) {
            // Vérification des caractères requis
            if (!UPPERCASE_PATTERN.matcher(password).find()) {
                errors.add("Le mot de passe doit contenir au moins une lettre majuscule");
            }
            
            if (!LOWERCASE_PATTERN.matcher(password).find()) {
                errors.add("Le mot de passe doit contenir au moins une lettre minuscule");
            }
            
            if (!DIGIT_PATTERN.matcher(password).find()) {
                errors.add("Le mot de passe doit contenir au moins un chiffre");
            }
            
            if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
                errors.add("Le mot de passe doit contenir au moins un caractère spécial (!@#$%^&*()_+-=[]{}|;':\",./<>?)");
            }
            
            // Vérification des mots de passe communs
            if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
                errors.add("Ce mot de passe est trop commun et facile à deviner");
            }
            
            // Vérification des séquences répétitives
            if (hasRepeatingCharacters(password)) {
                warnings.add("Le mot de passe contient des caractères répétitifs");
            }
            
            // Vérification des séquences numériques
            if (hasSequentialNumbers(password)) {
                warnings.add("Le mot de passe contient des séquences numériques");
            }
            
            // Vérification de la complexité
            if (password.length() < 12) {
                warnings.add("Pour une sécurité optimale, utilisez au moins 12 caractères");
            }
        }
        
        boolean isValid = errors.isEmpty();
        int strength = calculatePasswordStrength(password);
        
        return new PasswordValidationResult(isValid, errors, warnings, strength);
    }
    
    private boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i + 1) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasSequentialNumbers(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (Character.isDigit(password.charAt(i))) {
                int first = Character.getNumericValue(password.charAt(i));
                int second = Character.getNumericValue(password.charAt(i + 1));
                int third = Character.getNumericValue(password.charAt(i + 2));
                
                if (second == first + 1 && third == second + 1) {
                    return true;
                }
                if (second == first - 1 && third == second - 1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int calculatePasswordStrength(String password) {
        if (password == null) return 0;
        
        int score = 0;
        
        // Longueur
        if (password.length() >= 8) score += 1;
        if (password.length() >= 12) score += 1;
        if (password.length() >= 16) score += 1;
        
        // Complexité des caractères
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 1;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 1;
        if (DIGIT_PATTERN.matcher(password).find()) score += 1;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 1;
        
        // Diversité des caractères
        long uniqueChars = password.chars().distinct().count();
        if (uniqueChars >= 8) score += 1;
        if (uniqueChars >= 12) score += 1;
        
        return Math.min(score, 10); // Score maximum de 10
    }
    
    public static class PasswordValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        private final int strength;
        
        public PasswordValidationResult(boolean valid, List<String> errors, List<String> warnings, int strength) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
            this.strength = strength;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public int getStrength() {
            return strength;
        }
        
        public String getStrengthDescription() {
            if (strength <= 3) return "Très faible";
            if (strength <= 5) return "Faible";
            if (strength <= 7) return "Moyen";
            if (strength <= 9) return "Fort";
            return "Très fort";
        }
    }
}
