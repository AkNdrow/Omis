package com.omis.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Singleton que gestiona la conexión JDBC a la base de datos SQLite.
 * El archivo de base de datos se crea junto al ejecutable.
 */
public class DatabaseManager {

    private static final String DB_NAME = "omis_data.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        // Constructor privado para Singleton
    }

    /**
     * Obtiene la instancia única del DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtiene la conexión activa a SQLite.
     * Si no existe o está cerrada, crea una nueva.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            // Habilitar foreign keys (SQLite las tiene deshabilitadas por defecto)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    /**
     * Inicializa la base de datos ejecutando el schema.sql.
     * Crea las tablas si no existen e inserta datos iniciales.
     */
    public void initDatabase() {
        try {
            Connection conn = getConnection();

            // Leer el archivo schema.sql desde resources
            InputStream is = getClass().getResourceAsStream("/sql/schema.sql");
            if (is == null) {
                System.err.println("ERROR: No se encontró el archivo schema.sql en resources");
                return;
            }

            String schema;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                schema = reader.lines().collect(Collectors.joining("\n"));
            }

            // Ejecutar cada sentencia SQL línea por línea
            try (Statement stmt = conn.createStatement()) {
                StringBuilder currentStatement = new StringBuilder();
                for (String line : schema.split("\n")) {
                    String trimmedLine = line.trim();
                    // Ignorar líneas vacías y comentarios
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                        continue;
                    }
                    currentStatement.append(line).append("\n");
                    // Solo ejecutar cuando la línea termina en punto y coma
                    if (trimmedLine.endsWith(";")) {
                        String sql = currentStatement.toString().trim();
                        // Remover el punto y coma final
                        sql = sql.substring(0, sql.length() - 1).trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                        currentStatement.setLength(0);
                    }
                }
            }

            System.out.println("Base de datos inicializada correctamente: " + DB_NAME);

        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexión a la base de datos.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
