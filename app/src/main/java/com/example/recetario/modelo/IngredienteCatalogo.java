package com.example.recetario.modelo;

/** Un ingrediente del catálogo con el número de recetas que lo usan. */
public class IngredienteCatalogo {

    public final long id;
    public final String nombre;
    public final int numRecetas;

    /**
     * Constructor para inicializar un elemento del catálogo de ingredientes.
     * @param id Identificador del ingrediente en la BD.
     * @param nombre Nombre del ingrediente.
     * @param numRecetas Conteo de recetas registradas que incluyen este ingrediente.
     */
    public IngredienteCatalogo(long id, String nombre, int numRecetas) {
        this.id = id;
        this.nombre = nombre;
        this.numRecetas = numRecetas;
    }
}
