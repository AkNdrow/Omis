# Guía para Testers y Colaboradores - OMIS

¡Bienvenido al equipo de pruebas de OMIS! Esta guía te ayudará a configurar tu entorno local desde cero para que puedas ejecutar y probar el sistema de gestión.

## 1. Requisitos de Software

Antes de empezar, asegúrate de tener instalado:
*   **Java JDK 21:** [Descargar aquí](https://www.oracle.com/java/technologies/downloads/#java21)
*   **Apache Maven:** [Descargar aquí](https://maven.apache.org/download.cgi) (Asegúrate de agregar `bin` a tu variable de entorno PATH).
*   **Git:** [Descargar aquí](https://git-scm.com/downloads)

---

## 2. Configuración de Antigravity (IA Coding Assistant)

Si vas a colaborar usando Antigravity, sigue estos pasos:
1.  Instala la extensión de **Antigravity** en tu editor de código (VS Code u otros compatibles).
2.  Inicia sesión con tu cuenta autorizada.
3.  Asegúrate de que la extensión tenga permisos de lectura/escritura en la carpeta del proyecto.

---

## 3. Clonación del Proyecto

Abre una terminal y ejecuta:
```bash
git clone https://github.com/AkNdrow/Omis.git
cd Omis/OmisProject/Omis
```

---

## 4. Ejecución del Programa

Una vez dentro de la carpeta del proyecto (`.../OmisProject/Omis`), ejecuta el siguiente comando:

```bash
mvn javafx:run
```

**Nota para Testers:**
*   Al primer arranque, el sistema creará automáticamente el archivo `omis_data.db` (base de datos SQLite).
*   **Credenciales de Admin:** 
    *   Usuario: `admin`
    *   Contraseña: `admin123`

---

## 5. Áreas de Prueba (Checklist para Testers)

Por favor, verifica los siguientes puntos durante tu sesión de prueba:
1.  **Pantalla Completa:** ¿El programa arranca y se mantiene en pantalla completa al navegar entre módulos?
2.  **Módulo de Ventas:** Intenta realizar una venta y verifica que el stock disminuya en el inventario.
3.  **Reportes:** Haz clic en el botón "Reportes" y verifica que abra el explorador de archivos en la carpeta correcta.
4.  **Seguridad:** Intenta entrar a "Usuarios" con una cuenta de rol `Empleado` (debería estar bloqueado).

---
¡Gracias por ayudarnos a mejorar OMIS! Si encuentras algún error, por favor repórtalo en la sección de *Issues* del repositorio.
