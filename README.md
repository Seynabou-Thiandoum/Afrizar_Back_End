# Afrizar.sn - Backend API

API REST pour la plateforme Afrizar.sn, dédiée à la couture sénégalaise et aux accessoires artisanaux.

## 🎯 Vue d'ensemble

Cette API implémente toutes les fonctionnalités définies dans le cahier des charges d'Afrizar.sn, notamment :

- **Gestion des utilisateurs** : Clients, Vendeurs, Support, Admin
- **Catalogue de produits** : Avec photos multiples, tailles, personnalisation
- **Système de commandes** : Commandes immédiates et différées
- **Calcul automatique** : Commissions dynamiques et frais de livraison
- **Gestion des stocks** : Suivi en temps réel, alertes de rupture
- **Paiements multi-canaux** : Orange Money, Wave, PayPal, cartes bancaires
- **Livraison internationale** : Calculs basés sur poids et destination
- **Programme de fidélité** : Points et réductions automatiques

## 🏗️ Architecture

### Modèles Principaux

#### Utilisateurs
- **`Utilisateur`** : Classe parent (nom, email, téléphone, rôle)
- **`Client`** : Hérite d'Utilisateur (adresse, points fidélité)
- **`Vendeur`** : Hérite d'Utilisateur (boutique, rating, commission personnalisée)

#### Produits et Catalogue
- **`Produit`** : Nom, photos multiples, prix, stock, poids, tailles disponibles
- **`Categorie`** : Organisation hiérarchique avec sous-catégories
- **`Commission`** : Tranches de commission configurables

#### Commandes et Transactions
- **`Commande`** : Commandes avec lignes détaillées et calculs automatiques
- **`LigneCommande`** : Détails par produit (quantité, taille, personnalisation)
- **`Paiement`** : Transactions avec suivi des statuts
- **`Livraison`** : Informations d'expédition et suivi

### Fonctionnalités Métier Implémentées

#### 🧮 Calcul des Commissions (selon cahier des charges)
```
< 10 000 FCFA    → +10%
10 000-30 000 FCFA → +8%
30 000-50 000 FCFA → +6%
> 50 000 FCFA    → +5%
```

#### 🚚 Calcul des Frais de Livraison
- Basé sur le poids des produits
- Tarifs différenciés par destination (Sénégal, Afrique, International)
- Types de livraison : Express, Standard, Économique

#### 📦 Gestion des Commandes Différées
- Produits "Sur commande" avec délai de production
- Estimation automatique des dates de livraison
- Gestion mixte (produits en stock + sur commande)

#### 🎁 Programme de Fidélité
- Attribution automatique de points (1% du montant)
- Utilisation des points comme réduction
- Suivi des niveaux de fidélité

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Installation

1. **Cloner le projet**
```bash
git clone https://github.com/votre-org/afrizar-backend.git
cd afrizar-backend
```

2. **Configuration de la base de données**
```sql
CREATE DATABASE afrizar_db;
CREATE USER afrizar_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE afrizar_db TO afrizar_user;
```

3. **Configuration application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/afrizar_db
spring.datasource.username=afrizar_user
spring.datasource.password=your_password
```

4. **Démarrage de l'application**
```bash
mvn spring-boot:run
```

L'API sera disponible sur : `http://localhost:8080`

### 📚 Documentation API

Une fois l'application démarrée, la documentation Swagger est accessible sur :
- **Interface Swagger UI** : http://localhost:8080/swagger-ui.html
- **Spécification OpenAPI** : http://localhost:8080/api-docs

## 🔗 Endpoints Principaux

### Produits
- `GET /api/produits` - Liste des produits avec filtres
- `POST /api/produits` - Créer un produit
- `GET /api/produits/{id}` - Détails d'un produit
- `GET /api/produits/recherche` - Recherche avancée avec filtres
- `GET /api/produits/mieux-notes` - Produits les mieux notés

### Clients
- `POST /api/clients` - Inscription client
- `GET /api/clients/{id}` - Profil client
- `PATCH /api/clients/{id}/points-fidelite/ajouter` - Ajouter des points

### Commandes
- `POST /api/commandes` - Créer une commande
- `GET /api/commandes/{id}` - Détails d'une commande
- `POST /api/commandes/calculer-totaux` - Prévisualiser les totaux
- `PATCH /api/commandes/{id}/statut` - Changer le statut

### Statistiques
- `GET /api/commandes/statistiques/chiffre-affaires` - CA total
- `GET /api/produits/statistiques` - Stats produits
- `GET /api/clients/statistiques/points-fidelite/moyenne` - Moyennes

## 💾 Structure de la Base de Données

### Tables Principales
- `utilisateur` (table parent avec stratégie JOINED)
- `client` (hérite d'utilisateur)
- `vendeur` (hérite d'utilisateur)
- `produits`
- `categories`
- `commandes`
- `lignes_commande`
- `paiements`
- `livraisons`
- `commissions`

### Relations Clés
- Client → Commandes (1:N)
- Vendeur → Produits (1:N)
- Commande → LignesCommande (1:N)
- Commande → Paiement (1:1)
- Commande → Livraison (1:1)
- Produit → Catégorie (N:1)

## 🧪 Tests

Exécuter les tests :
```bash
mvn test
```

Tests d'intégration avec base H2 en mémoire.

## 🌍 Déploiement

### Variables d'environnement de production
```bash
export DATABASE_URL=postgresql://user:pass@host:5432/db
export SPRING_PROFILES_ACTIVE=production
export SERVER_PORT=8080
```

### Docker (optionnel)
```dockerfile
FROM openjdk:17-jre-slim
COPY target/afrizar-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📈 Monitoring et Logs

L'application génère des logs détaillés :
- **Transactions** : Toutes les opérations métier
- **Performance** : Requêtes SQL et temps de réponse
- **Erreurs** : Stack traces et contexte

## 🔒 Sécurité

Prêt pour l'intégration :
- JWT/OAuth2 pour l'authentification
- Validation des données avec Bean Validation
- Protection CSRF
- HTTPS en production

## 🚦 Roadmap

### Phase 1 ✅ (Actuelle)
- [x] Modèles de données complets
- [x] CRUD pour toutes les entités
- [x] Calculs métier (commissions, livraison)
- [x] API REST documentée

### Phase 2 (Prochaine)
- [ ] Système d'authentification/autorisation
- [ ] Intégrations paiement (Orange Money, Wave, PayPal)
- [ ] Notifications (email, SMS, WhatsApp)
- [ ] Tests de performance

### Phase 3 (Future)
- [ ] Analytics avancées
- [ ] Recommandations IA
- [ ] API mobile optimisée
- [ ] Cache Redis

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📞 Support

- **Email** : dev@afrizar.sn
- **Documentation** : [docs.afrizar.sn](https://docs.afrizar.sn)
- **Issues** : [GitHub Issues](https://github.com/votre-org/afrizar-backend/issues)

## 📄 Licence

Ce projet est sous licence MIT. Voir `LICENSE` pour plus de détails.

---

**Développé avec ❤️ pour promouvoir l'artisanat sénégalais dans le monde**

