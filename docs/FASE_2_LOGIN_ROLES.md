# OMIS — Fase 2: Login + Sistema de Roles

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Implementar la autenticación de usuarios con contraseñas hasheadas (BCrypt), control de sesión en memoria y la interfaz de login con navegación al Dashboard según el rol del usuario.

---

## Archivos Creados

### Capa Modelo

#### `model/Usuario.java`
POJO que representa la tabla `usuario`. Incluye:
- Campos: `idUsuario`, `nombreCompleto`, `usuarioLogin`, `password`, `rol`
- Constructor vacío + constructor completo
- Método de conveniencia `esJefe()` para verificación rápida de rol
- `toString()` formateado: `"Nombre (Rol)"`

---

### Capa DAO (Data Access Object)

#### `dao/UsuarioDAO.java`
CRUD completo para la tabla `usuario` con `PreparedStatement`:

| Método | SQL | Descripción |
|--------|-----|-------------|
| `findByLogin(String)` | `SELECT ... WHERE usuario_login = ?` | Busca por nombre de login |
| `findAll()` | `SELECT ... ORDER BY nombre_completo` | Lista todos los usuarios |
| `create(Usuario)` | `INSERT INTO usuario ...` | Crea usuario (password debe llegar hasheada) |
| `update(Usuario)` | `UPDATE usuario SET ...` | Actualiza datos; si password es null/vacío, no lo modifica |
| `delete(int)` | `DELETE FROM usuario WHERE id_usuario = ?` | Elimina por ID |
| `count()` | `SELECT COUNT(*)` | Cuenta total de usuarios (usado para detectar primera ejecución) |

---

### Capa Utilidades

#### `util/PasswordUtil.java`
Encapsula la librería jBCrypt con 2 métodos estáticos:
- `hash(String)` → genera hash BCrypt con 10 rondas
- `verify(String, String)` → compara contraseña plana contra hash almacenado

#### `util/SessionManager.java`
Singleton que almacena el `Usuario` activo en memoria:
- `login(Usuario)` → registra la sesión
- `logout()` → limpia la sesión
- `getUsuarioActivo()` → devuelve el usuario logueado
- `haySesion()` → verifica si hay sesión activa
- `esJefe()` → shortcut para verificar rol Jefe

---

### Capa Servicio

#### `service/AuthService.java`
Lógica de autenticación:
- `autenticar(String login, String password)`:
  1. Valida que los campos no estén vacíos
  2. Busca el usuario por login en la BD
  3. Verifica la contraseña con BCrypt
  4. Si es exitoso, registra la sesión en `SessionManager`
  5. Retorna el `Usuario` autenticado o `null`
- `cerrarSesion()` → invoca `SessionManager.logout()`
- `asegurarUsuarioAdmin()` → si la tabla `usuario` está vacía, crea un usuario Jefe por defecto:
  - Login: `admin`
  - Contraseña: `admin123`
  - Rol: `Jefe`
  - Hash generado dinámicamente con BCrypt (no hardcodeado en SQL)

---

### Capa Vista (FXML + Controller)

#### `fxml/login.fxml` + `controller/LoginController.java`

**Diseño visual:**
- Fondo con gradiente azul oscuro (`#1a237e` → `#1976D2`)
- Tarjeta blanca centrada con sombra (border-radius 16px)
- Logo "OMIS" en azul oscuro, subtítulo "Papelería Omis"
- Campos de usuario y contraseña con bordes redondeados
- Botón "Ingresar" con gradiente azul
- Label de error en rojo para credenciales incorrectas
- Presionar Enter en el campo de contraseña ejecuta el login

**Flujo del controlador:**
1. Valida campos vacíos → muestra error específico
2. Deshabilita el botón mientras procesa
3. Invoca `AuthService.autenticar()`
4. Si exitoso → navega a `dashboard.fxml`
5. Si falla → muestra "Usuario o contraseña incorrectos.", limpia contraseña, re-enfoca

#### `fxml/dashboard.fxml` + `controller/DashboardController.java`

**Dashboard placeholder** con estructura completa:
- **Sidebar** (220px, fondo `#1a237e`):
  - Logo OMIS
  - Info del usuario activo (nombre + rol)
  - Botones de navegación: Panel de Control, Inventario, Punto de Venta, Catálogos, Reportes, Usuarios
  - Botón "Cerrar Sesión" al final (texto rojo)
- **Área de contenido** central con mensaje de bienvenida
- El botón "Usuarios" solo es visible para el rol Jefe
- "Cerrar Sesión" regresa a `login.fxml`

---

### Estilos CSS Agregados

Se añadieron al archivo `styles.css` los estilos específicos del login:

| Clase CSS | Elemento | Estilo |
|-----------|----------|--------|
| `.login-root` | Fondo completo | Gradiente azul oscuro diagonal |
| `.login-card` | Tarjeta del formulario | Blanca, border-radius 16px, sombra pronunciada |
| `.login-logo` | Texto "OMIS" | 42px, bold, azul oscuro |
| `.login-button` | Botón Ingresar | Gradiente azul, 16px, bold, border-radius 8px |
| `.login-error` | Mensaje de error | Rojo (`-omis-danger`), 13px, bold |

---

### Modificaciones a Archivos Existentes

| Archivo | Cambio |
|---------|--------|
| `module-info.java` | Agregados: `requires jbcrypt`, opens/exports para `controller`, `model`, `dao`, `service`, `util` |
| `Main.java` | Carga `login.fxml` en vez de vista temporal. Invoca `AuthService.asegurarUsuarioAdmin()` en `init()` |
| `schema.sql` | Removido INSERT hardcodeado del admin. Ahora se crea programáticamente con BCrypt real |

---

## Verificaciones

| Prueba | Resultado |
|--------|-----------|
| Compilación `mvn compile` | ✅ Sin errores |
| App arranca y muestra login | ✅ Ventana con gradiente azul y tarjeta blanca |
| Login con `admin` / `admin123` | ✅ Navega al Dashboard |
| Dashboard muestra nombre y rol | ✅ "Administrador — Rol: Jefe" |
| Login con credenciales incorrectas | ✅ Mensaje de error en rojo |
| Botón "Usuarios" visible solo para Jefe | ✅ Funcional |
| Cerrar sesión regresa al login | ✅ Funcional |
| BD crea usuario admin con BCrypt | ✅ Hash generado dinámicamente |

---

## Credenciales por Defecto

| Campo | Valor |
|-------|-------|
| Usuario | `admin` |
| Contraseña | `admin123` |
| Rol | Jefe |

> ⚠️ Se recomienda cambiar la contraseña por defecto una vez que el módulo de gestión de usuarios (Fase 8) esté implementado.

---

## Siguiente: Fase 3 — CRUD Catálogos

La siguiente fase implementará:
- Modelos: `Categoria.java`, `Marca.java`, `Proveedor.java`
- DAOs con CRUD completo para cada entidad
- Vista `catalogos.fxml` con TabPane (pestañas) para gestionar los 3 catálogos
- Acceso exclusivo para el rol Jefe
