# Guide de Sécurité - Afrizar Backend

## 🔐 Vue d'ensemble de la sécurité

Ce document décrit les mesures de sécurité implémentées dans l'application Afrizar Backend.

## 🛡️ Fonctionnalités de sécurité implémentées

### 1. Authentification JWT
- **Tokens JWT** avec expiration configurable (24h par défaut)
- **Validation automatique** des tokens via filtre
- **Invalidation** des tokens lors de la déconnexion
- **Secret key** configurable et sécurisé

### 2. Politique de mot de passe robuste
- **Longueur minimale** : 8 caractères
- **Longueur maximale** : 128 caractères
- **Caractères requis** :
  - Au moins une lettre majuscule
  - Au moins une lettre minuscule
  - Au moins un chiffre
  - Au moins un caractère spécial
- **Protection contre** :
  - Mots de passe communs
  - Caractères répétitifs
  - Séquences numériques

### 3. Rate Limiting
- **Limites par type d'endpoint** :
  - Authentification : 5 requêtes/minute
  - API générale : 200 requêtes/minute
  - Autres : 100 requêtes/minute
- **Cache intelligent** avec expiration automatique
- **En-têtes de réponse** avec informations de limite

### 4. En-têtes de sécurité HTTP
- **X-Frame-Options** : Protection contre le clickjacking
- **X-Content-Type-Options** : Protection contre le MIME sniffing
- **X-XSS-Protection** : Protection XSS
- **Content-Security-Policy** : Politique de sécurité du contenu
- **Strict-Transport-Security** : Force HTTPS (en production)
- **Referrer-Policy** : Contrôle des référents

### 5. Gestion des rôles et autorisations
- **Rôles** : CLIENT, VENDEUR, ADMIN, SUPPORT
- **Autorisations granulaires** par endpoint
- **Méthode-level security** avec `@PreAuthorize`

### 6. Audit et logging de sécurité
- **Journalisation** de tous les événements de sécurité
- **Suivi des** :
  - Connexions réussies/échouées
  - Inscriptions
  - Tentatives d'accès refusées
  - Activités suspectes
  - Changements de mot de passe

### 7. Gestion des exceptions de sécurité
- **Handlers personnalisés** pour les erreurs d'authentification
- **Messages d'erreur** sécurisés (pas d'informations sensibles)
- **Logging** des tentatives d'accès non autorisées

## 🔧 Configuration

### Variables d'environnement
```properties
# JWT
app.security.jwt.secret=your-secret-key
app.security.jwt.expiration=86400000

# Rate Limiting
app.security.rate-limit.auth-requests-per-minute=5
app.security.rate-limit.api-requests-per-minute=200

# Password Policy
app.security.password-policy.min-length=8
app.security.password-policy.require-special-char=true
```

### Endpoints de sécurité
- `POST /api/security/validate-password` - Valider un mot de passe
- `GET /api/security/security-info` - Informations de sécurité (Admin)
- `POST /api/security/test-audit` - Test d'audit (Admin)

## 🚀 Utilisation

### Authentification
```bash
# Inscription
POST /api/auth/inscription
{
  "email": "user@example.com",
  "motDePasse": "SecurePass123!",
  "role": "CLIENT",
  "nom": "Doe",
  "prenom": "John"
}

# Connexion
POST /api/auth/connexion
{
  "email": "user@example.com",
  "motDePasse": "SecurePass123!"
}
```

### Utilisation des tokens
```bash
# Inclure le token dans les requêtes
Authorization: Bearer <your-jwt-token>
```

## 🔍 Monitoring et audit

### Logs de sécurité
Les événements de sécurité sont journalisés avec le préfixe `SECURITY_AUDIT:` :

```
SECURITY_AUDIT: AUTHENTICATION_SUCCESS - User: user@example.com - IP: 192.168.1.1 - Time: 2024-01-15 10:30:00
SECURITY_AUDIT: ACCESS_DENIED - User: user@example.com - Resource: /api/admin/users - Method: GET - IP: 192.168.1.1
```

### Métriques de sécurité
- Nombre de tentatives de connexion échouées
- Requêtes bloquées par rate limiting
- Tentatives d'accès non autorisées
- Mots de passe faibles détectés

## ⚠️ Bonnes pratiques

### Pour les développeurs
1. **Toujours valider** les entrées utilisateur
2. **Utiliser HTTPS** en production
3. **Ne jamais logger** les mots de passe ou tokens
4. **Implémenter** la rotation des secrets
5. **Monitorer** les logs de sécurité

### Pour les utilisateurs
1. **Utiliser des mots de passe forts** (12+ caractères)
2. **Ne jamais partager** les tokens
3. **Se déconnecter** sur les appareils partagés
4. **Signaler** toute activité suspecte

## 🔄 Maintenance

### Rotation des secrets
- Changer le secret JWT régulièrement
- Mettre à jour les certificats SSL
- Réviser les politiques de sécurité

### Mise à jour
- Surveiller les vulnérabilités des dépendances
- Mettre à jour Spring Security
- Tester les nouvelles fonctionnalités de sécurité

## 📞 Support

Pour toute question de sécurité, contactez l'équipe de développement ou consultez les logs d'audit.

---

**Note** : Ce guide doit être mis à jour à chaque modification des mesures de sécurité.


