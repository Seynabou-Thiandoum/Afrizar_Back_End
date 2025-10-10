# 🚀 Comment Utiliser le Système d'Authentification Afrizar

## 📌 Résumé Rapide

Votre système d'authentification avec différents profils est maintenant **complètement opérationnel** !

## 👤 Les 4 Rôles Disponibles

### 1. ADMIN (Administrateur)
**C'est le patron du système !**
- ✅ Peut valider les produits avant qu'ils soient visibles aux clients
- ✅ Peut vérifier et approuver les vendeurs
- ✅ Peut gérer tous les utilisateurs (activer/désactiver)
- ✅ Accès à toutes les statistiques

### 2. VENDEUR
**Peut vendre sur la plateforme**
- ✅ Crée ses produits
- ✅ Gère son stock
- ⚠️ Doit être vérifié par un admin pour commencer
- ⚠️ Ses produits doivent être validés avant publication

### 3. CLIENT
**Peut acheter sur la plateforme**
- ✅ Consulte les produits
- ✅ Passe des commandes
- ✅ Accumule des points de fidélité

### 4. SUPPORT
**Aide les utilisateurs**
- ✅ Consulte les informations clients/vendeurs
- ✅ Aide à résoudre les problèmes
- ✅ Peut ajouter des points fidélité

## 🎯 Démarrage Rapide

### 1. Démarrer le Backend

```bash
cd Afrizar_Back_End
mvn spring-boot:run
```

Au démarrage, vous verrez dans les logs :
```
✅ Compte admin créé avec succès !
   Email: admin@afrizar.sn
   Mot de passe: Admin@123
```

### 2. Se Connecter en Admin

**URL:** `POST http://localhost:8080/api/auth/connexion`

**Body JSON:**
```json
{
  "email": "admin@afrizar.sn",
  "motDePasse": "Admin@123"
}
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBhZnJpemFyLnNuIiwicm9sZSI6IkFETUlOIiwidXNlcklkIjoxLCJub20iOiJBZG1pbiIsInByZW5vbSI6IkFmcml6YXIiLCJpYXQiOjE2OTg1ODY3MDAsImV4cCI6MTY5ODY3MzEwMH0.abc123...",
  "tokenType": "Bearer",
  "utilisateurId": 1,
  "email": "admin@afrizar.sn",
  "role": "ADMIN"
}
```

### 3. Utiliser le Token

Dans toutes vos requêtes, ajoutez le header :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 📋 Ce Que l'Admin Peut Faire

### Voir les Produits en Attente de Validation
```
GET http://localhost:8080/api/admin/produits/en-attente
Header: Authorization: Bearer VOTRE_TOKEN
```

### Valider un Produit
```
PATCH http://localhost:8080/api/admin/produits/123/valider
Header: Authorization: Bearer VOTRE_TOKEN
```

Le produit devient alors visible pour les clients !

### Voir les Vendeurs Non Vérifiés
```
GET http://localhost:8080/api/admin/vendeurs/non-verifies
Header: Authorization: Bearer VOTRE_TOKEN
```

### Vérifier un Vendeur
```
PATCH http://localhost:8080/api/admin/vendeurs/456/verifier
Header: Authorization: Bearer VOTRE_TOKEN
```

Le vendeur peut alors commencer à vendre !

### Voir les Statistiques
```
GET http://localhost:8080/api/admin/statistiques/dashboard
Header: Authorization: Bearer VOTRE_TOKEN
```

Retourne :
- Nombre total de produits, actifs, en attente
- Nombre de vendeurs vérifiés/non vérifiés
- Nombre de clients
- Nombre d'utilisateurs actifs

## 🔄 Workflow Complet

### Pour un Vendeur

1. **S'inscrire** (rôle VENDEUR)
2. **Attendre** que l'admin le vérifie
3. Une fois vérifié, **créer des produits**
4. Les produits sont en attente
5. **L'admin valide** les produits
6. Les produits deviennent visibles aux clients

### Pour un Client

1. **S'inscrire** (rôle CLIENT) ou naviguer anonymement
2. **Voir les produits** actifs
3. **Passer une commande**
4. **Suivre sa commande**

