# Configurar Supabase para RentaYa

El repositorio ya incluye **`supabase.properties`** con la URL y la clave **anon** pública del proyecto.  
Al abrir el proyecto en Android Studio, Gradle las inyecta en `BuildConfig` y la app habla con Supabase **sin pasos extra**.

`local.properties` (generado por Android Studio, en `.gitignore`) puede **sobreescribir** esas claves si hace falta.  
Nunca subas la clave `service_role`.

## Cómo correr en Android Studio

1. Clona el repo: `git clone https://github.com/JuanVal0308/RentaYA.git` y ábrelo en Android Studio (**File → Open** sobre la carpeta raíz).
2. Usa **JDK 17 o 21** (File → Settings → Build → Gradle → Gradle JDK). En el PC del curso suele estar Adoptium 21.
3. Espera a que Gradle sincronice (el archivo `supabase.properties` ya trae URL y anon key).
4. Crea o elige un emulador API 24+, o conecta un celular con **depuración USB**.
5. Pulsa **Run 'app'** (Shift+F10).  
   Compilar por terminal: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

Registro e inicio de sesión van contra Supabase Auth. **Para la demo a inversionistas Confirm email DEBE estar encendido** (ver más abajo).  
La búsqueda intenta listar la tabla `propiedades`; si no hay red, cae a `SampleData` local (fotos empaquetadas + pines por barrio).

## Proyecto actual

- Cuenta: `juanpa.martinezromero@gmail.com`
- Ref: `vpivstoawetgxlvfjnjb`
- URL: ver `supabase.properties` / panel Supabase → Settings → API
- Tablas: `perfiles`, `propiedades` (semillas + columna `imagenes`), `favoritos`
- Storage: bucket `inmuebles` (lectura pública, escritura autenticada)
- Confirm email: **ON** (obligatorio en demo comercial)

## Pasos manuales en el Dashboard (imprescindibles para la demo)

Estos no se pueden hacer solo desde el APK. Entrá con `juanpa.martinezromero@gmail.com`:

### 1. Confirm email (Auth)

1. Authentication → Providers → Email → **Confirm email = ON**.
2. Authentication → URL Configuration:
   - **Site URL**: `https://juanval0308.github.io/RentaYA/`
   - **Redirect URLs**:
     - `https://juanval0308.github.io/RentaYA/**`
     - `rentaya://auth-callback`
3. El correo de verificación puede apuntar a GitHub Pages o al deep link `rentaya://auth-callback`. En el celular, tras confirmar, el usuario vuelve a la app y pulsa **Ya confirmé, iniciar sesión**.

No hay atajo de verificación en *release*. En debug, solo si `local.properties` tiene `DEBUG_OMITIR_VERIFICACION_CORREO=true` (no usar en pitches).

### 2. Schema + coordenadas + Storage

Si el proyecto ya existía (semillas en el centro `6.2442, -75.5812`):

1. SQL Editor → pegar y ejecutar [`migracion_fotos_mapa.sql`](migracion_fotos_mapa.sql).  
   Eso agrega `imagenes jsonb`, recoloca los pines por barrio y crea el bucket `inmuebles` con RLS.
2. Storage → bucket **inmuebles** → debe quedar **Public**. Si el SQL del bucket falla por permisos, créalo a mano: New bucket → `inmuebles` → Public.

Proyecto nuevo desde cero:

1. SQL Editor → [`esquema.sql`](esquema.sql)
2. SQL Editor → [`migracion_fotos_mapa.sql`](migracion_fotos_mapa.sql) (Storage + UPDATE; la columna `imagenes` ya viene en el esquema nuevo)
3. Opcional: [`politica_borrado.sql`](politica_borrado.sql)
4. Copiar URL + anon key a `supabase.properties` (o override en `local.properties`)

### 3. Semillas extra del equipo

- Steve: [`semillas_steve.sql`](semillas_steve.sql)  
- Mariana: [`semillas_mariana.sql`](semillas_mariana.sql)  

Las coordenadas de Mariana ya están por barrio. Si Steve agrega filas, usar `CoordenadasBarrios` (El Poblado, Laureles, Aranjuez, etc.) y no el centro genérico.

## Fotos

- `imagen`: URL `https://…/storage/v1/object/public/inmuebles/…`, clave de asset (`apto1.jpg`) o `sample`.
- `imagenes`: arreglo JSON de esas mismas URLs/claves.
- Si ambas están vacías o `sample`, la app elige JPG locales (`assets/inmuebles/`) con el mismo hash-por-id de RentaGo.

## Notas de seguridad

- La clave **anon** es pública por diseño y va en el APK; RLS limita qué puede hacer cada rol.
- No commits de `local.properties`, keystores ni `service_role`.
- El mapa usa OpenStreetMap (osmdroid); no requiere `MAPS_API_KEY`.
