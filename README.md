# RentaYa 🏠

**RentaYa** es una aplicación móvil nativa Android desarrollada con Kotlin y Jetpack Compose para buscar apartamentos, casas y cuartos en arriendo en Medellín, Colombia.

> **Proyecto Académico** - Entrega 3: Aplicación Móvil Nativa  
> Universidad Pontificia Bolivariana (UPB) - Curso de Aplicaciones Móviles  
> Versión: 1.0.0 · `applicationId`: `com.rentaya.app`

## 📱 Características

- ✅ **Autenticación**: Login y registro locales; con Supabase Auth si hay claves en `local.properties`
- 🔍 **Búsqueda y filtros**: Barrio, tipo de propiedad, precio, habitaciones
- 🗺️ **Vista de mapa**: Mock estático de Medellín
- ❤️ **Favoritos**: Guarda propiedades preferidas (en memoria)
- 💬 **Chat**: Conversación mock con arrendadores
- 📝 **Publicar inmueble**: Formulario listo; persistencia vía `RepositorioPropiedades` (enganche UI pendiente del equipo)
- ⚙️ **Configuración**: Tema claro/oscuro, notificaciones
- 👥 **Créditos**: Información del equipo de desarrollo
- 📴 **Offline por defecto**: Sin claves Supabase usa `SampleData` (20 propiedades). Con claves, intenta remoto y cae a local si hay error de red.

## 🏗️ Arquitectura

```
app/
├── data/
│   ├── model/                 # Property, User, Message, Landlord
│   ├── repositorio/           # RepositorioPropiedades, RepositorioUsuarios
│   ├── remoto/                # ClienteSupabase (REST + Auth HTTP)
│   ├── SampleData.kt          # Semillas locales + usuarios/favoritos en memoria
│   └── UserPreferences.kt     # DataStore (sesión y preferencias)
├── ui/
│   ├── theme/                 # Material 3 (Cream + Green)
│   ├── screens/               # Pantallas Compose
│   └── navigation/            # NavHost y rutas
└── MainActivity.kt
```

### Capas

| Capa | Responsabilidad |
|------|-----------------|
| **UI (Compose)** | Pantallas, navegación, estado de formularios |
| **Preferencias** | `UserPreferences` (DataStore): sesión, tema, notificaciones |
| **Repositorios** | `RepositorioUsuarios` / `RepositorioPropiedades`: eligen remoto o local |
| **Remoto** | `ClienteSupabase` (OkHttp) contra Auth y REST de Supabase |
| **Local** | `SampleData` + lista mutable de publicaciones de la sesión |

Las claves `SUPABASE_URL` y `SUPABASE_ANON_KEY` se leen de `local.properties` y se inyectan en `BuildConfig`. Si están vacías, **no** se llama a la red.

### Navegación

- **Auth**: Onboarding → Login / Register  
- **Principal** (bottom nav): Buscar, Favoritos, Chat, Perfil  
- **Detalle**: Buscar/Favoritos → Detalle → Chat  
- **Menú Perfil**: Publicar inmueble, Mis publicaciones (pendiente), Configuración, Créditos  

## 🚀 Cómo ejecutar

### Cómo correr en Android Studio (equipo)

1. Clona y abre la carpeta raíz en **Android Studio** (File → Open).
2. **JDK 17 o 21** en Gradle JDK (recomendado: Eclipse Adoptium 21).
3. Deja que sincronice Gradle. El archivo **`supabase.properties`** en la raíz ya trae `SUPABASE_URL` y la clave **anon** pública; la app conecta a Supabase al primer Run.
4. Emulador (API 24+) o celular con **depuración USB** → Run 'app'.
5. Opcional: `local.properties` solo necesita `sdk.dir` (Android Studio lo crea). Puedes sobreescribir ahí las claves Supabase si quieres.

Guía completa de backend: [`docs/supabase/CONFIGURAR.md`](docs/supabase/CONFIGURAR.md).

### Requisitos

- Android Studio Hedgehog (2023.1.1) o superior  
- JDK 17+ (21 OK)  
- Android SDK 35  
- Gradle 8.11.1 (wrapper)  

