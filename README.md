# LoginApp (Android + Firebase Auth)

## Setup Firebase
1. Pergi ke [Firebase Console](https://console.firebase.google.com) → **Create Project**.
2. Tambah Android app: package name `com.example.loginapp`.
3. Download `google-services.json`, letak dalam `app/` folder (setaraf dengan `build.gradle` app-level).
4. Dalam Firebase Console → **Authentication** → Sign-in method → enable **Email/Password**.
5. Enable **Firestore Database**.

## Build APK guna GitHub (tak perlu Android Studio / storan)
1. Buat repo baru kat github.com (boleh pilih Private).
2. Upload SEMUA fail dalam folder LoginApp ni ke repo tu (termasuk folder `.github`).
3. **PENTING**: upload `google-services.json` (dari langkah Setup Firebase di atas) sekali, letak dalam folder `app/`.
4. Pergi tab **Actions** dalam repo → workflow "Build APK" akan jalan automatik.
5. Lepas siap (~5 minit), klik build yang siap → download APK dari bahagian **Artifacts**.
6. Transfer APK tu ke phone Android, install (enable "install from unknown sources" kalau diminta).

## Setup (kalau guna Android Studio)
1. Buka projek ni dalam Android Studio (`File > Open` → pilih folder `LoginApp`).
2. Sync Gradle → Run.

## Flow
- `LoginActivity` (launcher) → auto-redirect ke Dashboard kalau session masih aktif.
- `RegisterActivity` → daftar akaun baru (email/password) + simpan nama dalam Firestore.
- `DashboardActivity` → paparan lepas login, ada butang logout.

## Buat akaun admin
Semua akaun daftar sendiri automatik jadi `role: user`. Untuk jadikan seseorang admin:
1. Firebase Console → Firestore Database → collection `users`.
2. Cari dokumen (uid) user tersebut → tukar field `role` daripada `user` ke `admin`.
3. Logout & login semula dalam app — sistem akan auto-redirect ke Admin Dashboard.

Admin Dashboard tunjuk senarai semua user yang daftar (nama + email), update secara realtime.

## Nota
- Ni skeleton asas — belum ada validation lanjut (contoh: forgot password, email verification).
- Kalau nak backend sendiri (bukan Firebase) — contoh guna PHP/Node API + MySQL untuk simpan data driver/lead — boleh saya tukar struktur ni ikut endpoint API awak.
