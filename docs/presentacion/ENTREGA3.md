# Presentación Entrega 3 — RentaYa

**Curso:** Aplicaciones Móviles · UPB · 2026  
**App:** RentaYa (`com.rentaya.app`) · Versión **1.0.2**  
**Repo:** https://github.com/JuanVal0308/RentaYA  

## 1. Arquitectura

- **UI:** Jetpack Compose + Material 3 + Navigation.
- **Preferencias:** DataStore (`UserPreferences`) — sesión, tema, IDs de favoritos.
- **Repositorios:** `RepositorioUsuarios`, `RepositorioPropiedades` (local o remoto).
- **Remoto opcional:** `ClienteSupabase` (OkHttp) → Auth + REST; claves en `supabase.properties`.
- **Local:** `SampleData` (~30 propiedades de Medellín) + publicaciones de la sesión.

```
Pantallas Compose → Repositorios → SampleData / ClienteSupabase
                 ↘ UserPreferences (DataStore)
```

## 2. Vistas principales

Onboarding · Login · Registro · Búsqueda · Resultados · Detalle · Favoritos · Mensajes/Chat · Perfil · Publicar · Mis publicaciones · Configuración · Créditos · Acerca de.

## 3. Funcionalidades

- Auth local y Supabase Auth (si hay claves).
- Búsqueda por barrio/tipo y chip de **precio máximo**.
- Favoritos persistidos en DataStore; contador “X favoritos”.
- Publicar inmueble **persiste** (lista local + INSERT remoto).
- Mis publicaciones, chat mock, tema claro/oscuro.
- Política de privacidad: https://juanval0308.github.io/RentaYA/privacidad.html  

## 4. Diseño

- Wireframes papel: `docs/wireframes/manuales/`
- Figma: https://www.figma.com/design/6IKRsM1DLKdmIa6J7ZciIO/Soluciones-parchadas  

## 5. Publicación Play

- Package: `com.rentaya.app` · Nombre: **RentaYa**
- Cuenta: `juanpa.martinezro@gmail.com`
- AAB: `RentaYa-1.0.2.aab` (pista **prueba interna**)
- Enlace de ficha / testing: _[completar tras liberar en Play Console]_

## 6. Equipo

| Integrante | Rol |
|------------|-----|
| Juan Pablo Martinez Romero | Infraestructura, Supabase, Play, docs |
| Steve (Zteve0) | Propiedades, About, diálogo clave; completar commits restantes |
| Mariana Osorio | Publicar, Mis publicaciones, favoritos DataStore, semillas |

## 7. Demo sugerida (2–3 min)

1. Login / registro  
2. Buscar + chip precio  
3. Detalle + favorito  
4. Publicar → Mis publicaciones  
5. Abrir Figma + enlace privacidad + (si hay) Play internal  