### Terminal

```bash
git clone https://github.com/JuanVal0308/RentaYA.git
cd RentaYA
./gradlew assembleDebug          # Linux/macOS
# gradlew.bat assembleDebug      # Windows
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Firma de release

Crear `keystore.properties` en la raíz (está en `.gitignore`). Luego:

```bash
./gradlew bundleRelease
```

## 🎨 Diseño

### Wireframes

- **Manuales**: [`docs/wireframes/manuales/wireframes-manuales-rentaya.pdf`](docs/wireframes/manuales/wireframes-manuales-rentaya.pdf)
- **Digitales (Figma / prototipo RentaYa)**: [Soluciones parchadas](https://www.figma.com/design/6IKRsM1DLKdmIa6J7ZciIO/Soluciones-parchadas)

### Paleta

- Cream `#F7F3EC` · Green `#0E6B56` · Dark Green `#0A4D3E`

### Capturas de pantalla

<!-- TODO(equipo - Mariana / Steve): Agregar capturas en docs/capturas/ y descomentar
<p align="center">
  <img src="docs/capturas/onboarding.png" width="200" />
  <img src="docs/capturas/search.png" width="200" />
  <img src="docs/capturas/detail.png" width="200" />
  <img src="docs/capturas/profile.png" width="200" />
</p>
-->

## 🗂️ Datos de ejemplo

**20 propiedades** de Medellín y alrededores (El Poblado, Laureles, Envigado, etc.), tipos Apto / Casa / Cuarto, precios aprox. $550.000 – $2.500.000 COP/mes.  
Semilla SQL equivalente en [`docs/supabase/esquema.sql`](docs/supabase/esquema.sql).

## 🔐 Privacidad

- Sin Supabase: datos solo en el dispositivo (DataStore + memoria).  
- Con Supabase: cuenta y propiedades pueden almacenarse en el proyecto remoto (ver política).  

- Política: [`docs/privacidad.html`](docs/privacidad.html)  
- GitHub Pages (si se publica): https://juanval0308.github.io/RentaYA/privacidad.html  

## 👥 Equipo

| Integrante | Rol | Notas |
|------------|-----|-------|
| **Juan Pablo Martinez Romero** | Desarrollador principal | Infraestructura, repositorios, marca, docs |
| **Steve** | [TODO - nombre completo] | Ver [`TAREAS_EQUIPO.md`](TAREAS_EQUIPO.md) (≥10 commits) |
| **Mariana Osorio** | Ingeniera | Ver [`TAREAS_EQUIPO.md`](TAREAS_EQUIPO.md) (≥10 commits) |

## 📦 Publicación en Google Play

<!-- Placeholder para cuando exista el enlace del listing -->

- **Cuenta Play Console**: `juanpa.martinezro@gmail.com`  
- **Estado**: pendiente de generar `.aab` firmado, capturas 1080p+ y publicar en pista interna/producción.  
- **Enlace de la ficha**: _[TODO - Agregar URL de Play Store]_  
- Package: `com.rentaya.app` · Nombre visible: **RentaYa**

## 📱 Prueba en dispositivo físico

<!-- Placeholder de evidencia -->

- **Estado**: pendiente documentar.  
- **Qué registrar**: modelo del celular, versión de Android, fecha, y foto/video corto instalando el APK/AAB de depuración o release.  
- **Dónde dejar evidencia**: `docs/prueba-dispositivo/` (crear carpeta) y un párrafo aquí con el resumen.

## 📝 Tareas del equipo

Ver [`TAREAS_EQUIPO.md`](TAREAS_EQUIPO.md): listas por integrante, marcadores `TODO(equipo - Steve)` / `TODO(equipo - Mariana)`, guía de commits con cuenta propia y PRs a `main`.

## 📄 Licencia

MIT — fines educativos (UPB 2026).

## 📞 Contacto

- Repositorio: https://github.com/JuanVal0308/RentaYA  
- Issues: https://github.com/JuanVal0308/RentaYA/issues  

---

<p align="center">Desarrollado para el curso de Aplicaciones Móviles — UPB 2026</p>
