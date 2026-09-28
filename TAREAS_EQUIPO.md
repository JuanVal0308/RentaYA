# TAREAS PARA EL EQUIPO

Este documento contiene tareas pendientes para que los integrantes del equipo completen. Cada tarea está marcada con `TODO(equipo)` en el código.

## 📝 Tareas Disponibles

### 1. Agregar información de integrantes en Créditos ⭐ FÁCIL
**Archivos:** `app/src/main/java/com/rentaya/app/ui/screens/CreditsScreen.kt`

**Pasos:**
1. Abrir el archivo `CreditsScreen.kt`
2. Buscar los comentarios `// TODO(equipo): Agregar información del segundo integrante` y `// TODO(equipo): Agregar información del tercer integrante`
3. Reemplazar "Integrante 2" y "Integrante 3" con sus nombres completos
4. Opcional: Agregar sus fotos o cambiar el rol si aplica

**Ejemplo:**
```kotlin
TeamMemberCard(
    name = "María Pérez González",
    role = "Desarrolladora"
)
```

---

### 2. Agregar más propiedades de ejemplo ⭐ FÁCIL
**Archivos:** `app/src/main/java/com/rentaya/app/data/SampleData.kt`

**Pasos:**
1. Abrir el archivo `SampleData.kt`
2. En la lista `properties`, agregar 5 nuevas propiedades siguiendo el formato existente
3. Usar barrios reales de Medellín: Laureles, Belén, Estadio, Aranjuez, etc.
4. Variar precios, tipos (APARTAMENTO, CASA, CUARTO), y amenidades

**Ejemplo:**
```kotlin
Property(
    id = "21",
    title = "Apto en Aranjuez",
    description = "Apartamento cerca al metro...",
    type = PropertyType.APARTAMENTO,
    price = 1250000,
    neighborhood = "Aranjuez",
    bedrooms = 2,
    bathrooms = 1,
    area = 55,
    amenities = listOf("Parqueadero"),
    landlord = Landlord("Nombre Apellido", 4.3f, "300 XXX XXXX")
)
```

---

### 3. Implementar diálogo "¿Olvidaste la clave?" ⭐⭐ MEDIA
**Archivos:** `app/src/main/java/com/rentaya/app/ui/screens/LoginScreen.kt`

**Pasos:**
1. Abrir `LoginScreen.kt`
2. Buscar el comentario `TODO(equipo): Implementar diálogo de recuperación de contraseña`
3. Crear un estado `var showForgotPasswordDialog by remember { mutableStateOf(false) }`
4. Cambiar el `onClick` del TextButton para mostrar el diálogo: `onClick = { showForgotPasswordDialog = true }`
5. Agregar un `AlertDialog` que muestre un mensaje como "Funcionalidad no disponible en versión offline"

**Ayuda:** Revisar el `AlertDialog` en `SettingsScreen.kt` como referencia.

---

### 4. Implementar pantalla "Acerca de" ⭐⭐ MEDIA
**Archivos:** 
- Crear: `app/src/main/java/com/rentaya/app/ui/screens/AboutScreen.kt`
- Modificar: `app/src/main/java/com/rentaya/app/ui/screens/SettingsScreen.kt`
- Modificar: `app/src/main/java/com/rentaya/app/ui/navigation/Screen.kt` y `RentaYaApp.kt`

**Pasos:**
1. Crear una nueva pantalla `AboutScreen.kt` similar a `CreditsScreen.kt`
2. Mostrar información como: versión de la app, descripción del proyecto, tecnologías usadas
3. Agregar la ruta en `Screen.kt`: `object About : Screen("about")`
4. Agregar la navegación en `RentaYaApp.kt`
5. En `SettingsScreen.kt`, buscar el TODO y cambiar el `onClick` para navegar a la pantalla About

---

### 5. Implementar vista "Mis publicaciones" ⭐⭐ MEDIA
**Archivos:**
- Crear: `app/src/main/java/com/rentaya/app/ui/screens/MyPropertiesScreen.kt`
- Modificar: `app/src/main/java/com/rentaya/app/data/SampleData.kt`
- Modificar: `app/src/main/java/com/rentaya/app/ui/screens/ProfileScreen.kt`

**Pasos:**
1. En `SampleData.kt`, agregar una lista `val myProperties = mutableListOf<Property>()` para guardar propiedades publicadas
2. Modificar `PublishPropertyScreen.kt` para agregar las propiedades publicadas a esa lista
3. Crear `MyPropertiesScreen.kt` que muestre las propiedades del usuario
4. Agregar navegación en `ProfileScreen.kt` y `RentaYaApp.kt`

---

### 6. Agregar capturas de pantalla al README ⭐ FÁCIL
**Archivos:** `README.md`, `docs/capturas/`

**Pasos:**
1. Ejecutar la app en un emulador o dispositivo
2. Tomar capturas de pantalla de las principales pantallas (Onboarding, Login, Búsqueda, Detalle, Perfil)
3. Guardar las imágenes en `docs/capturas/`
4. Agregar las imágenes al README.md en la sección correspondiente

---

### 7. Agregar enlace de Figma al README ⭐ FÁCIL
**Archivos:** `README.md`

**Pasos:**
1. Crear los wireframes digitales en Figma (si aún no existen)
2. Obtener el enlace público del proyecto de Figma
3. En el README.md, buscar la línea que dice `- Digitales: [TODO - Agregar enlace Figma]`
4. Reemplazarla con el enlace real: `- Digitales: [Ver en Figma](https://figma.com/...)`

---

## 🚀 Cómo contribuir

1. Escoger una tarea de la lista
2. Crear una rama: `git checkout -b feature/nombre-tarea`
3. Hacer los cambios necesarios
4. Probar que la app compila y funciona: `./gradlew assembleDebug`
5. Commit: `git commit -m "feat: descripción de la tarea"`
6. Push: `git push origin feature/nombre-tarea`
7. Crear un Pull Request hacia `main`

## ⚠️ Importante

- Asegurarse de que el código compila antes de hacer push
- Seguir el estilo de código existente
- Probar los cambios en la app antes de hacer commit
- Consultar con el equipo si tienen dudas

## 📞 Contacto

Si tienen preguntas sobre alguna tarea, pueden:
- Abrir un issue en el repositorio
- Contactar al equipo por el chat del proyecto
- Revisar el código existente como referencia
