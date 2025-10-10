# Guide d'Authentification et Gestion des Rôles - Afrizar

## 🎯 Vue d'ensemble

Le système d'authentification Afrizar utilise JWT (JSON Web Tokens) avec une gestion des rôles complète permettant de contrôler l'accès aux différentes fonctionnalités selon le profil utilisateur.

## 👥 Rôles Disponibles

### 1. **ADMIN** (Administrateur)
L'administrateur a un accès complet au système et peut :
- ✅ Valider ou rejeter les produits en attente de publication
- ✅ Gérer tous les vendeurs (vérifier, activer, désactiver)
- ✅ Voir et gérer tous les utilisateurs du système
- ✅ Accéder aux statistiques complètes du système
- ✅ Consulter tous les produits (tous statuts)
- ✅ Gérer les commandes de tous les clients

### 2. **VENDEUR** (Vendeur)
Le vendeur peut :
- ✅ Créer et gérer ses propres produits
- ✅ Voir ses statistiques de vente
- ✅ Gérer ses commandes
- ✅ Mettre à jour son profil et sa boutique
- ⚠️ Ses produits doivent être validés par un admin avant publication (optionnel selon configuration)

### 3. **CLIENT** (Client)
Le client peut :
- ✅ Consulter le catalogue de produits actifs
- ✅ Passer des commandes
- ✅ Gérer son profil et ses adresses
- ✅ Consulter ses commandes et leur historique
- ✅ Accumuler et utiliser des points de fidélité

### 4. **SUPPORT** (Support Client)
Le support peut :
- ✅ Consulter les informations de tous les clients et vendeurs
- ✅ Voir toutes les commandes
- ✅ Aider les utilisateurs (ajout de points fidélité, etc.)
- ✅ Accéder aux statistiques pour le support client
- ❌ Ne peut PAS modifier les produits ou valider les vendeurs

## 🚀 Démarrage Rapide

### 1. Compte Admin par Défaut

Au premier démarrage de l'application, un compte admin est automatiquement créé :

```
Email: admin@afrizar.sn
Mot de passe: Admin@123
```

⚠️ **IMPORTANT** : Changez ce mot de passe dès la première connexion !

### 2. Connexion

**Endpoint:** `POST /api/auth/connexion`

```json
{
  "email": "admin@afrizar.sn",
  "motDePasse": "Admin@123"
}
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "utilisateurId": 1,
  "email": "admin@afrizar.sn",
  "nom": "Admin",
  "prenom": "Afrizar",
  "role": "ADMIN",
  "actif": true
}
```

### 3. Utilisation du Token

Incluez le token dans toutes les requêtes authentifiées :

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Le token JWT contient :
- L'email de l'utilisateur
- Le rôle (ADMIN, CLIENT, VENDEUR, SUPPORT)
- L'ID de l'utilisateur
- Le nom et prénom
- La date d'expiration (24h par défaut)

## 📋 Endpoints par Rôle

### Endpoints ADMIN

#### Gestion des Produits
```http
GET  /api/admin/produits/en-attente        # Produits en attente de validation
GET  /api/admin/produits/tous             # Tous les produits (tous statuts)
PATCH /api/admin/produits/{id}/valider    # Valider un produit
PATCH /api/admin/produits/{id}/rejeter    # Rejeter un produit
```

#### Gestion des Vendeurs
```http
GET  /api/admin/vendeurs/tous              # Tous les vendeurs
GET  /api/admin/vendeurs/non-verifies      # Vendeurs non vérifiés
PATCH /api/admin/vendeurs/{id}/verifier    # Vérifier un vendeur
PATCH /api/admin/vendeurs/{id}/desactiver  # Désactiver un vendeur
PATCH /api/admin/vendeurs/{id}/activer     # Activer un vendeur
```

