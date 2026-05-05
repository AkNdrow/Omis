-- ============================================
-- OMIS - Papelería Omis
-- Schema de Base de Datos (SQLite)
-- ============================================

-- Catálogos base
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_categoria TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS marca (
    id_marca INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_marca TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_empresa TEXT NOT NULL,
    contacto TEXT,
    frecuencia_entrega TEXT
);

-- Usuarios y roles
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_completo TEXT NOT NULL,
    usuario_login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    rol TEXT NOT NULL CHECK(rol IN ('Jefe', 'Empleado'))
);

-- Productos (giro mixto: escolar + alimentos)
CREATE TABLE IF NOT EXISTS producto (
    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    costo_adquisicion REAL NOT NULL DEFAULT 0,
    precio_venta REAL NOT NULL DEFAULT 0,
    margen_utilidad REAL NOT NULL DEFAULT 0,
    stock_actual INTEGER NOT NULL DEFAULT 0,
    id_categoria INTEGER,
    ubicacion_fisica TEXT,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

-- Subentidad: Láminas didácticas (relación 1:1 con producto)
CREATE TABLE IF NOT EXISTS lamina (
    id_producto INTEGER PRIMARY KEY,
    id_marca INTEGER,
    numero_serie TEXT NOT NULL,
    tema TEXT NOT NULL,
    materia TEXT NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE,
    FOREIGN KEY (id_marca) REFERENCES marca(id_marca)
);

-- Subentidad: Perecederos (relación 1:1 con producto)
CREATE TABLE IF NOT EXISTS perecedero (
    id_producto INTEGER PRIMARY KEY,
    refrigeracion TEXT,
    fecha_caducidad DATE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto) ON DELETE CASCADE
);

-- Entradas de mercancía (cabecera)
CREATE TABLE IF NOT EXISTS entrada_mercancia (
    id_entrada INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_recepcion DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
    observaciones TEXT,
    id_proveedor INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Detalle de entrada (renglones de cada entrada)
CREATE TABLE IF NOT EXISTS detalle_entrada (
    id_detalle_ent INTEGER PRIMARY KEY AUTOINCREMENT,
    id_entrada INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad_recibida INTEGER NOT NULL,
    costo_unitario REAL NOT NULL,
    precio_calculado REAL NOT NULL DEFAULT 0,
    FOREIGN KEY (id_entrada) REFERENCES entrada_mercancia(id_entrada) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- Ventas (cabecera)
CREATE TABLE IF NOT EXISTS venta (
    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_hora DATETIME NOT NULL DEFAULT (datetime('now', 'localtime')),
    monto_total REAL NOT NULL DEFAULT 0,
    id_usuario INTEGER NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Detalle de venta (renglones del carrito)
CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario REAL NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- ============================================
-- Datos iniciales
-- ============================================

-- Categorías por defecto
INSERT OR IGNORE INTO categoria (nombre_categoria) VALUES ('Escolar');
INSERT OR IGNORE INTO categoria (nombre_categoria) VALUES ('Alimento');

-- Usuario administrador se crea programáticamente con BCrypt (ver AuthService.java)
