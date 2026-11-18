package com.example.recetario.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import java.util.Locale;

/** Ayudas pequeñas para pintar vistas. */
public final class Vistas {

    private Vistas() {
    }

    /**
     * Genera un Drawable rectangular con esquinas redondeadas y un color de fondo plano.
     * @param color Color del Drawable.
     * @param radioPx Radio de las esquinas en píxeles.
     * @return Objeto GradientDrawable configurado.
     */
    public static GradientDrawable redondeado(int color, float radioPx) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(radioPx);
        d.setColor(color);
        return d;
    }

    /**
     * Parsea un código de color hexadecimal. Si el formato es inválido, retorna el color por defecto.
     * @param hex Código hexadecimal de color (ej. "#FF5722").
     * @param porDefecto Color entero alternativo.
     * @return Color parseado.
     */
    public static int color(String hex, int porDefecto) {
        try {
            return Color.parseColor(hex);
        } catch (RuntimeException e) {
            return porDefecto;
        }
    }

    /**
     * Obtiene la primera letra del nombre en mayúscula para usarse como avatar por defecto.
     * @param nombre Nombre de la receta.
     * @return Primera letra o '?' si el nombre está vacío.
     */
    public static String inicial(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "?";
        }
        return nombre.trim().substring(0, 1).toUpperCase(new Locale("es", "MX"));
    }
}
