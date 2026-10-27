# Documentación RentaYa

Este directorio contiene la documentación y páginas web de RentaYa, publicadas mediante GitHub Pages.

## Páginas disponibles

### Landing Page Principal
- **URL**: https://juanval0308.github.io/RentaYA/
- **Archivo**: `index.html`
- **Descripción**: Página principal del proyecto con descripción de RentaYa

### Página de Verificación de Email
- **URL**: https://juanval0308.github.io/RentaYA/verificado.html
- **Archivo**: `verificado.html`
- **Descripción**: Página de confirmación exitosa después de verificar el email
- **Uso**: Los usuarios llegan aquí al hacer clic en el enlace de verificación de Supabase
- **Funcionalidad**: 
  - Muestra mensaje "¡Verificado!"
  - Redirige automáticamente a la app mediante deep link `rentaya://auth-callback`
  - Incluye botón manual para abrir la app

### Política de Privacidad
- **URL**: https://juanval0308.github.io/RentaYA/privacidad.html
- **Archivo**: `privacidad.html`
- **Descripción**: Política de privacidad de RentaYa

## Configuración de GitHub Pages

Para que estas páginas estén disponibles, GitHub Pages debe estar habilitado:

1. Ve a **Settings** del repositorio en GitHub
2. En la sección **Pages** (lateral izquierdo)
3. Configura:
   - **Source**: Deploy from a branch
   - **Branch**: `main` (o la rama principal)
   - **Folder**: `/docs`
4. Guarda los cambios

Después de unos minutos, las páginas estarán disponibles en:
`https://juanval0308.github.io/RentaYA/`

## Integración con Supabase

La página `verificado.html` está configurada para funcionar con Supabase Auth:

### Configuración actual en Supabase Dashboard
- **Project ref**: `vpivstoawetgxlvfjnjb`
- **Site URL**: `https://juanval0308.github.io/RentaYA/`
- **Redirect URLs**:
  - `https://juanval0308.github.io/RentaYA/**`
  - `rentaya://auth-callback`

### Flujo de verificación
1. Usuario se registra en la app
2. Supabase envía email de verificación
3. Usuario hace clic en el enlace del email
4. Supabase redirige a `verificado.html`
5. Usuario ve mensaje de éxito
6. La página intenta abrir la app automáticamente
7. Usuario regresa a la app para iniciar sesión

## Estructura de directorios

```
docs/
├── index.html              # Landing page principal
├── verificado.html         # Página de verificación de email
├── privacidad.html         # Política de privacidad
├── README.md              # Este archivo
├── capturas/              # Capturas de pantalla de la app
├── presentacion/          # Materiales de presentación
├── prueba-dispositivo/    # Documentación de pruebas
├── supabase/             # Documentación de configuración Supabase
│   ├── CONFIGURAR.md
│   ├── VERIFICACION_EMAIL.md
│   ├── esquema.sql
│   └── ...
└── wireframes/           # Diseños y mockups
```

## Verificación

Para verificar que GitHub Pages está funcionando correctamente:

1. **Landing page**: https://juanval0308.github.io/RentaYA/
2. **Verificación**: https://juanval0308.github.io/RentaYA/verificado.html
3. **Privacidad**: https://juanval0308.github.io/RentaYA/privacidad.html

Si alguna página muestra error 404, espera 1-2 minutos para que GitHub Pages actualice después de un push.

## Actualización de contenido

Para actualizar cualquier página:

1. Edita el archivo HTML correspondiente en `docs/`
2. Commit y push a la rama principal
3. Espera 1-2 minutos para que GitHub Pages actualice
4. Verifica los cambios en la URL correspondiente

## Soporte

Para más información sobre la configuración de Supabase y el flujo de verificación, consulta:
- `supabase/CONFIGURAR.md`
- `supabase/VERIFICACION_EMAIL.md`
