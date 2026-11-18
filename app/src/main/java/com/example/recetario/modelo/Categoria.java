package com.example.recetario.modelo;

/** Tipo de comida: Desayunos, Entradas, Sopas y Caldos, Platos Fuertes, Bebidas, Postres. */
public class Categoria {

    public final long id;
    public final String nombre;
    public final String color;

    /**
     * Constructor para inicializar una categoría de receta.
     * @param id Identificador único de la categoría.
     * @param nombre Nombre descriptivo (ej. Desayunos, Postres).
     * @param color Código de color hexadecimal para la UI.
     */
    public Categoria(long id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
    }

    /**
     * Retorna el nombre de la categoría.
     * Es utilizado automáticamente por los componentes de la UI como el Spinner para mostrar el texto.
     * @return El nombre de la categoría.
     */
    @Override
    public String toString() {
        return nombre;
    }
}