#### Gestion des Utilisateurs
```http
GET  /api/admin/utilisateurs/tous          # Tous les utilisateurs
PATCH /api/admin/utilisateurs/{id}/desactiver  # Désactiver un utilisateur
PATCH /api/admin/utilisateurs/{id}/activer     # Activer un utilisateur
```

#### Statistiques
```http
GET  /api/admin/statistiques/dashboard     # Statistiques du dashboard admin
```

### Endpoints SUPPORT

```http
GET  /api/support/clients                  # Liste des clients
GET  /api/support/clients/{id}             # Détails d'un client
GET  /api/support/vendeurs                 # Liste des vendeurs
GET  /api/support/commandes                # Toutes les commandes
POST /api/support/clients/{id}/ajouter-points  # Ajouter des points fidélité
```

### Endpoints VENDEUR

```http
POST /api/produits                         # Créer un produit
PUT  /api/produits/{id}                    # Modifier son produit
GET  /api/produits/vendeur/{vendeurId}     # Ses produits
PATCH /api/produits/{id}/stock             # Mettre à jour le stock
```

### Endpoints CLIENT

```http
GET  /api/produits                         # Consulter les produits
GET  /api/produits/{id}                    # Détails d'un produit
POST /api/commandes                        # Passer une commande
GET  /api/commandes/mes-commandes          # Ses commandes
```

### Endpoints Publics (sans authentification)

```http
POST /api/auth/inscription                 # S'inscrire
POST /api/auth/connexion                   # Se connecter
GET  /api/produits                         # Voir les produits (lecture seule)
GET  /api/categories                       # Voir les catégories
```

## 🔐 Sécurité

### Protection des Endpoints

La sécurité est configurée dans `SecurityConfig.java` :

```java
// Endpoints publics
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("GET", "/api/produits/**").permitAll()

// Endpoints pour admin uniquement
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// Endpoints pour admin et support
.requestMatchers("/api/statistiques/**").hasAnyRole("ADMIN", "SUPPORT")

// Endpoints pour clients
.requestMatchers("/api/clients/**").hasAnyRole("CLIENT", "ADMIN")

// Endpoints pour vendeurs
.requestMatchers("/api/vendeurs/**").hasAnyRole("VENDEUR", "ADMIN")
```

### Workflow de Validation

```
1. Vendeur s'inscrit
   ↓
2. Compte créé mais non vérifié (verifie = false)
   ↓
3. Admin vérifie le vendeur via PATCH /api/admin/vendeurs/{id}/verifier
   ↓
4. Vendeur crée des produits
   ↓
5. Produits en statut EN_ATTENTE_VALIDATION
   ↓
6. Admin valide les produits via PATCH /api/admin/produits/{id}/valider
   ↓
7. Produits deviennent ACTIF et visibles aux clients
```

## 📝 Exemples d'Utilisation

### Exemple 1 : Admin Valide un Produit

```bash
# 1. Connexion admin
curl -X POST http://localhost:8080/api/auth/connexion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@afrizar.sn",
    "motDePasse": "Admin@123"
  }'

# Réponse : { "token": "TOKEN_JWT" }

# 2. Voir les produits en attente
curl -X GET http://localhost:8080/api/admin/produits/en-attente \
  -H "Authorization: Bearer TOKEN_JWT"

# 3. Valider un produit
curl -X PATCH http://localhost:8080/api/admin/produits/123/valider \
  -H "Authorization: Bearer TOKEN_JWT"
```

### Exemple 2 : Admin Vérifie un Vendeur

```bash
# 1. Voir les vendeurs non vérifiés
curl -X GET http://localhost:8080/api/admin/vendeurs/non-verifies \
  -H "Authorization: Bearer TOKEN_JWT"

# 2. Vérifier un vendeur
curl -X PATCH http://localhost:8080/api/admin/vendeurs/456/verifier \
  -H "Authorization: Bearer TOKEN_JWT"
```

### Exemple 3 : Client Passe une Commande

