# Tareas del equipo — RentaYa (Entrega 3)

Cada integrante debe aportar **al menos 10 commits propios** con su cuenta de GitHub  
(`user.name` / `user.email` de **esa** persona). No uses co-autoría de herramientas ni bots.

## Estado actual de commits (orientativo)

| Integrante | Commits (aprox.) | Meta |
|------------|------------------|------|
| Juan Pablo Martinez Romero | ≥33 | ✅ |
| Mariana Osorio (MinervaStarfish) | ≥11 | ✅ |
| Steve (Zteve0) | ~5 | ❌ necesita **≥5 commits más** |

---

## Nota sobre ítems cerrados para la entrega

Para dejar la app **completa y publicable**, Juan cerró en código (sin quitar el mérito de los commits previos de cada quien):

- Validación de correo y teléfono en `RegisterScreen`
- Chip / etiqueta de **precio máximo** en `SearchScreen`
- Contador **“X favoritos”** en `FavoritesScreen`
- `docs/supabase/semillas_steve.sql` con las 5 propiedades de Steve
- Capturas base en `docs/capturas/` y presentación en `docs/presentacion/`

Esos TODOs de código ya no deben reabrirse. **Steve sigue necesitando ≥5 commits nuevos** con las tareas de la sección siguiente (no deshacer lo anterior).

---

## Cómo hacer commits con tu propia cuenta

```bash
git clone https://github.com/JuanVal0308/RentaYA.git
cd RentaYA
git checkout -b feature/steve-apellido
git config user.name "Tu Nombre En GitHub"
git config user.email "tu-email-de-github@ejemplo.com"
# ... cambios ...
git add -A
git commit -m "Mensaje en español"
git push -u origin feature/steve-apellido
# Abrir Pull Request hacia main
```

---

## Steve — tareas restantes (≥5 commits, una por ítem)

| # | Tarea | Archivo(s) | Mensaje sugerido |
|---|--------|------------|------------------|
| 1 | Poner tu **apellido** en Créditos (hoy solo dice “Steve”) | `CreditsScreen.kt` | `Agrega el apellido de Steve en créditos` |
| 2 | Completar nombre en la tabla Equipo del README | `README.md` | `Actualiza el nombre completo de Steve en el README` |
| 3 | Agregar **2 propiedades nuevas** en SampleData (barrios distintos, p. ej. La Floresta y Envigado Sur) | `SampleData.kt` + opcional INSERT en `semillas_steve.sql` | `Agrega dos propiedades nuevas de ejemplo` |
| 4 | Chip de filtro **“Mascotas”** en la hoja de filtros de búsqueda (y 1 propiedad con esa amenidad) | `SearchScreen.kt`, `SampleData.kt` | `Agrega filtro y amenidad Mascotas` |
| 5 | Tomar **2 capturas reales** (emulador o celular) Login + Búsqueda y reemplazar o añadir en `docs/capturas/` | `docs/capturas/` | `Agrega capturas reales de Login y Búsqueda` |
| 6 | Completar la tabla de `docs/prueba-dispositivo/README.md` y subir una foto del celular (`foto-dispositivo.jpg`) **o** un párrafo corto en `docs/presentacion/ENTREGA3.md` §6 sobre tu aporte | esos docs | `Documenta prueba en dispositivo / aporte en la presentación` |

Con 5 de estas ya llegas a ≥10 commits totales.

---

## Mariana — opcional

Ya cumples ≥10 commits y las tareas core. Opcional:

- Sustituir alguna captura de `docs/capturas/` por una tomada del emulador/dispositivo.
- Revisar que tu nombre figure bien en la presentación HTML.

---

## Qué no tocar

- No subir `local.properties`, keystores ni `service_role`.
- No borrar trabajo ajeno ni `Co-authored-by` de agentes.
- Package `com.rentaya.rentola` y nombre **RentaYa**.
