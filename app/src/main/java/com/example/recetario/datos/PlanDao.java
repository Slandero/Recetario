package com.example.recetario.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.recetario.modelo.ComidaPlan;
import com.example.recetario.util.ListaCompras;
import com.example.recetario.util.Semana;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acceso a Datos (DAO) para gestionar las operaciones del planificador semanal
 * y el cálculo de la lista de compras de la semana.
 */
public class PlanDao {

    private final RecetarioDbHelper helper;

    /**
     * Constructor del DAO que obtiene la instancia Singleton del Helper de la BD.
     * @param c Contexto de la aplicación.
     */
    public PlanDao(Context c) {
        helper = RecetarioDbHelper.get(c);
    }

    /**
     * Obtiene la lista de platillos y bebidas agendados para la semana que inicia en la fecha lunes dada.
     * @param lunes Fecha de inicio de la semana en formato YYYY-MM-DD.
     * @return Lista de objetos ComidaPlan ordenada por fecha y tiempo de comida.
     */
    public List<ComidaPlan> semana(String lunes) {
        String domingo = Semana.sumarDias(lunes, 6);
        List<ComidaPlan> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT p.id, p.fecha, p.tiempo, p.receta_id, r.nombre, cat.color, p.porciones "
                        + "FROM plan_comidas p "
                        + "JOIN recetas r ON r.id = p.receta_id "
                        + "JOIN categorias cat ON cat.id = r.categoria_id "
                        + "WHERE p.fecha BETWEEN ? AND ? "
                        + "ORDER BY p.fecha, p.tiempo",
                new String[]{lunes, domingo})) {
            while (c.moveToNext()) {
                ComidaPlan cp = new ComidaPlan();
                cp.id = c.getLong(0);
                cp.fecha = c.getString(1);
                cp.tiempo = c.getInt(2);
                cp.recetaId = c.getLong(3);
                cp.recetaNombre = c.getString(4);
                cp.colorCategoria = c.getString(5);
                cp.porciones = c.getInt(6);
                lista.add(cp);
            }
        }
        return lista;
    }

    /**
     * Obtiene una representación textual de las recetas asignadas en cada tiempo de un día específico.
     * Concatenadas por comas si hay múltiples recetas en el mismo tiempo.
     * @param fecha Fecha en formato YYYY-MM-DD.
     * @return Arreglo de 3 cadenas (índice 0 = Desayuno, 1 = Comida, 2 = Cena).
     */
    public String[] recetasDelDia(String fecha) {
        String[] nombres = new String[3];
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT p.tiempo, r.nombre FROM plan_comidas p JOIN recetas r ON r.id = p.receta_id WHERE p.fecha = ?",
                new String[]{fecha})) {
            while (c.moveToNext()) {
                int t = c.getInt(0);
                if (t >= 0 && t < 3) {
                    if (nombres[t] == null) {
                        nombres[t] = c.getString(1);
                    } else {
                        nombres[t] += ", " + c.getString(1);
                    }
                }
            }
        }
        return nombres;
    }

    /**
     * Agrega una receta/platillo/bebida al planificador semanal para una fecha y tiempo específicos.
     * @param fecha Fecha de la comida (YYYY-MM-DD).
     * @param tiempo Tiempo de comida (0 = Desayuno, 1 = Comida, 2 = Cena).
     * @param recetaId ID de la receta a agregar.
     * @param porciones Número de porciones planificadas.
     */
    public void asignar(String fecha, int tiempo, long recetaId, int porciones) {
        ContentValues v = new ContentValues();
        v.put("fecha", fecha);
        v.put("tiempo", tiempo);
        v.put("receta_id", recetaId);
        v.put("porciones", porciones);
        helper.getWritableDatabase().insertOrThrow("plan_comidas", null, v);
    }

    /**
     * Modifica la receta asignada en una entrada existente del planificador.
     * @param id ID de la entrada en plan_comidas.
     * @param recetaId Nuevo ID de la receta a asociar.
     */
    public void cambiarReceta(long id, long recetaId) {
        ContentValues v = new ContentValues();
        v.put("receta_id", recetaId);
        helper.getWritableDatabase().update("plan_comidas", v, "id = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Modifica el número de porciones de una entrada existente del planificador.
     * @param id ID de la entrada en plan_comidas.
     * @param porciones Nuevo número de porciones.
     */
    public void cambiarPorciones(long id, int porciones) {
        ContentValues v = new ContentValues();
        v.put("porciones", porciones);
        helper.getWritableDatabase().update("plan_comidas", v, "id = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Elimina una comida específica del planificador por su ID.
     * @param id ID de la entrada en plan_comidas.
     */
    public void quitar(long id) {
        helper.getWritableDatabase().delete("plan_comidas", "id = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Elimina todas las comidas asignadas en la semana que inicia en el lunes dado.
     * @param lunes Fecha de inicio de la semana (YYYY-MM-DD).
     * @return El número de registros eliminados.
     */
    public int limpiarSemana(String lunes) {
        return helper.getWritableDatabase().delete("plan_comidas", "fecha BETWEEN ? AND ?",
                new String[]{lunes, Semana.sumarDias(lunes, 6)});
    }

    /**
     * Calcula y suma los ingredientes requeridos para todas las comidas agendadas en la semana dada.
     * Realiza un JOIN entre plan_comidas, recetas, receta_ingrediente e ingredientes, escalando
     * la cantidad según porciones planificadas vs porciones base de cada receta.
     * @param lunes Fecha de inicio de la semana (YYYY-MM-DD).
     * @return Lista de objetos Fila con el total acumulado por ingrediente y unidad.
     */
    public List<ListaCompras.Fila> ingredientesSemana(String lunes) {
        String domingo = Semana.sumarDias(lunes, 6);
        List<ListaCompras.Fila> filas = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT i.nombre, ri.unidad, "
                        + "       SUM(ri.cantidad * p.porciones * 1.0 / r.porciones) AS total, "
                        + "       GROUP_CONCAT(DISTINCT r.nombre) AS recetas "
                        + "FROM plan_comidas p "
                        + "JOIN recetas r ON r.id = p.receta_id "
                        + "JOIN receta_ingrediente ri ON ri.receta_id = r.id "
                        + "JOIN ingredientes i ON i.id = ri.ingrediente_id "
                        + "WHERE p.fecha BETWEEN ? AND ? "
                        + "GROUP BY i.id, ri.unidad",
                new String[]{lunes, domingo})) {
            while (c.moveToNext()) {
                filas.add(new ListaCompras.Fila(c.getString(0), c.getString(1), c.getDouble(2), c.getString(3)));
            }
        }
        return filas;
    }
}
