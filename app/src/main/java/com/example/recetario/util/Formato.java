package com.example.recetario.util;

import java.util.Locale;

/** Formato de cantidades para mostrarlas en pantalla y en los archivos exportados. */
public final class Formato {

    public static final String AL_GUSTO = "al gusto";

    private Formato() {
    }

    /** 2 -> "2", 1.5 -> "1½", 0.25 -> "¼", 1.3 -> "1.3" */
    public static String numero(double valor) {
        if (valor <= 0) {
            return "0";
        }
        long entero = (long) Math.floor(valor + 1e-9);
        double resto = valor - entero;
        String fraccion = null;
        if (resto < 0.02) {
            resto = 0;
        } else if (resto > 0.98) {
            entero += 1;
            resto = 0;
        } else if (Math.abs(resto - 0.25) < 0.02) {
            fraccion = "¼";
        } else if (Math.abs(resto - 1.0 / 3) < 0.02) {
            fraccion = "⅓";
        } else if (Math.abs(resto - 0.5) < 0.02) {
            fraccion = "½";
        } else if (Math.abs(resto - 2.0 / 3) < 0.02) {
            fraccion = "⅔";
        } else if (Math.abs(resto - 0.75) < 0.02) {
            fraccion = "¾";
        }
        if (resto == 0) {
            return String.valueOf(entero);
        }
        if (fraccion != null) {
            return entero == 0 ? fraccion : entero + fraccion;
        }
        String s = String.format(Locale.US, "%.2f", valor);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Cantidad con su unidad: "3 pz", "1.2 kg", "al gusto".
     * Pasa de g a kg y de ml a l (y al revés) para que se lea mejor.
     */
    public static String cantidad(double cantidad, String unidad) {
        if (AL_GUSTO.equals(unidad) || cantidad <= 0) {
            return AL_GUSTO;
        }
        if ("kg".equals(unidad) && cantidad < 1) {
            return cantidad(cantidad * 1000, "g");
        }
        if ("l".equals(unidad) && cantidad < 1) {
            return cantidad(cantidad * 1000, "ml");
        }
        if ("g".equals(unidad) || "ml".equals(unidad)) {
            if (cantidad >= 1000) {
                return numero(cantidad / 1000) + ("g".equals(unidad) ? " kg" : " l");
            }
            if (cantidad >= 10) {
                return Math.round(cantidad) + " " + unidad;
            }
        }
        return numero(cantidad) + " " + (cantidad > 1 ? plural(unidad) : unidad);
    }

    /** Las abreviaturas (pz, g, cda) no cambian; las palabras completas sí: 2 dientes, 3 tazas. */
    private static String plural(String unidad) {
        switch (unidad) {
            case "diente":
                return "dientes";
            case "rama":
                return "ramas";
            case "taza":
                return "tazas";
            default:
                return unidad;
        }
    }

    /** Número para editar en un campo de texto: 3 -> "3", 0.5 -> "0.5" (sin fracciones ni ceros de más). */
    public static String decimal(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        String s = String.format(Locale.US, "%.3f", valor).replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    /** "45 min" o "2 h 30 min" */
    public static String tiempo(int minutos) {
        if (minutos < 60) {
            return minutos + " min";
        }
        int h = minutos / 60;
        int m = minutos % 60;
        return m == 0 ? h + " h" : h + " h " + m + " min";
    }

    /** Nombre de archivo seguro: "Chile en Nogada" -> "chile_en_nogada" */
    public static String nombreArchivo(String texto) {
        String s = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return s.isEmpty() ? "receta" : s;
    }
}
