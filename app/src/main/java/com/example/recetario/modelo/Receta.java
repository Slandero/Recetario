package com.example.recetario.modelo;

import java.util.ArrayList;
import java.util.List;

/** Una receta con su categoría, sus ingredientes y si es favorita. */
public class Receta {

    public static final int FACIL = 1;
    public static final int MEDIA = 2;
    public static final int DIFICIL = 3;

    public long id;
    public String nombre = "";
    public long categoriaId;
    public String categoria = "";
    public String colorCategoria = "#5B4F8F";
    public int dificultad = FACIL;
    public int tiempoMin;
    public int porciones = 4;
    public String preparacion = "";
    public String foto;
    public boolean favorita;
    public long creadaEn;
    public int numIngredientes;
    public List<IngredienteReceta> ingredientes = new ArrayList<>();

    public static String textoDificultad(int dificultad) {
        switch (dificultad) {
            case FACIL:
                return "Fácil";
            case MEDIA:
                return "Media";
            case DIFICIL:
                return "Difícil";
            default:
                return "";
        }
    }

    /** Cada renglón de la preparación es un paso. */
    public List<String> pasos() {
        List<String> pasos = new ArrayList<>();
        if (preparacion != null) {
            for (String linea : preparacion.split("\n")) {
                String l = linea.trim();
                if (!l.isEmpty()) {
                    pasos.add(l);
                }
            }
        }
        return pasos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
