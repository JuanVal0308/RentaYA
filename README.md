# RentaYa 🏠

**RentaYa** es una aplicación móvil nativa Android desarrollada con Kotlin y Jetpack Compose para buscar apartamentos, casas y cuartos en arriendo en Medellín, Colombia.

> **Proyecto Académico** - Entrega 3: Aplicación Móvil Nativa  
> Universidad Pontificia Bolivariana (UPB) - Curso de Aplicaciones Móviles  
> Versión: **1.1.0** · `applicationId`: `com.rentaya.rentola` · paquete Kotlin: `com.rentaya.app`

## 📱 Características

- ✅ **Autenticación**: Login y registro con Supabase Auth; verificación real de correo (pantalla “Revisa tu correo” + reenvío)
- 🔍 **Búsqueda y filtros**: Barrio, tipo, chip de **precio máximo**, habitaciones
- 🗺️ **Mapa OSM**: Pines reales por barrio (El Poblado, Laureles, Envigado, Belén, Aranjuez, etc.); toca un pin para ver el detalle
- 📷 **Fotos reales**: Pool JPG de RentaGo en `assets/inmuebles/` (hash estable por id). Al publicar se eligen fotos de galería/cámara y se suben a Storage
- ❤️ **Favoritos**: IDs persistidos en **DataStore** + contador en la barra
- 💬 **Chat**: Conversación mock con arrendadores
- 📝 **Publicar inmueble**: Fotos, pin en el mapa y persistencia vía `RepositorioPropiedades`
- 🏠 **Mis publicaciones**: Lista de lo publicado en la sesión
- ⚙️ **Configuración / Acerca de / Créditos**
- 📴 **Offline**: Sin red o sin claves usa `SampleData` (**~30 propiedades**, fotos empaquetadas + coordenadas por barrio)

## 🏗️ Arquitectura

```
app/
├── data/
│   ├── model/                 # Property, User, Message, Landlord
│   ├── repositorio/           # RepositorioPropiedades, RepositorioUsuarios
│   ├── remoto/                # ClienteSupabase (REST + Auth HTTP)
│   ├── SampleData.kt          # Semillas locales (~30) + favoritos en memoria
│   ├── ImagenesInmuebles.kt   # Hash-por-id (mismo criterio que RentaGo)
│   ├── CoordenadasBarrios.kt  # Pines por barrio del Valle de Aburrá
│   └── UserPreferences.kt     # DataStore (sesión, tema, IDs favoritos)
├── ui/
│   ├── theme/                 # Material 3 (Cream + Green)
│   ├── components/            # Coil + mapa osmdroid
│   ├── screens/               # Pantallas Compose
│   └── navigation/            # NavHost y rutas
└── MainActivity.kt
```

### Capas

| Capa | Responsabilidad |
|------|-----------------|
| **UI (Compose)** | Pantallas, navegación, formularios |
| **Preferencias** | `UserPreferences` (DataStore) |
| **Repositorios** | Eligen remoto o local |
| **Remoto** | `ClienteSupabase` (OkHttp) |
| **Local** | `SampleData` + publicaciones de la sesión |

Las claves `SUPABASE_URL` y `SUPABASE_ANON_KEY` se leen de `supabase.properties` (y opcionalmente `local.properties`) y se inyectan en `BuildConfig`. Si están vacías, **no** se llama a la red.

### Navegación

- **Auth**: Onboarding → Login / Register → Revisa tu correo (si Confirm email está ON)  
- **Principal**: Buscar, Favoritos, Chat, Perfil  
- **Detalle**: Buscar/Favoritos/Mapa → Detalle (galería + mapa) → Chat  
- **Menú Perfil**: Publicar inmueble (fotos + pin), Mis publicaciones, Configuración, Acerca de, Créditos

## 🚀 Cómo ejecutar

