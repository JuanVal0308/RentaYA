# Renta Ya 🏠

<p align="center">
  <img src="docs/wireframes/manuales/wireframes-manuales-rentaya.pdf" alt="Renta Ya Logo" width="120"/>
</p>

**Renta Ya** es una aplicación móvil nativa Android desarrollada con Kotlin y Jetpack Compose para buscar apartamentos, casas y cuartos en arriendo en Medellín, Colombia.

> **Proyecto Académico** - Entrega 3: Aplicación Móvil Nativa  
> Universidad Pontificia Bolivariana (UPB) - Curso de Aplicaciones Móviles  
> Versión: 1.0.0

## 📱 Características

- ✅ **Autenticación local**: Login y registro sin backend (DataStore)
- 🔍 **Búsqueda y filtros**: Busca por barrio, tipo de propiedad, precio, habitaciones
- 🗺️ **Vista de mapa**: Visualización de propiedades en mapa (mockup estático)
- ❤️ **Favoritos**: Guarda tus propiedades preferidas
- 💬 **Chat**: Conversa con arrendadores
- 📝 **Publicar inmueble**: Formulario para arrendadores
- ⚙️ **Configuración**: Tema claro/oscuro, notificaciones
- 👥 **Créditos**: Información del equipo de desarrollo
- 📴 **Funciona offline**: Toda la data es local (20 propiedades de ejemplo)

## 🏗️ Arquitectura

```
app/
├── data/
│   ├── model/          # Modelos de datos (Property, User, Message)
│   ├── SampleData.kt   # Datos de ejemplo (20 propiedades de Medellín)
│   └── UserPreferences.kt  # DataStore para persistencia
├── ui/
│   ├── theme/          # Colores, tipografía, tema Material 3
│   ├── screens/        # 13 pantallas Composable
│   └── navigation/     # NavHost y rutas
└── MainActivity.kt
```

### Paquetes principales

- **data.model**: `Property`, `User`, `Message`, `ChatMessage`, `Landlord`
- **data**: `SampleData` (20 propiedades), `UserPreferences` (DataStore)
- **ui.screens**: `OnboardingScreen`, `LoginScreen`, `RegisterScreen`, `SearchScreen`, `ResultsScreen`, `DetailScreen`, `FavoritesScreen`, `MessagesScreen`, `ChatScreen`, `ProfileScreen`, `SettingsScreen`, `CreditsScreen`, `PublishPropertyScreen`
- **ui.theme**: Palette Cream (`#F7F3EC`) + Green (`#0E6B56`)

### Navegación

- **Auth flow**: Onboarding → Login / Register
- **Main flow** (Bottom Nav): Buscar, Favoritos, Chat, Perfil
- **Detalle**: Desde Buscar/Favoritos → Detalle → Chat
- **Menú**: Perfil → Publicar inmueble, Configuración, Créditos

## 🚀 Cómo ejecutar

### Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK 35 (API 35)
- Gradle 8.11.1 (incluido via wrapper)

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JuanVal0308/RentaYA.git
   cd RentaYA
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta raíz del proyecto
   - Android Studio sincronizará Gradle automáticamente

3. **Ejecutar en emulador o dispositivo**
   - Crear un AVD (Android Virtual Device) con API 24+ desde Device Manager
   - Run → Run 'app' (Shift+F10)

4. **Compilar desde la terminal**
   ```bash
   # Debug APK
   ./gradlew assembleDebug
   # Output: app/build/outputs/apk/debug/app-debug.apk

   # Release APK (sin firma)
   ./gradlew assembleRelease
   # Output: app/build/outputs/apk/release/app-release-unsigned.apk

   # Release AAB (para Play Store)
   ./gradlew bundleRelease
   # Output: app/build/outputs/bundle/release/app-release.aab
   ```

### Firma de release (opcional)

Para firmar el APK/AAB de release, crear un archivo `keystore.properties` en la raíz del proyecto (NO incluido en git):

```properties
storeFile=ruta/a/tu/keystore.jks
storePassword=tuPassword
keyAlias=tuAlias
keyPassword=tuKeyPassword
```

Luego ejecutar:
```bash
./gradlew bundleRelease  # Genera app-release.aab firmado
```

⚠️ **Importante**: Los archivos `keystore.jks` y `keystore.properties` están en `.gitignore` y NUNCA deben subirse al repositorio.

## 🎨 Diseño

### Wireframes

- **Manuales**: [`docs/wireframes/manuales/wireframes-manuales-rentaya.pdf`](docs/wireframes/manuales/wireframes-manuales-rentaya.pdf)
- **Digitales**: [TODO - Agregar enlace Figma]

### Paleta de colores

- **Cream**: `#F7F3EC` (Fondo principal)
- **Green**: `#0E6B56` (Primary)
- **Dark Green**: `#0A4D3E` (Secondary)

### Capturas de pantalla

<!-- TODO(equipo): Agregar capturas en docs/capturas/ y descomentar esta sección
<p align="center">
  <img src="docs/capturas/onboarding.png" width="200" />
  <img src="docs/capturas/search.png" width="200" />
  <img src="docs/capturas/detail.png" width="200" />
  <img src="docs/capturas/profile.png" width="200" />
</p>
-->

## 🗂️ Datos de ejemplo

La app incluye **20 propiedades** de diferentes barrios de Medellín:
- El Poblado, Laureles, Envigado, Sabaneta, Belén, Buenos Aires, Estadio, etc.
- Tipos: Apartamentos, Casas, Cuartos
- Precios: $550.000 - $2.500.000 COP/mes
- Amenidades: Parqueadero, Amoblado, Gimnasio

## 🔐 Privacidad

Toda la información se almacena **localmente** en el dispositivo. No hay backend, no se envían datos a servidores externos.

- Ver política de privacidad: [`docs/privacidad.html`](docs/privacidad.html)
- GitHub Pages: https://juanval0308.github.io/RentaYA/privacidad.html (cuando se publique)

## 👥 Equipo

- **Juan Pablo Martinez Romero** - Desarrollador principal
- **Integrante 2** - [TODO - Agregar nombre]
- **Integrante 3** - [TODO - Agregar nombre]

## 📝 Tareas pendientes para el equipo

Ver [`TAREAS_EQUIPO.md`](TAREAS_EQUIPO.md) para tareas disponibles y guía de contribución.

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT para fines educativos.

## 📞 Contacto

- **Repositorio**: https://github.com/JuanVal0308/RentaYA
- **Issues**: https://github.com/JuanVal0308/RentaYA/issues

---

<p align="center">
  Desarrollado con ❤️ para el curso de Aplicaciones Móviles - UPB 2026
</p>
