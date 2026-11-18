package com.example.recetario.modelo;

/** Una comida del planificador: día + tiempo (desayuno, comida o cena) + receta. */
public class ComidaPlan {

    public static final int DESAYUNO = 0;
    public static final int COMIDA = 1;
    public static final int CENA = 2;
    public static final String[] TIEMPOS = {"Desayuno", "Comida", "Cena"};

    public long id;
    public String fecha;
    public int tiempo;
    public long recetaId;
    public String recetaNombre;
    public String colorCategoria;
    public int porciones;
}
