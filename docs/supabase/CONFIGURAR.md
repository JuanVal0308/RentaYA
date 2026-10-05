# Configurar Supabase para RentaYa

La app funciona **sin** Supabase (modo offline con `SampleData`).  
Estas instrucciones activan auth y listado remoto cuando quieras.

## 1. Crear el proyecto

1. Entra a [https://supabase.com](https://supabase.com) con la cuenta **`juanpa.martinezromero@gmail.com`**  
   (no uses la cuenta de Google Play `juanpa.martinezro@gmail.com`).
2. **New project** → nombre sugerido: `rentaya`.
3. Elige región cercana (p. ej. South America / East US) y una contraseña fuerte de base de datos (guárdala en un gestor; **no** la subas al repo).
4. Espera a que el proyecto quede listo.

## 2. Ejecutar el esquema

1. En el panel: **SQL Editor** → **New query**.
2. Copia y pega todo el contenido de [`esquema.sql`](esquema.sql).
3. Pulsa **Run**. Debe crear tablas `perfiles`, `propiedades`, `favoritos`, políticas RLS y las 20 propiedades semilla.

## 3. Copiar URL y clave anónima

1. Ve a **Project Settings** → **API**.
2. Copia:
   - **Project URL** → `SUPABASE_URL`
   - **anon public** key → `SUPABASE_ANON_KEY`  
     (nunca subas la `service_role` al cliente Android ni al Git).

## 4. Pegar en `local.properties`

En la raíz del repo (mismo nivel que `settings.gradle.kts`), edita o crea `local.properties`  
(está en `.gitignore`; ver también `local.properties.example`):

```properties
sdk.dir=C:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=https://TU_PROYECTO.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOi...tu_anon_key
```

Luego en Android Studio: **Sync Project with Gradle Files** y vuelve a instalar la app.  
`BuildConfig` inyecta esas cadenas en tiempo de compilación.

## 5. Probar

1. Registra un usuario nuevo desde la app (irá a Supabase Auth).
2. Inicia sesión con ese correo.
3. La búsqueda intentará listar `propiedades` por REST; si la red falla, cae a datos locales.
4. Cuando Mariana conecte el formulario, `publicarPropiedad` insertará en la tabla remota (y siempre en la lista local).

## 6. Semillas extra del equipo

- Steve: [`semillas_steve.sql`](semillas_steve.sql)  
- Mariana: [`semillas_mariana.sql`](semillas_mariana.sql)  

Después de agregar INSERTs, ejecútalos en el SQL Editor (ids distintos a 1–20).

## Notas de seguridad

- Solo la clave **anon** va en el APK (protegida por RLS).
- No commits de `local.properties`, keystores ni `service_role`.
- Auth por correo/contraseña de Supabase; en local sin claves se usa `SampleData` en memoria.
