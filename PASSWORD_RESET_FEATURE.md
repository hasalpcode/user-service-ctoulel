# Fonctionnalité de Réinitialisation du Mot de Passe

## Description
Cette fonctionnalité permet aux utilisateurs de réinitialiser leur mot de passe en cas d'oubli.

## Architecture

### 1. Modèle Utilisateur (User.java)
Nouveaux champs ajoutés :
- `resetToken`: Token unique pour la réinitialisation
- `resetTokenExpiry`: Date d'expiration du token (24 heures par défaut)

### 2. DTOs Créés
- **PasswordResetRequestDTO**: Contient l'email de l'utilisateur
- **PasswordResetDTO**: Contient le token, le nouveau mot de passe et la confirmation
- **PasswordResetResponseDTO**: Réponse avec le statut de succès et un message

### 3. Endpoints API

#### 1. Demander une réinitialisation de mot de passe
```
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "utilisateur@example.com"
}

Réponse (200):
{
  "success": true,
  "message": "Un lien de réinitialisation a été envoyé à votre email",
  "resetToken": "uuid-token-123456"
}
```

#### 2. Réinitialiser le mot de passe
```
POST /auth/reset-password
Content-Type: application/json

{
  "token": "uuid-token-123456",
  "newPassword": "nouveauMotDePasse123",
  "confirmPassword": "nouveauMotDePasse123"
}

Réponse (200):
{
  "success": true,
  "message": "Votre mot de passe a été réinitialisé avec succès"
}
```

## Validations

1. **Email valide**: L'utilisateur doit exister dans la base de données
2. **Token valide**: Le token doit être stocké et ne pas avoir expiré
3. **Correspondance des mots de passe**: Les deux mots de passe doivent être identiques
4. **Longueur minimale**: Le mot de passe doit contenir au moins 6 caractères
5. **Expiration du token**: Le token expire après 24 heures

## Traitement des Erreurs

- **Email introuvable** (404): L'utilisateur n'existe pas
- **Token invalide** (400): Le token n'existe pas ou a expiré
- **Mots de passe non correspondants** (400): Les mots de passe ne correspondent pas
- **Mot de passe trop court** (400): Le mot de passe doit avoir au moins 6 caractères

## Flux d'Utilisation

1. L'utilisateur clique sur "Mot de passe oublié"
2. L'application appelle `POST /auth/forgot-password` avec l'email
3. L'utilisateur reçoit un token (en production, envoyé par email)
4. L'utilisateur soumet le token et le nouveau mot de passe
5. L'application valide et met à jour le mot de passe
6. Le token est nettoyé après utilisation

## Notes de Sécurité

- En production, le token doit être envoyé par email et non retourné directement
- Le token expire après 24 heures
- Le mot de passe est encodé avec PasswordEncoder avant d'être stocké
- Le token est supprimé après une réinitialisation réussie

## Exemple d'implémentation Frontend (JavaScript)

```javascript
// Étape 1: Demander une réinitialisation
async function requestPasswordReset(email) {
  const response = await fetch('/auth/forgot-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  return await response.json();
}

// Étape 2: Réinitialiser le mot de passe
async function resetPassword(token, newPassword, confirmPassword) {
  const response = await fetch('/auth/reset-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ 
      token, 
      newPassword, 
      confirmPassword 
    })
  });
  return await response.json();
}
```

## Base de Données

Les migrations Liquibase/Flyway doivent ajouter les colonnes :
```sql
ALTER TABLE user ADD COLUMN reset_token VARCHAR(255);
ALTER TABLE user ADD COLUMN reset_token_expiry TIMESTAMP;
```
