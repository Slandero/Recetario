package com.example.recetario.util;

import java.text.Normalizer;
import java.util.Locale;

/** Formato de cantidades para mostrarlas en pantalla y en los archivos exportados. */
public final class Formato {

    public static final String AL_GUSTO = "al gusto";

    private Formato() {
    }

    /**
     * Convierte un valor numérico a una representación de texto legible con fracciones vulgares si aplica (ej. ½, ¼, ⅓, ⅔, ¾).
     * @param valor Valor numérico double.
     * @return Cadena formateada.
     */
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
     * Formatea la cantidad junto con la unidad de medida, convirtiendo unidades menores a mayores según la escala (ej. 1500 g -> 1.5 kg).
     * @param cantidad Cantidad numérica.
     * @param unidad Unidad de medida original.
     * @return Cadena con la cantidad y unidad formateadas.
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

    /**
     * Obtiene el plural de una unidad si aplica.
     * @param unidad Unidad en singular.
     * @return Unidad pluralizada o igual si es abreviatura.
     */
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

    /**
     * Formatea un valor numérico para edición en un campo de texto decimal (ej. 3 -> "3", 0.5 -> "0.5").
     * @param valor Valor double.
     * @return Cadena sin ceros redundantes a la derecha.
     */
    public static String decimal(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        String s = String.format(Locale.US, "%.3f", valor).replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Formatea los minutos a horas y minutos (ej. 150 min -> "2 h 30 min").
     * @param minutos Minutos de preparación.
     * @return Cadena formateada de tiempo.
     */
    public static String tiempo(int minutos) {
        if (minutos < 60) {
            return minutos + " min";
        }
        int h = minutos / 60;
        int m = minutos % 60;
        return m == 0 ? h + " h" : h + " h " + m + " min";
    }

    /**
     * Sanitiza una cadena de texto para usar como un nombre de archivo seguro en el sistema.
     * @param texto Nombre original.
     * @return Cadena normalizada sin acentos ni caracteres especiales (ej. "chile_en_nogada").
     */
    public static String nombreArchivo(String texto) {
        String s = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        return s.isEmpty() ? "receta" : s;
    }
}
