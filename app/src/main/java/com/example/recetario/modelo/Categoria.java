package com.example.recetario.modelo;

/** Tipo de comida: Desayunos, Entradas, Sopas y Caldos, Platos Fuertes, Bebidas, Postres. */
public class Categoria {

    public final long id;
    public final String nombre;
    public final String color;

    public Categoria(long id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
    }

    @Override
    public String toString() {
        // ArrayAdapter usa toString() para mostrarla en el Spinner
        return nombre;
    }
}
