# MANUAL DE USUARIO - OMIS (Sistema de Gestión para Papelería)

Bienvenido al Manual de Usuario de OMIS. Este documento le guiará a través de todas las funcionalidades del sistema para que pueda gestionar su papelería de manera eficiente.

---

## 1. Inicio de Sesión (Login)

Al abrir la aplicación, verá la pantalla de inicio de sesión.
* **Usuario (Login):** Su nombre de usuario corto (ej. `admin`).
* **Contraseña:** Su clave secreta.
* El sistema diferencia entre dos **Roles**:
  * **Jefe:** Tiene acceso a TODO el sistema, incluyendo reportes, catálogos, ajuste de inventarios y gestión de empleados.
  * **Empleado:** Solo tiene acceso al **Punto de Venta** para registrar cobros a clientes.

*(Nota: Al instalar el sistema por primera vez, se crea el usuario por defecto `admin` con contraseña `admin`).*

---

## 2. Dashboard (Panel Principal)

El Dashboard es la pantalla inicial tras iniciar sesión. 
Aquí, los usuarios con rol de Jefe verán un resumen rápido de cómo va el negocio:
* **Ventas de Hoy:** Dinero total recaudado en el día.
* **Productos Bajo Stock:** Alerta inmediata indicando cuántos productos tienen menos de 5 unidades.
* **Empleados Activos:** Cantidad de usuarios registrados en el sistema.

En el lado izquierdo, dispone de un menú lateral para navegar entre módulos.

---

## 3. Módulo de Catálogos (Gestión de Productos)

En la sección **"Catálogos"**, podrá administrar el inventario base.

* **Crear Producto:** Rellene el código de barras, nombre, costo, margen de ganancia (el precio de venta se calculará automáticamente), categoría y cantidad inicial. Presione "Guardar".
* **Editar:** Seleccione un producto de la tabla inferior, cambie sus datos en los cuadros de texto y presione "Actualizar".
* **Eliminar:** Seleccione un producto y pulse el botón rojo "Eliminar Seleccionado".

---

## 4. Módulo de Entradas de Mercancía

Cuando llega un camión con nuevos productos o compras de sus proveedores, debe registrarlo en la sección **"Entrada de Merc."**.

1. Seleccione el producto buscando por código de barras o desplegando la lista.
2. Ingrese el costo de compra unitario.
3. El sistema propondrá un precio de venta basado en el margen; puede modificarlo si lo desea.
4. Escriba la cantidad que ingresa al local y pulse **"Agregar al Documento"**.
5. Repita para todos los productos que llegaron.
6. Pulse **"Registrar Entrada"**. El sistema actualizará el stock de la tienda y ajustará los nuevos precios de manera automática.

---

## 5. Módulo de Punto de Venta (POS)

Esta es la pantalla de cobro diario de su papelería.

### Búsqueda de Productos
* **Código de Barras:** Escriba el código o pásele el lector y presione `Enter`.
* **Búsqueda Avanzada (Láminas):** Si el cliente pide láminas didácticas, pulse la lupa (🔍). Se abrirá un pop-up donde podrá filtrar láminas por Tema, Materia o Serie escolar. Haga doble clic en la lámina para añadirla al carrito.

### Proceso de Cobro
1. Una vez añadido todo al carrito, la columna derecha mostrará el **Total a Pagar**.
2. Escriba la cantidad de efectivo que le entrega el cliente en el recuadro "Efectivo entregado".
3. El sistema le mostrará el cambio (vuelto) exacto que debe dar.
4. Elija cómo terminar:
   * **Registrar Venta:** Solo descuenta el stock y guarda la ganancia.
   * **Registrar y Generar Ticket PDF:** Registra la venta y además crea un recibo en PDF para imprimir al cliente.

---

## 6. Reportes y Tareas Programadas

El sistema OMIS se encarga de la contabilidad sin que usted tenga que intervenir:
* Cada vez que cobra con la opción de ticket, se genera un PDF en la carpeta: `Reportes de venta Omis / Año / Mes / Día / ticket HH-mm.pdf`.
* **Corte de Caja Diario:** Automáticamente, todos los días a medianoche (o la próxima vez que abra el sistema), se generará un reporte PDF con la suma de todo lo vendido el día anterior.
* **Cortes Semanales y Mensuales:** También se autogeneran los domingos y los fines de mes. Encontrará todos los archivos PDF en la carpeta de su sistema.

---

## 7. Gestión de Usuarios

Exclusivo para el Jefe. Aquí puede dar de alta o despedir a empleados.
* Puede asignarles contraseñas y elegir si su Rol será `Jefe` (administrador) o `Empleado` (cajero).
* El sistema encriptará las contraseñas de manera segura, de modo que ni el propio dueño pueda ver la clave exacta del empleado.

---

## 8. Salida del Sistema

Use el botón **"Cerrar Sesión"** en la parte inferior del menú lateral cuando acabe su turno. El sistema asegurará las bases de datos y lo devolverá a la pantalla de Login, protegiendo así la información de la papelería.
