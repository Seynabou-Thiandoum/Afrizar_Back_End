# 🔌 Exemples d'appels API - Afrizar Backend

## 📋 Collection Postman / Exemples curl

---

## 🔐 1. AUTHENTIFICATION

### 1.1 Test de connexion au backend (PUBLIC)

```bash
curl -X GET http://localhost:8080/api/auth/test
```

**Réponse attendue** :
```json
{
  "message": "✅ Backend Afrizar est accessible !",
  "timestamp": "2025-10-08T10:30:00",
  "version": "1.0.0",
  "status": "OK"
}
```

---

### 1.2 Inscription d'un client

```bash
curl -X POST http://localhost:8080/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Diallo",
    "prenom": "Aminata",
    "email": "aminata.diallo@example.com",
    "motDePasse": "SecurePass123!",
    "telephone": "+221771234567",
    "role": "CLIENT",
    "adresse": "Sacré-Coeur 3, Villa 123",
    "ville": "Dakar",
    "codePostal": "12000",
    "pays": "Sénégal"
  }'
```

**Réponse attendue** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "nom": "Diallo",
  "prenom": "Aminata",
  "email": "aminata.diallo@example.com",
  "role": "CLIENT",
  "pointsFidelite": 0,
  "actif": true
}
```

---

### 1.3 Inscription d'un vendeur

```bash
curl -X POST http://localhost:8080/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Ndiaye",
    "prenom": "Fatou",
    "email": "fatou.couture@example.com",
    "motDePasse": "VendeurPass123!",
    "telephone": "+221775555555",
    "role": "VENDEUR",
    "nomBoutique": "Fatou Couture",
    "description": "Spécialiste en boubous traditionnels",
    "adresseBoutique": "Marché Sandaga, Allée 5",
    "specialites": "Boubous, Caftan, Broderie"
  }'
```

---

### 1.4 Connexion

```bash
curl -X POST http://localhost:8080/api/auth/connexion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "aminata.diallo@example.com",
    "motDePasse": "SecurePass123!"
  }'
```

**Réponse** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "aminata.diallo@example.com",
  "nom": "Diallo",
  "prenom": "Aminata",
  "role": "CLIENT",
  "pointsFidelite": 500
}
```

**⚠️ Important** : Copiez le token pour les requêtes suivantes !

---

### 1.5 Test d'authentification (PROTÉGÉ)

```bash
curl -X GET http://localhost:8080/api/auth/test-auth \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 1.6 Déconnexion

```bash
curl -X POST http://localhost:8080/api/auth/deconnexion \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🛍️ 2. PRODUITS

### 2.1 Lister tous les produits (PUBLIC)

```bash
curl -X GET "http://localhost:8080/api/produits?page=0&size=10&sortBy=dateCreation&sortDir=desc"
```

---

### 2.2 Obtenir un produit par ID (PUBLIC)

```bash
curl -X GET http://localhost:8080/api/produits/1
```

---

### 2.3 Rechercher des produits avec filtres (PUBLIC)

```bash
curl -X GET "http://localhost:8080/api/produits/recherche?nom=boubou&prixMin=10000&prixMax=50000&categorieId=1"
```

---

### 2.4 Créer un produit (VENDEUR/ADMIN)

```bash
curl -X POST http://localhost:8080/api/produits \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Boubou Bazin Riche Brodé",
    "description": "Magnifique boubou en bazin riche avec broderie traditionnelle",
    "prix": 45000,
    "stock": 15,
    "delaiProduction": 0,
    "poids": 1.5,
    "taillesDisponibles": ["M", "L", "XL"],
    "qualite": "PREMIUM",
    "personnalisable": true,
    "optionsPersonnalisation": "Couleur, Broderie, Longueur",
    "statut": "ACTIF",
    "disponibilite": "EN_STOCK",
    "vendeurId": 1,
    "categorieId": 1,
    "photos": [
      "https://example.com/boubou1.jpg",
      "https://example.com/boubou2.jpg"
    ]
  }'
```

---

## 🛒 3. COMMANDES

### 3.1 Calculer les totaux d'une commande (prévisualisation)

