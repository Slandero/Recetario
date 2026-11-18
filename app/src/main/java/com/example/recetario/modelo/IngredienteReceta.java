package com.example.recetario.modelo;

/** Un renglón de la tabla receta_ingrediente unido con el nombre del ingrediente. */
public class IngredienteReceta {

    public long ingredienteId;
    public String nombre;
    public double cantidad;
    public String unidad;

    /**
     * Constructor por defecto para la instanciación vacía.
     */
    public IngredienteReceta() {
    }

    /**
     * Constructor parametrizado para instanciar un ingrediente en el formulario o detalle.
     * @param nombre Nombre del ingrediente.
     * @param cantidad Cantidad requerida.
     * @param unidad Unidad de medida (ej. g, kg, taza, pz).
     */
    public IngredienteReceta(String nombre, double cantidad, String unidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    /**
     * Calcula y retorna la cantidad del ingrediente ajustada proporcionalmente a un número nuevo de porciones.
     * Fórmula: cantidad × porcionesNuevas / porcionesBase
     * @param porciones Número de porciones al que se desea escalar la receta.
     * @param porcionesBase Porciones originales definidas en la receta base.
     * @return La cantidad ajustada proporcionalmente.
     */
    public double cantidadPara(int porciones, int porcionesBase) {
        if (porcionesBase <= 0) {
            return cantidad;
        }
        return cantidad * porciones / porcionesBase;
    }
}