```bash
# 1. Inscription client
curl -X POST http://localhost:8080/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Diop",
    "prenom": "Fatou",
    "email": "fatou@example.com",
    "motDePasse": "SecurePass123!",
    "telephone": "+221771234567",
    "role": "CLIENT",
    "adresse": "Dakar, Sénégal",
    "ville": "Dakar",
    "pays": "Sénégal"
  }'

# 2. Connexion
curl -X POST http://localhost:8080/api/auth/connexion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "fatou@example.com",
    "motDePasse": "SecurePass123!"
  }'

# 3. Passer une commande
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer TOKEN_CLIENT" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 2,
    "lignesCommande": [
      {
        "produitId": 1,
        "quantite": 2,
        "taille": "M"
      }
    ]
  }'
```

## 🔄 Flux de Travail Complet

### Pour l'Admin

1. **Connexion** avec les identifiants par défaut
2. **Vérification des vendeurs** en attente
3. **Validation des produits** soumis par les vendeurs
4. **Suivi des statistiques** du système
5. **Gestion des utilisateurs** (activation/désactivation)

### Pour le Vendeur

1. **Inscription** avec rôle VENDEUR
2. **Attente de vérification** par l'admin
3. **Création de produits** une fois vérifié
4. **Gestion du stock** et des commandes
5. **Consultation des statistiques** de vente

### Pour le Client

1. **Inscription** ou navigation anonyme
2. **Navigation dans le catalogue**
3. **Ajout de produits** au panier
4. **Passage de commande**
5. **Suivi de commande** et accumulation de points

### Pour le Support

1. **Connexion** avec identifiants support
2. **Assistance aux clients** (recherche de commandes, utilisateurs)
3. **Résolution de problèmes** (ajout de points, etc.)
4. **Consultation des statistiques** pour analyse

## 🛠️ Configuration

### Modifier la durée du Token JWT

Dans `application.properties` :

```properties
# Durée du token en millisecondes (24h par défaut)
app.jwt.expiration=86400000

# Secret JWT (à changer en production !)
app.jwt.secret=votre_secret_securise_ici
```

### Désactiver la Validation des Produits

Si vous voulez que les produits soient directement actifs sans validation admin, modifiez dans `ProduitServiceImpl.java` :

```java
// Ligne 55
produit.setStatut(Produit.StatutProduit.ACTIF); // Au lieu de EN_ATTENTE_VALIDATION
```

## ❓ FAQ

**Q: Comment créer un compte admin supplémentaire ?**
R: Utilisez le endpoint d'inscription avec l'email d'un admin existant pour créer manuellement un nouvel admin dans la base de données, ou créez un endpoint dédié réservé aux admins.

**Q: Que se passe-t-il si je perds mon mot de passe admin ?**
R: Supprimez tous les comptes admin de la base de données et redémarrez l'application. Le compte par défaut sera recréé.

**Q: Les vendeurs peuvent-ils voir les produits des autres vendeurs ?**
R: Oui, ils peuvent voir mais pas modifier. Utilisez le filtre `vendeurId` pour voir uniquement ses propres produits.

**Q: Comment désactiver un utilisateur malveillant ?**
R: Utilisez `PATCH /api/admin/utilisateurs/{id}/desactiver`. L'utilisateur ne pourra plus se connecter.

## 📚 Ressources Supplémentaires

- [Documentation Swagger](http://localhost:8080/swagger-ui.html) - Documentation interactive des API
- [SECURITY_README.md](./SECURITY_README.md) - Guide de sécurité détaillé
- [EXEMPLES_API_CALLS.md](./EXEMPLES_API_CALLS.md) - Plus d'exemples d'appels API

## 🆘 Support

Pour toute question ou problème :
- Email: support@afrizar.sn
- Documentation API: http://localhost:8080/swagger-ui.html
- GitHub Issues: [Créer une issue](https://github.com/votre-repo/issues)

---

**Dernière mise à jour:** Octobre 2025  
**Version:** 1.0.0

