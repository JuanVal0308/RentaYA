# Tareas del equipo — RentaYa (Entrega 3)

Cada integrante debe aportar **al menos 10 commits propios** con su cuenta de GitHub  
(`user.name` / `user.email` de **esa** persona). No uses co-autoría de herramientas ni bots.

Los marcadores en código son:

- `TODO(equipo - Steve)`
- `TODO(equipo - Mariana)`

La infraestructura (marca RentaYa, repositorios, ClienteSupabase, esquema SQL, README/Figma)  
ya la dejó Juan. **No cierres las tareas deliberadas de los otros** si no eres esa persona.

---

## Cómo hacer commits con tu propia cuenta

1. Clona el repo y crea una rama:
   ```bash
   git clone https://github.com/JuanVal0308/RentaYA.git
   cd RentaYA
   git checkout -b feature/steve-creditos   # o feature/mariana-...
   ```
2. Configura **solo en este repo** (o en tu máquina) tu identidad de GitHub:
   ```bash
   git config user.name "Tu Nombre En GitHub"
   git config user.email "tu-email-de-github@ejemplo.com"
   ```
3. Un cambio lógico = un commit. Mensajes **en español**, por ejemplo:
   ```bash
   git add -A
   git commit -m "Agrega nombre de Steve en la pantalla de créditos"
   ```
4. Sube la rama y abre un Pull Request hacia `main`:
   ```bash
   git push -u origin feature/steve-creditos
   ```
5. Pide revisión a Juan. Tras el merge, sigue con la siguiente tarea en una rama nueva.

**Meta:** ≥ **10 commits** visibles con tu autoría en el historial de `main` (vía PR).

---

## Tareas de Steve (≥12 ítems tamaño-commit)

| # | Tarea | Archivo(s) | Marcador | Mensaje de commit sugerido |
|---|--------|------------|----------|----------------------------|
| 1 | Poner tu nombre y rol (reemplazar “Integrante 2”) | `CreditsScreen.kt` | `TODO(equipo - Steve)` | `Agrega nombre de Steve en la pantalla de créditos` |
| 2 | Actualizar sección Equipo del README con tu nombre | `README.md` | tabla Equipo | `Actualiza el README con el nombre de Steve` |
| 3 | Agregar **3 apartamentos** (Aranjuez, Manrique, Robledo) en SampleData | `SampleData.kt` | `TODO(equipo - Steve)` en SampleData | `Agrega tres apartamentos de ejemplo en SampleData` |
| 4 | Agregar **2 casas** (Bello, Itagüí) con amenidades distintas | `SampleData.kt` | mismo | `Agrega dos casas de ejemplo en SampleData` |
| 5 | INSERTs equivalentes en SQL | `docs/supabase/semillas_steve.sql` | `TODO(equipo - Steve)` | `Agrega semillas SQL de propiedades de Steve` |
| 6 | Diálogo “¿Olvidaste la clave?” | `LoginScreen.kt` | `TODO(equipo - Steve)` | `Implementa el diálogo de recuperación de contraseña` |
| 7 | Validar formato de correo en registro | `RegisterScreen.kt` | `TODO(equipo - Steve)` | `Valida el formato del correo en el registro` |
| 8 | Validar teléfono colombiano (10 dígitos) | `RegisterScreen.kt` | `TODO(equipo - Steve)` | `Valida el teléfono colombiano en el registro` |
| 9 | Filtro de precio máximo más claro (chip o etiqueta) | `SearchScreen.kt` | `TODO(equipo - Steve)` | `Mejora el filtro de precio máximo en la búsqueda` |
| 10 | Contador “X favoritos” cuando la lista no esté vacía | `FavoritesScreen.kt` | `TODO(equipo - Steve)` | `Muestra el contador de favoritos en la barra` |
| 11 | Crear `AboutScreen` (versión, UPB, Kotlin/Compose) y cablear desde Ajustes | nuevo `AboutScreen.kt`, `Screen.kt`, `RentaYaApp.kt`, `SettingsScreen.kt` | `TODO(equipo - Steve)` en Settings | `Agrega la pantalla Acerca de y su navegación` |
| 12 | *(Bonus)* 2 capturas Login + Búsqueda en `docs/capturas/` y enlace en README | `docs/capturas/`, `README.md` | comentario capturas | `Agrega capturas de Login y Búsqueda al README` |
| 13 | *(Bonus)* Tipo `PARQUEADERO` o amenidad “Mascotas” + 1 propiedad | `Property.kt`, `SampleData.kt` | — | `Agrega amenidad Mascotas y una propiedad de ejemplo` |