### Pour l'Admin

1. **Se connecter** avec le compte par défaut
2. **Vérifier les nouveaux vendeurs**
3. **Valider les produits** soumis
4. **Consulter les statistiques**
5. **Gérer les utilisateurs** si besoin

## 🛠️ Test avec Postman/Thunder Client

### Collection de Tests

**1. Connexion Admin**
```
POST http://localhost:8080/api/auth/connexion
Body:
{
  "email": "admin@afrizar.sn",
  "motDePasse": "Admin@123"
}
```

**2. Inscription Client**
```
POST http://localhost:8080/api/auth/inscription
Body:
{
  "nom": "Diop",
  "prenom": "Fatou",
  "email": "fatou@example.com",
  "motDePasse": "Secure123!",
  "telephone": "+221771234567",
  "role": "CLIENT",
  "adresse": "Dakar",
  "ville": "Dakar",
  "pays": "Sénégal"
}
```

**3. Inscription Vendeur**
```
POST http://localhost:8080/api/auth/inscription
Body:
{
  "nom": "Ndiaye",
  "prenom": "Moussa",
  "email": "moussa@example.com",
  "motDePasse": "Secure123!",
  "telephone": "+221771234568",
  "role": "VENDEUR",
  "nomBoutique": "Boutique Moussa",
  "description": "Vêtements traditionnels",
  "adresseBoutique": "Marché Sandaga",
  "specialites": "Boubous, Bijoux"
}
```

**4. Voir Produits en Attente (Admin)**
```
GET http://localhost:8080/api/admin/produits/en-attente
Headers:
  Authorization: Bearer VOTRE_TOKEN_ADMIN
```

**5. Valider un Produit (Admin)**
```
PATCH http://localhost:8080/api/admin/produits/1/valider
Headers:
  Authorization: Bearer VOTRE_TOKEN_ADMIN
```

## 📱 Intégration Frontend

### Exemple React/TypeScript

```typescript
// Login
const login = async (email: string, password: string) => {
  const response = await fetch('http://localhost:8080/api/auth/connexion', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, motDePasse: password })
  });
  
  const data = await response.json();
  
  // Stocker le token
  localStorage.setItem('token', data.token);
  localStorage.setItem('role', data.role);
  
  return data;
};

// Requête authentifiée
const fetchWithAuth = async (url: string) => {
  const token = localStorage.getItem('token');
  
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return response.json();
};

// Vérifier le rôle
const isAdmin = () => localStorage.getItem('role') === 'ADMIN';
```

## 📖 Documentation Complète

Pour plus de détails, consultez :
- **`GUIDE_AUTHENTIFICATION_ROLES.md`** - Guide complet avec tous les endpoints
- **`AUTHENTIFICATION_IMPLEMENTATION_COMPLETE.md`** - Détails techniques de l'implémentation
- **Swagger UI** : http://localhost:8080/swagger-ui.html (documentation interactive)

## ⚠️ Important

1. **Changez le mot de passe admin** après la première connexion
2. **Ne commitez jamais** les tokens dans le code
3. **En production**, changez le secret JWT dans `application.properties`
4. **Activez HTTPS** en production

## 🆘 Problèmes Courants

### "Token invalide" ou "401 Unauthorized"
- Vérifiez que le token est bien dans le header `Authorization: Bearer TOKEN`
- Vérifiez que le token n'a pas expiré (24h par défaut)
- Reconnectez-vous pour obtenir un nouveau token

### "403 Forbidden"
- Votre rôle n'a pas accès à cet endpoint
- Vérifiez que vous utilisez le bon compte (admin pour /api/admin/...)

### Le compte admin n'existe pas
- Redémarrez l'application
- Le compte sera créé automatiquement au démarrage

## 🎉 C'est Tout !

Votre système est prêt à l'emploi. L'admin peut maintenant :
- ✅ Se connecter
- ✅ Vérifier les vendeurs
- ✅ Valider les produits
- ✅ Gérer les utilisateurs
- ✅ Consulter les statistiques

Bon développement ! 🚀

---

**Support:** dev@afrizar.sn  
**Documentation API:** http://localhost:8080/swagger-ui.html

