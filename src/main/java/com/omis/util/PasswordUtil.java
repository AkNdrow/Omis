package com.omis.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para el manejo seguro de contraseñas con BCrypt.
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Genera un hash BCrypt a partir de una contraseña en texto plano.
     * @param plainPassword Contraseña sin encriptar.
     * @return Hash BCrypt seguro.
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt.
     * @param plainPassword Contraseña ingresada por el usuario.
     * @param hashedPassword Hash almacenado en la base de datos.
     * @return true si coinciden.
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Error al verificar contraseña: " + e.getMessage());
            return false;
        }
    }
}