1. Clona y abre la carpeta en **Android Studio** (JDK 17 o 21).  
2. Sync Gradle — `supabase.properties` ya trae URL y clave anon.  
3. Emulador o celular con depuración USB → Run 'app'.  

Detalle: [`docs/supabase/CONFIGURAR.md`](docs/supabase/CONFIGURAR.md).

```bash
./gradlew assembleDebug
./gradlew bundleRelease   # requiere keystore.properties local
```

## 🎨 Diseño

- **Manuales**: [`docs/wireframes/manuales/wireframes-manuales-rentaya.pdf`](docs/wireframes/manuales/wireframes-manuales-rentaya.pdf)
- **Figma**: [Soluciones parchadas](https://www.figma.com/design/6IKRsM1DLKdmIa6J7ZciIO/Soluciones-parchadas)
- Paleta: Cream `#F7F3EC` · Green `#0E6B56` · Dark Green `#0A4D3E`

### Capturas

<p align="center">
  <img src="docs/capturas/login.png" width="180" alt="Login" />
  <img src="docs/capturas/busqueda.png" width="180" alt="Búsqueda" />
  <img src="docs/capturas/detalle.png" width="180" alt="Detalle" />
  <img src="docs/capturas/publicar.png" width="180" alt="Publicar" />
</p>

## 🗂️ Datos de ejemplo

**~30 propiedades** (semilla base + Steve y Mariana): El Poblado, Laureles, Aranjuez, Manrique, Robledo, Bello, Itagüí, etc. Cada una tiene foto del pool RentaGo y un pin distinto.  
SQL: [`docs/supabase/esquema.sql`](docs/supabase/esquema.sql), [`semillas_steve.sql`](docs/supabase/semillas_steve.sql), [`semillas_mariana.sql`](docs/supabase/semillas_mariana.sql).  
Migración de coordenadas/fotos/Storage: [`docs/supabase/migracion_fotos_mapa.sql`](docs/supabase/migracion_fotos_mapa.sql).

## 🔐 Privacidad

- Local: DataStore + memoria.  
- Con Supabase: cuenta y propiedades en el proyecto remoto.  

- Archivo: [`docs/privacidad.html`](docs/privacidad.html)  
- **URL pública:** https://juanval0308.github.io/RentaYA/privacidad.html  

## 👥 Equipo

| Integrante | Rol | Notas |
|------------|-----|-------|
| **Juan Pablo Martinez Romero** | Desarrollador principal | Infra, Supabase, Play, docs |
| **Steve** (Zteve0) | Desarrollador | Completo en funcionalidad base; ver commits restantes en `TAREAS_EQUIPO.md` |
| **Mariana Osorio** | Ingeniera | Publicar, Mis publicaciones, favoritos DataStore — **completo** |

## 📦 Publicación en Google Play

- **Cuenta Play Console**: `juanpa.martinezro@gmail.com`  
- **Prueba interna**: https://play.google.com/apps/internaltest/4701566508678775463  
- Package: `com.rentaya.rentola` · Nombre visible: **RentaYa**  
- Versión demo comercial: **1.1.0** (`versionCode` 6)

## 📱 Prueba en dispositivo físico

Checklist y plantilla: [`docs/prueba-dispositivo/README.md`](docs/prueba-dispositivo/README.md)  
(Pendiente: foto del celular del equipo.)

## 📑 Presentación

- Guion: [`docs/presentacion/ENTREGA3.md`](docs/presentacion/ENTREGA3.md)  
- One-pager: [`docs/presentacion/index.html`](docs/presentacion/index.html)

## 📝 Tareas del equipo

Ver [`TAREAS_EQUIPO.md`](TAREAS_EQUIPO.md).

## 📄 Licencia

MIT — fines educativos (UPB 2026).

## 📞 Contacto

- https://github.com/JuanVal0308/RentaYA  

---

<p align="center">Desarrollado para el curso de Aplicaciones Móviles — UPB 2026</p>
