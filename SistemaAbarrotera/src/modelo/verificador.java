package modelo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class verificador {

    // 1. Solo letras (nombres, apellidos, direcciones)
    public static boolean esSoloLetras(String texto) {
        return validarRegex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", texto);
    }

    // 2. Solo números (telefonos, cantidades, precios)
    public static boolean esSoloNumeros(String texto) {
        return validarRegex("^[0-9]+$", texto);
    }

    // 3. Solo mayúsculas (IDs, códigos, claves)
    public static boolean esSoloMayusculas(String texto) {
        return validarRegex("^[A-ZÁÉÍÓÚÑ\\s]+$", texto);
    }

    // 4. Formato RFC (Registro Federal de Contribuyentes)
    public static boolean esRFCValido(String texto) {
        return validarRegex("^[A-ZÑ&]{3,4}\\d{6}[A-Z\\d]{3}$", texto);
    }

    // Motor de validación interno
    private static boolean validarRegex(String regex, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(texto);
        return matcher.matches();
    }
}