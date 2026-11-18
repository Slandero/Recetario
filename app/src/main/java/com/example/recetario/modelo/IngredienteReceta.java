package com.example.recetario.modelo;

/** Un renglón de la tabla receta_ingrediente unido con el nombre del ingrediente. */
public class IngredienteReceta {

    public long ingredienteId;
    public String nombre;
    public double cantidad;
    public String unidad;

    public IngredienteReceta() {
    }

    public IngredienteReceta(String nombre, double cantidad, String unidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    /** Cantidad ajustada a otras porciones: cantidad × porciones / porcionesBase */
    public double cantidadPara(int porciones, int porcionesBase) {
        if (porcionesBase <= 0) {
            return cantidad;
        }
        return cantidad * porciones / porcionesBase;
    }
}