```bash
curl -X POST http://localhost:8080/api/commandes/calculer-totaux \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "type": "IMMEDIATE",
    "pointsFideliteUtilises": 500,
    "notes": "Livraison urgente SVP",
    "lignesCommande": [
      {
        "produitId": 1,
        "quantite": 2,
        "taille": "L",
        "personnalisation": "Couleur: Bleu royal",
        "notes": ""
      },
      {
        "produitId": 2,
        "quantite": 1,
        "taille": "M",
        "personnalisation": "",
        "notes": ""
      }
    ],
    "livraison": {
      "type": "STANDARD",
      "adresseLivraison": "Sacré-Coeur 3, Villa 123",
      "ville": "Dakar",
      "codePostal": "12000",
      "pays": "Sénégal",
      "notes": ""
    }
  }'
```

**Réponse attendue** :
```json
{
  "type": "IMMEDIATE",
  "montantHT": 65000,
  "montantCommission": 3900,
  "fraisLivraison": 3000,
  "reduction": 500,
  "montantTotal": 71400,
  "pointsFideliteUtilises": 500,
  "lignesCommande": [
    {
      "produitId": 1,
      "nomProduit": "Boubou Bazin Riche Brodé",
      "quantite": 2,
      "prixUnitaire": 45000,
      "sousTotal": 90000,
      "commission": 5400,
      "taille": "L",
      "personnalisation": "Couleur: Bleu royal"
    },
    {
      "produitId": 2,
      "nomProduit": "Caftan Traditionnel",
      "quantite": 1,
      "prixUnitaire": 25000,
      "sousTotal": 25000,
      "commission": 2000,
      "taille": "M"
    }
  ]
}
```

---

### 3.2 Créer une commande

```bash
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "type": "IMMEDIATE",
    "pointsFideliteUtilises": 0,
    "notes": "Livraison entre 14h et 18h",
    "lignesCommande": [
      {
        "produitId": 1,
        "quantite": 1,
        "taille": "L",
        "personnalisation": "",
        "notes": ""
      }
    ],
    "livraison": {
      "type": "EXPRESS",
      "adresseLivraison": "Sacré-Coeur 3, Villa 123",
      "ville": "Dakar",
      "codePostal": "12000",
      "pays": "Sénégal",
      "notes": "Appeler avant de livrer"
    }
  }'
```

**Réponse** :
```json
{
  "id": 1,
  "numeroCommande": "CMD-1696784523456",
  "dateCreation": "2025-10-08T10:30:00",
  "statut": "EN_ATTENTE",
  "type": "IMMEDIATE",
  "montantHT": 45000,
  "montantCommission": 2700,
  "fraisLivraison": 4500,
  "montantTotal": 52200,
  "reduction": 0,
  "pointsFideliteUtilises": 0,
  "clientId": 1,
  "nomClient": "Diallo",
  "emailClient": "aminata.diallo@example.com",
  "lignesCommande": [...],
  "paiement": {
    "id": 1,
    "montant": 52200,
    "statut": "EN_ATTENTE",
    "methode": "CARTE_CREDIT",
    "numeroTransaction": "PAY-1696784523457",
    "devise": "XOF"
  },
  "livraison": {
    "id": 1,
    "type": "EXPRESS",
    "adresseLivraison": "Sacré-Coeur 3, Villa 123",
    "ville": "Dakar",
    "pays": "Sénégal",
    "cout": 4500,
    "poidsTotal": 1.5,
    "statut": "EN_PREPARATION",
    "numeroSuivi": "AFRIZAR-1696784523458-789",
    "dateLivraisonPrevue": "2025-10-09"
  }
}
```

---

### 3.3 Obtenir une commande par ID

```bash
curl -X GET http://localhost:8080/api/commandes/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3.4 Obtenir les commandes d'un client

```bash
curl -X GET "http://localhost:8080/api/commandes/client/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3.5 Confirmer une commande

```bash
curl -X PATCH http://localhost:8080/api/commandes/1/confirmer \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3.6 Annuler une commande

```bash
curl -X PATCH "http://localhost:8080/api/commandes/1/annuler?motif=Client%20a%20changé%20d'avis" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3.7 Changer le statut d'une commande

