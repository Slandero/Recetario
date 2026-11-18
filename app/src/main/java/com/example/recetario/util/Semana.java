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

    private static SimpleDateFormat iso() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        f.setLenient(false);
        return f;
    }

    public static String hoy() {
        return aIso(Calendar.getInstance());
    }

    public static String aIso(Calendar c) {
        return iso().format(c.getTime());
    }

    public static Calendar desdeIso(String fecha) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(iso().parse(fecha));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Fecha inválida: " + fecha, e);
        }
        return c;
    }

    public static String sumarDias(String fecha, int dias) {
        Calendar c = desdeIso(fecha);
        c.add(Calendar.DAY_OF_MONTH, dias);
        return aIso(c);
    }

    /** Lunes de la semana a la que pertenece la fecha. */
    public static String lunesDe(String fecha) {
        Calendar c = desdeIso(fecha);
        int diaSemana = c.get(Calendar.DAY_OF_WEEK); // domingo = 1 ... sábado = 7
        int retroceso = (diaSemana + 5) % 7;          // lunes -> 0, domingo -> 6
        c.add(Calendar.DAY_OF_MONTH, -retroceso);
        return aIso(c);
    }

    /** Los 7 días (lunes a domingo) de la semana que empieza en lunes. */
    public static String[] dias(String lunes) {
        String[] d = new String[7];
        for (int i = 0; i < 7; i++) {
            d[i] = sumarDias(lunes, i);
        }
        return d;
    }

    /** "Lunes 26" */
    public static String nombreDia(String fecha) {
        Calendar c = desdeIso(fecha);
        return DIAS[c.get(Calendar.DAY_OF_WEEK) - 1] + " " + c.get(Calendar.DAY_OF_MONTH);
    }

    /** "lunes 26 oct" */
    public static String fechaCorta(String fecha) {
        Calendar c = desdeIso(fecha);
        return DIAS[c.get(Calendar.DAY_OF_WEEK) - 1].toLowerCase(new Locale("es", "MX")) + " "
                + c.get(Calendar.DAY_OF_MONTH) + " " + MESES[c.get(Calendar.MONTH)];
    }

    /** "26 oct – 1 nov 2026" */
    public static String etiqueta(String lunes) {
        Calendar ini = desdeIso(lunes);
        Calendar fin = desdeIso(sumarDias(lunes, 6));
        return ini.get(Calendar.DAY_OF_MONTH) + " " + MESES[ini.get(Calendar.MONTH)] + " – "
                + fin.get(Calendar.DAY_OF_MONTH) + " " + MESES[fin.get(Calendar.MONTH)] + " "
                + fin.get(Calendar.YEAR);
    }
}
