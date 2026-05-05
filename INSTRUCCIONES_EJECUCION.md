# Instrucciones de Ejecución - Sistema OMIS

Este documento contiene las instrucciones necesarias para ejecutar y compilar el sistema de gestión de la Papelería OMIS.

## Requisitos Previos

Para ejecutar el programa desde el código fuente, asegúrate de tener instalado:
1. **Java JDK 21** o superior.
2. **Apache Maven** (configurado en las variables de entorno).
3. **SQLite** (incluido automáticamente mediante el driver JDBC).

---

## Cómo ejecutar el programa

1. **Abrir una terminal:** Navega hasta la carpeta raíz del proyecto (`.../OMIS/OmisProject/Omis`).
2. **Ejecutar el comando de Maven:**
   ```powershell
   mvn javafx:run
   ```
   *Este comando compila el código, descarga las dependencias necesarias y lanza la interfaz gráfica.*

---

## Estructura de Reportes

Los reportes generados por el sistema se guardan automáticamente en:
`[Carpeta del Proyecto]/Reportes de venta Omis/`

Puedes acceder a ellos directamente desde el botón **"Reportes"** en el menú lateral de la aplicación, el cual abrirá el explorador de archivos automáticamente.

---

## Credenciales por Defecto (Acceso Total)

*   **Usuario:** `admin`
*   **Contraseña:** `admin123`

---

## Generación de Instalador (.exe)

Para crear un instalador independiente que no requiera Maven ni Java instalado en la máquina cliente:

1. **Empaquetar el proyecto:**
   ```powershell
   mvn clean package
   ```
2. **Generar el ejecutable (requiere herramienta jpackage de JDK):**
   *(Este paso varía según el sistema operativo, contactar con el desarrollador para la configuración del script de despliegue final).*

---
**Nota:** Si realizas cambios en los archivos `.fxml` o `.css`, recuerda ejecutar `mvn compile` antes de `mvn javafx:run` para asegurar que los cambios se reflejen en la ejecución.
