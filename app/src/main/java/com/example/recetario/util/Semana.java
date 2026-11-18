package com.example.recetario.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Fechas del planificador. Las fechas se guardan en la BD como texto "yyyy-MM-dd". */
public final class Semana {

    private static final String[] DIAS = {
            "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"
    };
    private static final String[] MESES = {
            "ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"
    };

    private Semana() {
    }

    /**
     * Retorna el formateador ISO 8601 de fechas (yyyy-MM-DD).
     * @return SimpleDateFormat configurado.
     */
    private static SimpleDateFormat iso() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setLenient(false);
        return f;
    }

    /**
     * Retorna la fecha actual en formato ISO (YYYY-MM-DD).
     * @return Cadena con la fecha de hoy.
     */
    public static String hoy() {
        return aIso(Calendar.getInstance());
    }

    /**
     * Convierte un objeto Calendar a una cadena de fecha ISO (YYYY-MM-DD).
     * @param c Instancia de Calendar.
     * @return Cadena de fecha formateada.
     */
    public static String aIso(Calendar c) {
        return iso().format(c.getTime());
    }

    /**
     * Parsea una cadena de fecha ISO (YYYY-MM-DD) a una instancia de Calendar.
     * @param fecha Cadena de fecha ISO.
     * @return Objeto Calendar.
     */
    public static Calendar desdeIso(String fecha) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(iso().parse(fecha));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fecha inválida: " + fecha, e);
        }
        return c;
    }

    /**
     * Suma o resta un número de días a una fecha ISO.
     * @param fecha Fecha base ISO (YYYY-MM-DD).
     * @param dias Cantidad de días a sumar (o restar si es negativo).
     * @return Nueva fecha ISO resultante.
     */
    public static String sumarDias(String fecha, int dias) {
        Calendar c = desdeIso(fecha);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return aIso(c);
    }

    /**
     * Obtiene la fecha correspondiente al lunes de la semana a la que pertenece la fecha especificada.
     * @param fecha Fecha de referencia ISO.
     * @return Fecha del lunes de esa semana en formato ISO.
     */
    public static String lunesDe(String fecha) {
        Calendar c = desdeIso(fecha);
        int diaSemana = c.get(Calendar.DAY_OF_WEEK); // domingo = 1 ... sábado = 7
        int retroceso = (diaSemana + 5) % 7;          // lunes -> 0, domingo -> 6
        c.add(Calendar.DAY_OF_MONTH, -retroceso);
        return aIso(c);
    }

    /**
     * Genera un arreglo con las 7 fechas ISO correspondientes a la semana que inicia en el lunes dado.
     * @param lunes Fecha del lunes (YYYY-MM-DD).
     * @return Arreglo de 7 fechas ISO (de lunes a domingo).
     */
    public static String[] dias(String lunes) {
        String[] d = new String[7];
        for (int i = 0; i < 7; i++) {
            d[i] = sumarDias(lunes, i);
        }
        return d;
    }

    /**
     * Retorna una etiqueta legible de día y número (ej. "Lunes 26").
     * @param fecha Fecha ISO.
     * @return Cadena formateada de nombre de día y número.
     */
    public static String nombreDia(String fecha) {
        Calendar c = desdeIso(fecha);
        return DIAS[c.get(Calendar.DAY_OF_WEEK) - 1] + " " + c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Retorna una fecha corta legible en español (ej. "lunes 26 oct").
     * @param fecha Fecha ISO.
     * @return Cadena de fecha corta.
     */
    public static String fechaCorta(String fecha) {
        Calendar c = desdeIso(fecha);
        return DIAS[c.get(Calendar.DAY_OF_WEEK) - 1].toLowerCase(new Locale("es", "MX")) + " "
                + c.get(Calendar.DAY_OF_MONTH) + " " + MESES[c.get(Calendar.MONTH)];
    }

    /**
     * Genera la etiqueta del rango completo de la semana para el encabezado (ej. "26 oct – 1 nov 2026").
     * @param lunes Fecha ISO del lunes de la semana.
     * @return Etiqueta formateada del rango semanal.
     */
    public static String etiqueta(String lunes) {
        Calendar ini = desdeIso(lunes);
        Calendar fin = desdeIso(sumarDias(lunes, 6));
        return ini.get(Calendar.DAY_OF_MONTH) + " " + MESES[ini.get(Calendar.MONTH)] + " – "
                + fin.get(Calendar.DAY_OF_MONTH) + " " + MESES[fin.get(Calendar.MONTH)] + " "
                + fin.get(Calendar.YEAR);
    }
}
