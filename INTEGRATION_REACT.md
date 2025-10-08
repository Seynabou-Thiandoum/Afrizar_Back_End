# 🚀 Guide d'intégration React - Afrizar Backend

## 📋 Table des matières
1. [Configuration initiale](#configuration-initiale)
2. [Authentification](#authentification)
3. [Gestion des commandes](#gestion-des-commandes)
4. [Exemples de code](#exemples-de-code)
5. [Endpoints disponibles](#endpoints-disponibles)

---

## ⚙️ Configuration initiale

### 1. Variables d'environnement

Créez un fichier `.env` à la racine de votre projet React :

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

### 2. Configuration Axios

Créez un fichier `src/services/api.js` :

```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// Instance Axios configurée
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important pour les cookies CORS
});

// Intercepteur pour ajouter le token JWT automatiquement
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 🔐 Authentification

### 1. Service d'authentification

Créez `src/services/authService.js` :

```javascript
import api from './api';

const authService = {
  // Test de connexion au backend
  async test() {
    try {
      const response = await api.get('/auth/test');
      return response.data;
    } catch (error) {
      console.error('Erreur de connexion au backend:', error);
      throw error;
    }
  },

  // Inscription
  async inscrire(userData) {
    try {
      const response = await api.post('/auth/inscription', userData);
      const { token, ...user } = response.data;
      
      // Sauvegarder le token et l'utilisateur
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      
      return response.data;
    } catch (error) {
      console.error('Erreur lors de l\'inscription:', error);
      throw error;
    }
  },

  // Connexion
  async connexion(email, motDePasse) {
    try {
      const response = await api.post('/auth/connexion', {
        email,
        motDePasse
      });
      
      const { token, ...user } = response.data;
      
      // Sauvegarder le token et l'utilisateur
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la connexion:', error);
      throw error;
    }
  },

  // Déconnexion
  async deconnexion() {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await api.post('/auth/deconnexion', {}, {
          headers: { Authorization: `Bearer ${token}` }
        });
      }
    } catch (error) {
      console.error('Erreur lors de la déconnexion:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  },

  // Obtenir le profil de l'utilisateur connecté
  async getProfil() {
    try {
      const response = await api.get('/auth/profil');
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la récupération du profil:', error);
      throw error;
    }
  },

  // Valider le token
  async validerToken() {
    try {
      const response = await api.get('/auth/valider-token');
      return response.data.valide;
    } catch (error) {
      return false;
    }
  },

  // Test d'authentification
  async testAuth() {
    try {
      const response = await api.get('/auth/test-auth');
      return response.data;
    } catch (error) {
      console.error('Test d\'authentification échoué:', error);
      throw error;
    }
  },

  // Obtenir l'utilisateur depuis le localStorage
  getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  // Vérifier si l'utilisateur est connecté
  isAuthenticated() {
    return !!localStorage.getItem('token');
  }
};

export default authService;
```

### 2. Exemple de composant de connexion

```javascript
import React, { useState } from 'react';
import authService from '../services/authService';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.connexion(email, password);
      console.log('Connexion réussie:', response);
      
      // Rediriger vers le dashboard
      window.location.href = '/dashboard';
    } catch (err) {
      setError(err.response?.data?.erreur || 'Erreur de connexion');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Connexion</h2>
      {error && <div className="error">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Mot de passe"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Connexion...' : 'Se connecter'}
        </button>
      </form>
    </div>
  );
}

export default Login;
```

### 3. Exemple d'inscription

```javascript
import React, { useState } from 'react';
import authService from '../services/authService';

function Register() {
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    telephone: '',
    role: 'CLIENT',
    adresse: '',
    ville: '',
    pays: 'Sénégal'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.inscrire(formData);
      console.log('Inscription réussie:', response);
      
      // Rediriger vers le dashboard
      window.location.href = '/dashboard';
    } catch (err) {
      const errorMessage = err.response?.data?.erreur || 'Erreur lors de l\'inscription';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Inscription</h2>
      {error && <div className="error">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="nom"
          placeholder="Nom"
          value={formData.nom}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="prenom"
          placeholder="Prénom"
          value={formData.prenom}
          onChange={handleChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
        />
        <input
          type="password"
          name="motDePasse"
          placeholder="Mot de passe"
          value={formData.motDePasse}
          onChange={handleChange}
          required
        />
        <input
          type="tel"
          name="telephone"
          placeholder="Téléphone"
          value={formData.telephone}
          onChange={handleChange}
        />
        <input
          type="text"
          name="adresse"
          placeholder="Adresse"
          value={formData.adresse}
          onChange={handleChange}
        />
        <input
          type="text"
          name="ville"
          placeholder="Ville"
          value={formData.ville}
          onChange={handleChange}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Inscription...' : 'S\'inscrire'}
        </button>
      </form>
    </div>
  );
}

export default Register;
```

---

## 🛒 Gestion des commandes

### Service de commandes

Créez `src/services/commandeService.js` :

```javascript
import api from './api';

const commandeService = {
  // Calculer les totaux d'une commande (prévisualisation)
  async calculerTotaux(commandeData) {
    try {
      const response = await api.post('/commandes/calculer-totaux', commandeData);
      return response.data;
    } catch (error) {
      console.error('Erreur lors du calcul des totaux:', error);
      throw error;
    }
  },

  // Créer une commande
  async creerCommande(commandeData) {
    try {
      const response = await api.post('/commandes', commandeData);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la création de la commande:', error);
      throw error;
    }
  },

  // Obtenir une commande par ID
  async getCommande(id) {
    try {
      const response = await api.get(`/commandes/${id}`);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la récupération de la commande:', error);
      throw error;
    }
  },

  // Obtenir les commandes d'un client
  async getCommandesClient(clientId, page = 0, size = 10) {
    try {
      const response = await api.get(`/commandes/client/${clientId}`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la récupération des commandes:', error);
      throw error;
    }
  },

  // Confirmer une commande
  async confirmerCommande(id) {
    try {
      const response = await api.patch(`/commandes/${id}/confirmer`);
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la confirmation de la commande:', error);
      throw error;
    }
  },

  // Annuler une commande
  async annulerCommande(id, motif) {
    try {
      const response = await api.patch(`/commandes/${id}/annuler`, null, {
        params: { motif }
      });
      return response.data;
    } catch (error) {
      console.error('Erreur lors de l\'annulation de la commande:', error);
      throw error;
    }
  }
};

export default commandeService;
```

### Exemple de création de commande

```javascript
import React, { useState } from 'react';
import commandeService from '../services/commandeService';

function CreerCommande({ clientId, panier }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [totaux, setTotaux] = useState(null);

  // Calculer les totaux avant de créer la commande
  const calculerTotaux = async () => {
    setLoading(true);
    setError('');

    try {
      const commandeData = {
        clientId: clientId,
        lignesCommande: panier.map(item => ({
          produitId: item.produitId,
          quantite: item.quantite,
          taille: item.taille,
          personnalisation: item.personnalisation
        })),
        livraison: {
          type: 'STANDARD',
          adresseLivraison: '123 Rue Example',
          ville: 'Dakar',
          pays: 'Sénégal'
        },
        pointsFideliteUtilises: 0
      };

      const response = await commandeService.calculerTotaux(commandeData);
      setTotaux(response);
    } catch (err) {
      setError('Erreur lors du calcul des totaux');
    } finally {
      setLoading(false);
    }
  };

  // Créer la commande
  const creerCommande = async () => {
    setLoading(true);
    setError('');

    try {
      const commandeData = {
        clientId: clientId,
        lignesCommande: panier.map(item => ({
          produitId: item.produitId,
          quantite: item.quantite,
          taille: item.taille,
          personnalisation: item.personnalisation
        })),
        livraison: {
          type: 'STANDARD',
          adresseLivraison: '123 Rue Example',
          ville: 'Dakar',
          pays: 'Sénégal'
        },
        pointsFideliteUtilises: 0
      };

      const response = await commandeService.creerCommande(commandeData);
      alert('Commande créée avec succès!');
      console.log('Commande:', response);
    } catch (err) {
      setError('Erreur lors de la création de la commande');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Créer une commande</h2>
      {error && <div className="error">{error}</div>}

      <button onClick={calculerTotaux} disabled={loading}>
        Calculer les totaux
      </button>

      {totaux && (
        <div className="totaux">
          <p>Montant HT: {totaux.montantHT} FCFA</p>
          <p>Commission: {totaux.montantCommission} FCFA</p>
          <p>Frais de livraison: {totaux.fraisLivraison} FCFA</p>
          <p><strong>Total: {totaux.montantTotal} FCFA</strong></p>
        </div>
      )}

      <button onClick={creerCommande} disabled={loading || !totaux}>
        {loading ? 'Création...' : 'Créer la commande'}
      </button>
    </div>
  );
}

export default CreerCommande;
```

---

## 📡 Endpoints disponibles

### Authentification (`/api/auth`)
- `POST /auth/inscription` - Inscription
- `POST /auth/connexion` - Connexion
- `POST /auth/deconnexion` - Déconnexion
- `GET /auth/profil` - Profil utilisateur
- `GET /auth/valider-token` - Valider le token
- `GET /auth/test` - Test de connexion (public)
- `GET /auth/test-auth` - Test d'authentification (protégé)
- `GET /auth/health` - Health check

### Produits (`/api/produits`)
- `GET /api/produits` - Liste des produits (public)
- `GET /api/produits/{id}` - Détails d'un produit (public)
- `GET /api/produits/recherche` - Recherche avec filtres (public)
- `POST /api/produits` - Créer un produit (VENDEUR/ADMIN)

### Commandes (`/api/commandes`)
- `POST /api/commandes` - Créer une commande (CLIENT/ADMIN)
- `POST /api/commandes/calculer-totaux` - Calculer les totaux (CLIENT/ADMIN)
- `GET /api/commandes/{id}` - Détails d'une commande (CLIENT/ADMIN)
- `GET /api/commandes/client/{clientId}` - Commandes d'un client (CLIENT/ADMIN)
- `PATCH /api/commandes/{id}/confirmer` - Confirmer une commande
- `PATCH /api/commandes/{id}/annuler` - Annuler une commande

### Clients (`/api/clients`)
- `POST /api/clients` - Créer un client (CLIENT/ADMIN)
- `GET /api/clients/{id}` - Détails d'un client (CLIENT/ADMIN)
- `PUT /api/clients/{id}` - Mettre à jour un client
- `PATCH /api/clients/{id}/points-fidelite/ajouter` - Ajouter des points

---

## 🧪 Tests

### Test de connexion au backend

```javascript
import authService from './services/authService';

async function testBackend() {
  try {
    const response = await authService.test();
    console.log('✅ Backend accessible:', response);
  } catch (error) {
    console.error('❌ Backend non accessible:', error);
  }
}

testBackend();
```

### Test d'authentification

```javascript
import authService from './services/authService';

async function testAuthentication() {
  try {
    // 1. Test de connexion
    const loginResponse = await authService.connexion(
      'test@example.com',
      'TestPassword123!'
    );
    console.log('✅ Connexion réussie:', loginResponse);

    // 2. Test d'authentification
    const authTest = await authService.testAuth();
    console.log('✅ Authentification validée:', authTest);

    // 3. Test du profil
    const profil = await authService.getProfil();
    console.log('✅ Profil récupéré:', profil);

  } catch (error) {
    console.error('❌ Erreur d\'authentification:', error);
  }
}

testAuthentication();
```

---

## 🔒 Gestion des erreurs

```javascript
// Exemple de gestion d'erreurs dans un composant
import React, { useState, useEffect } from 'react';
import authService from '../services/authService';

function Dashboard() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        // Vérifier l'authentification
        const isAuth = await authService.validerToken();
        if (!isAuth) {
          window.location.href = '/login';
          return;
        }

        // Charger les données utilisateur
        const userProfile = await authService.getProfil();
        setUser(userProfile);
      } catch (err) {
        setError('Erreur lors du chargement des données');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  if (loading) return <div>Chargement...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <h1>Bienvenue {user?.email}</h1>
      {/* Contenu du dashboard */}
    </div>
  );
}

export default Dashboard;
```

---

## 🚀 Démarrage rapide

1. **Démarrer le backend** :
   ```bash
   cd Afrizar_Back_End
   mvn spring-boot:run
   ```

2. **Tester la connexion** :
   ```bash
   curl http://localhost:8080/api/auth/test
   ```

3. **Dans votre projet React** :
   ```bash
   npm install axios
   # Copier les fichiers de service
   # Configurer les variables d'environnement
   npm start
   ```

---

## 📞 Support

En cas de problème :
1. Vérifiez que le backend est démarré (port 8080)
2. Vérifiez la configuration CORS
3. Vérifiez les logs du backend
4. Testez avec Postman ou curl

---

**🎉 Voilà ! Votre frontend React est maintenant prêt à communiquer avec le backend Afrizar !**