```bash
curl -X PATCH "http://localhost:8080/api/commandes/1/statut?statut=EN_PREPARATION" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3.8 Expédier une commande

```bash
curl -X PATCH "http://localhost:8080/api/commandes/1/expedier?numeroSuivi=TRACK123&transporteur=DHL" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 👤 4. CLIENTS

### 4.1 Obtenir un client par ID

```bash
curl -X GET http://localhost:8080/api/clients/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 4.2 Ajouter des points de fidélité

```bash
curl -X PATCH "http://localhost:8080/api/clients/1/points-fidelite/ajouter?points=1000" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 4.3 Utiliser des points de fidélité

```bash
curl -X PATCH "http://localhost:8080/api/clients/1/points-fidelite/utiliser?points=500" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 📊 5. STATISTIQUES

### 5.1 Dashboard général (ADMIN)

```bash
curl -X GET http://localhost:8080/api/statistiques/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 5.2 Statistiques d'un client

```bash
curl -X GET http://localhost:8080/api/statistiques/clients/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 5.3 Chiffre d'affaires total

```bash
curl -X GET http://localhost:8080/api/commandes/statistiques/chiffre-affaires \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🧮 6. CALCULS

### 6.1 Calculer le prix avec commission

```bash
curl -X GET "http://localhost:8080/api/calcul/prix-final?prixVendeur=50000&vendeurId=1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 📦 Scénario complet : Commande de A à Z

### Étape 1 : Inscription d'un client
```bash
curl -X POST http://localhost:8080/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Sarr",
    "prenom": "Moussa",
    "email": "moussa.sarr@example.com",
    "motDePasse": "MoussaPass123!",
    "telephone": "+221776543210",
    "role": "CLIENT",
    "adresse": "Mermoz, Résidence 45",
    "ville": "Dakar",
    "pays": "Sénégal"
  }'
```

**→ Récupérer le token et l'ID client**

---

### Étape 2 : Voir les produits disponibles
```bash
curl -X GET "http://localhost:8080/api/produits?page=0&size=10"
```

---

### Étape 3 : Calculer les totaux avant de commander
```bash
curl -X POST http://localhost:8080/api/commandes/calculer-totaux \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "lignesCommande": [{"produitId": 1, "quantite": 2, "taille": "L"}],
    "livraison": {
      "type": "STANDARD",
      "adresseLivraison": "Mermoz, Résidence 45",
      "ville": "Dakar",
      "pays": "Sénégal"
    }
  }'
```

---

### Étape 4 : Créer la commande
```bash
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "lignesCommande": [{"produitId": 1, "quantite": 2, "taille": "L"}],
    "livraison": {
      "type": "STANDARD",
      "adresseLivraison": "Mermoz, Résidence 45",
      "ville": "Dakar",
      "pays": "Sénégal"
    }
  }'
```

---

### Étape 5 : Confirmer la commande
```bash
curl -X PATCH http://localhost:8080/api/commandes/1/confirmer \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Étape 6 : Suivre la commande
```bash
curl -X GET http://localhost:8080/api/commandes/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔥 Exemples de scénarios d'erreur

### Stock insuffisant
```bash
curl -X POST http://localhost:8080/api/commandes \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "lignesCommande": [{"produitId": 1, "quantite": 999}],
    "livraison": {...}
  }'
```

**→ Erreur 400** : "Stock insuffisant pour le produit..."

---

### Points de fidélité insuffisants
```bash
curl -X POST http://localhost:8080/api/commandes/calculer-totaux \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "pointsFideliteUtilises": 99999,
    "lignesCommande": [...],
    "livraison": {...}
  }'
```

**→ Erreur 400** : "Points de fidélité insuffisants..."

---

## 📱 Import dans Postman

1. Créer une nouvelle collection "Afrizar API"
2. Créer une variable d'environnement `{{baseUrl}}` = `http://localhost:8080`
3. Créer une variable `{{token}}` pour stocker le JWT
4. Copier/coller ces exemples en remplaçant `YOUR_TOKEN_HERE` par `{{token}}`

---

## 🎯 Variables Postman recommandées

```
baseUrl = http://localhost:8080
token = (à remplir après connexion)
clientId = 1
produitId = 1
commandeId = 1
```

---

**🎉 Vous êtes prêt à tester toutes les fonctionnalités de l'API Afrizar !**
