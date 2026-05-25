# Lab — Application de Localisation Android + PHP + Google Maps

> Application mobile de tracking GPS : l'émulateur Android envoie sa position vers un serveur PHP qui stocke en MySQL, et une activité Google Maps affiche tous les points enregistrés.

---

## 🗄️ Partie 1 — Base de données MySQL

### Étape 1.1 — Création de la base
Créer une base nommée `localisation` depuis phpMyAdmin.

### Étape 1.2 — Création de la table `position`
```sql
CREATE TABLE `position` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `date` datetime NOT NULL,
  `imei` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
✅ `SELECT * FROM position;` retourne vide → base prête.

---

## 🐘 Partie 2 — Backend PHP

### Arborescence
```
localisation/
  model/GeoPoint.php          ← classe modèle
  db/DatabaseHelper.php       ← connexion PDO
  contract/ICrud.php          ← interface CRUD
  repository/GeoPointRepository.php  ← INSERT + SELECT
  saveLocation.php            ← API insertion (Android → serveur)
  getLocations.php            ← API lecture (serveur → carte)
```

### Étape 2.1 — `model/GeoPoint.php`
Classe représentant un point GPS (id, lat, lng, date, deviceId) avec getters/setters.

### Étape 2.2 — `db/DatabaseHelper.php`
Connexion PDO avec `ERRMODE_EXCEPTION` pour capturer les erreurs SQL.

### Étape 2.3 — `contract/ICrud.php`
Interface imposant : `insert`, `modify`, `remove`, `findById`, `fetchAll`.

### Étape 2.4 — `repository/GeoPointRepository.php`
- `insert(GeoPoint)` : requête préparée avec placeholders nommés (`:lat`, `:lng`, etc.)
- `fetchAll()` : `SELECT * FROM position ORDER BY date DESC`

### Étape 2.5 — `saveLocation.php`
- Reçoit POST : `latitude`, `longitude`, `date`, `deviceId`
- Insère via `GeoPointRepository`
- Répond JSON : `{"ok": true, "id": X, "ip": "..."}`

### Étape 2.6 — `getLocations.php`
- Requête POST
- Retourne JSON : `{"ok": true, "locations": [...]}`

✅ **Test Postman :**

![Postman saveLocation](captures/capture_postman_saveLocation.png)

![Postman getLocations](captures/capture_postman_getLocations.png)

---

## 📱 Partie 3 — Android (GPS + Volley)

### Étape 3.1 — Projet Android Studio
Nom du projet : `TrackingApp` — Empty Activity.

### Étape 3.2 — Permissions `AndroidManifest.xml`
- `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` : GPS
- `INTERNET` : appels réseau Volley
- `READ_PHONE_STATE` : identifiant appareil
- `usesCleartextTraffic="true"` : autorise HTTP simple (labo)

### Étape 3.3 — Dépendance Volley
```gradle
implementation 'com.android.volley:volley:1.2.1'
```

### Étape 3.4 — `MainActivity.java`
- Demande permission GPS au démarrage (runtime)
- `LocationManager.requestLocationUpdates(GPS_PROVIDER, 60000, 150, listener)`
- `onLocationChanged` → affiche lat/lon + envoie POST via Volley
- `getParams()` : construit `latitude`, `longitude`, `date` (format MySQL), `deviceId`

### Étape 3.5 — Résultat sur l'émulateur

![Android GPS](captures/capture_android_gps.png)

✅ Toast visible avec lat/lon | `tvStatus` affiche "Envoyé ✓"

---

## 🗺️ Partie 4 — Google Maps Activity

### Étape 4.1 — Création de `MapActivity`
Android Studio → New → Google Maps Activity.
Clé API configurée dans `google_maps_api.xml`.

### Étape 4.2 — `MapActivity.java`
- `Volley POST` vers `getLocations.php`
- Parse `JSONArray "locations"`
- `gMap.addMarker(...)` avec titre (deviceId) et snippet (date)
- `CameraUpdateFactory.newLatLngZoom(...)` sur le dernier point

---

## 🗃️ Données en base (phpMyAdmin)

![phpMyAdmin table position](captures/capture_phpmyadmin_position.png)

---

## ✅ Checkpoints de validation

| Étape | Résultat |
|---|---|
| Base MySQL | Table `position` créée, 5 entrées de démo |
| `saveLocation.php` | Postman → `{"ok":true,"id":6,"ip":"..."}` |
| `getLocations.php` | Postman → `{"ok":true,"locations":[...]}` |
| Android GPS | Lat/Lon affichés + Toast avec précision |
| Volley POST | `tvStatus` = "Envoyé ✓" après localisation |
| Google Maps | Markers placés sur la carte avec date et deviceId |

---

*Lab réalisé sur émulateur Android API 33 — serveur XAMPP local (127.0.0.1 / 192.168.1.10)*
