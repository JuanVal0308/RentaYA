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

Registro e inicio de sesión van contra Supabase Auth (confirmación de correo desactivada en el proyecto).  
La búsqueda intenta listar la tabla `propiedades`; si no hay red, cae a `SampleData` local.

## Proyecto actual

- Cuenta: `juanpa.martinezromero@gmail.com`
- URL: ver `supabase.properties` / panel Supabase → Settings → API
- Tablas: `perfiles`, `propiedades` (20 semillas), `favoritos`
- Confirm email: OFF

## Si creas otro proyecto desde cero

1. New project en Supabase con esa cuenta.
2. SQL Editor → pegar y ejecutar [`esquema.sql`](esquema.sql).
3. (Opcional) [`politica_borrado.sql`](politica_borrado.sql) para borrar publicaciones propias.
4. Copiar URL + anon key a `supabase.properties` (o override en `local.properties`).

## Semillas extra del equipo

- Steve: [`semillas_steve.sql`](semillas_steve.sql)  
- Mariana: [`semillas_mariana.sql`](semillas_mariana.sql)  

## Notas de seguridad

- La clave **anon** es pública por diseño y va en el APK; RLS limita qué puede hacer cada rol.
- No commits de `local.properties`, keystores ni `service_role`.
