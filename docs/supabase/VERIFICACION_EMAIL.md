# Configuración de Verificación de Email

Este documento explica cómo configurar el flujo de verificación de correo electrónico para RentaYa con Supabase.

## Flujo de verificación

1. **Usuario se registra** en la app con su correo y contraseña
2. **Supabase Auth envía** un correo de verificación con un enlace
3. **Usuario hace clic** en el enlace del correo
4. **Supabase redirige** a la página de confirmación exitosa
5. **Usuario ve** mensaje "¡Verificado!" y puede volver a la app
6. **Usuario inicia sesión** en la app con su cuenta verificada

## Configuración requerida en Supabase Dashboard

### Authentication → URL Configuration

Configura las siguientes URLs en el proyecto de Supabase:

- **Site URL**: `https://juanval0308.github.io/RentaYA/`
  - URL base del sitio web del proyecto (GitHub Pages)

- **Redirect URLs**:
  - `https://juanval0308.github.io/RentaYA/**`
  - `https://juanval0308.github.io/RentaYA/verificado.html`
  - `rentaya://auth-callback`

### Email Templates (opcional)

Puedes personalizar el template del correo de verificación en:
**Authentication → Email Templates → Confirm signup**

Por defecto, Supabase usa `{{ .ConfirmationURL }}` que redirige a la Site URL configurada.

## Página de verificación

La página `docs/verificado.html` sirve como landing page después de confirmar el email:

- **URL pública**: `https://juanval0308.github.io/RentaYA/verificado.html`
- **Función**: Mostrar mensaje de éxito y redirigir a la app
- **Deep link**: Intenta abrir `rentaya://auth-callback` automáticamente

### Despliegue en GitHub Pages

Para que la página esté disponible:

1. El contenido de `docs/` está configurado como fuente de GitHub Pages
2. Tras hacer push, GitHub Pages publica automáticamente `verificado.html`
3. La URL será accesible en: `https://juanval0308.github.io/RentaYA/verificado.html`

## Deep Link en Android

El AndroidManifest de la app ya incluye el deep link:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="rentaya" android:host="auth-callback" />
</intent-filter>
```

Esto permite que `rentaya://auth-callback` abra la app automáticamente.

## Verificación del flujo completo

1. Verifica que GitHub Pages esté activo: Settings → Pages → Source: docs/
2. Confirma que `verificado.html` es accesible: https://juanval0308.github.io/RentaYA/verificado.html
3. En Supabase Dashboard, verifica las URLs en Authentication → URL Configuration
4. Prueba el registro desde la app y revisa el correo de verificación

## Proyecto actual

- **Cuenta Supabase**: `juanpa.martinezromero@gmail.com`
- **Project ref**: `vpivstoawetgxlvfjnjb`
- **Site URL ya configurada**: `https://juanval0308.github.io/RentaYA/`
- **Confirm email**: ON (obligatorio para demo)

## Solución de problemas

### Error 404 al hacer clic en el enlace de verificación

**Causa**: La página `verificado.html` no está publicada en GitHub Pages.

**Solución**:
1. Hacer commit y push de `docs/verificado.html`
2. Esperar 1-2 minutos para que GitHub Pages actualice
3. Verificar acceso a la URL

### El enlace no abre la app

**Causa**: El deep link no está configurado o la app no está instalada.

**Solución**:
1. Verificar que la app esté instalada en el dispositivo
2. El botón manual "Abrir RentaYa" en la página permite intentar nuevamente
3. Si no funciona, el usuario puede abrir la app manualmente

### Usuario no puede publicar inmuebles después de verificar

**Causa**: El flag de verificación no se actualizó en el cliente.

**Solución**:
- Cerrar sesión y volver a iniciar sesión
- El método `ClienteSupabase.puedePublicar()` verifica el estado actualizado