---

## Tareas de Mariana (≥12 ítems tamaño-commit)

| # | Tarea | Archivo(s) | Marcador | Mensaje de commit sugerido |
|---|--------|------------|----------|----------------------------|
| 1 | Poner tu nombre y rol (reemplazar “Integrante 3”) | `CreditsScreen.kt` | `TODO(equipo - Mariana)` | `Agrega nombre de Mariana en la pantalla de créditos` |
| 2 | Actualizar README (integrante 3; capturas o texto de equipo) | `README.md` | tabla Equipo | `Actualiza el README con el nombre de Mariana` |
| 3 | Agregar **3 apartamentos** (Laureles Norte, Estadio, Guayabal) | `SampleData.kt` | `TODO(equipo - Mariana)` | `Agrega tres apartamentos de ejemplo de Mariana` |
| 4 | Agregar **2 cuartos** económicos (Buenos Aires, Castilla) | `SampleData.kt` | mismo | `Agrega dos cuartos económicos en SampleData` |
| 5 | INSERTs equivalentes en SQL | `docs/supabase/semillas_mariana.sql` | `TODO(equipo - Mariana)` | `Agrega semillas SQL de propiedades de Mariana` |
| 6 | **Persistir publicar**: llamar `RepositorioPropiedades.publicarPropiedad` desde el formulario | `PublishPropertyScreen.kt` | `TODO(equipo - Mariana)` (el repo **ya** implementa el método) | `Conecta el formulario de publicar con el repositorio` |
| 7 | Validaciones extra: precio > 0, área > 0, habitaciones ≥ 1 | `PublishPropertyScreen.kt` | mismo bloque | `Agrega validaciones de precio, área y habitaciones` |
| 8 | Pantalla **Mis publicaciones** + navegación desde Perfil | nuevo `MyPropertiesScreen.kt`, `Screen.kt`, `RentaYaApp.kt`, `ProfileScreen.kt` | `TODO(equipo - Mariana)` en Profile | `Agrega la pantalla Mis publicaciones` |
| 9 | Empty state mejorado en mensajes (ícono, texto, CTA a Buscar) | `MessagesScreen.kt` | `TODO(equipo - Mariana)` | `Mejora el estado vacío de la bandeja de mensajes` |
| 10 | Sección “Tecnologías usadas” en Créditos o About | `CreditsScreen.kt` o About | `TODO(equipo - Mariana)` | `Documenta las tecnologías usadas en Créditos` |
| 11 | ≥4 capturas para Play/README en `docs/capturas/` | `docs/capturas/`, `README.md` | comentario capturas | `Agrega capturas de pantalla para Play y README` |
| 12 | *(Bonus)* Persistencia de IDs de favoritos en DataStore | `UserPreferences.kt`, pantallas favoritos | — | `Persiste los favoritos en DataStore` |
| 13 | *(Bonus)* Revisar párrafo de privacidad / enlace en Settings si aplica | `docs/privacidad.html` | coordinar con Juan | `Ajusta el texto de privacidad según el modo remoto` |

### Nota sobre la tarea 6 (publicar)

`RepositorioPropiedades.publicarPropiedad` **ya está listo**: agrega a la lista local mutable y, si hay Supabase, hace INSERT.  
Tu trabajo es **solo cablear la UI** del `PublishPropertyScreen` (construir el `Property` y llamar al método en un `coroutineScope`).

Para listar “Mis publicaciones” usa `RepositorioPropiedades.obtenerPublicadasLocalmente()`.

---

## Qué no deben tocar (salvo acuerdo)

- No agregar colaboradores bots ni `Co-authored-by` de agentes.
- No subir `local.properties`, keystores ni claves `service_role`.
- No borrar los TODOs de la otra persona.
- Package `com.rentaya.app` y nombre visible **RentaYa** se mantienen.

## Referencias rápidas

- Guía Supabase: [`docs/supabase/CONFIGURAR.md`](docs/supabase/CONFIGURAR.md)
- Esquema + seed base: [`docs/supabase/esquema.sql`](docs/supabase/esquema.sql)
- Prototipo Figma: https://www.figma.com/design/6IKRsM1DLKdmIa6J7ZciIO/Soluciones-parchadas
