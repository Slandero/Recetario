package com.example.recetario.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import java.util.Locale;

/** Ayudas pequeñas para pintar vistas. */
public final class Vistas {

    private Vistas() {
    }

    public static GradientDrawable redondeado(int color, float radioPx) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(radioPx);
        d.setColor(color);
        return d;
    }

    public static int color(String hex, int porDefecto) {
        try {
            return Color.parseColor(hex);
        } catch (RuntimeException e) {
            return porDefecto;
        }
    }

    /** Primera letra del nombre para el avatar cuando la receta no tiene foto. */
    public static String inicial(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "?";
        }
        return nombre.trim().substring(0, 1).toUpperCase(new Locale("es", "MX"));
    }
}
