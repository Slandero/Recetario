package com.example.recetario.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.recetario.modelo.ComidaPlan;
import com.example.recetario.util.ListaCompras;
import com.example.recetario.util.Semana;

import java.util.ArrayList;
import java.util.List;

/** Planificador semanal y lista de ingredientes. */
public class PlanDao {

    private final RecetarioDbHelper helper;

    public PlanDao(Context c) {
        helper = RecetarioDbHelper.get(c);
    }

    /** Comidas de la semana que empieza en lunes. */
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

    /** Nombres de las recetas agregadas en cada tiempo del día (null si no hay ninguna). */
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

    /** Agrega una receta/platillo/bebida a un día y tiempo de comida. */
    public void asignar(String fecha, int tiempo, long recetaId, int porciones) {
        ContentValues v = new ContentValues();
        v.put("fecha", fecha);
        v.put("tiempo", tiempo);
        v.put("receta_id", recetaId);
        v.put("porciones", porciones);
        helper.getWritableDatabase().insertOrThrow("plan_comidas", null, v);
    }

    /** Reemplaza la receta de una entrada existente del planificador. */
    public void cambiarReceta(long id, long recetaId) {
        ContentValues v = new ContentValues();
        v.put("receta_id", recetaId);
        helper.getWritableDatabase().update("plan_comidas", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void cambiarPorciones(long id, int porciones) {
        ContentValues v = new ContentValues();
        v.put("porciones", porciones);
        helper.getWritableDatabase().update("plan_comidas", v, "id = ?", new String[]{String.valueOf(id)});
    }

    public void quitar(long id) {
        helper.getWritableDatabase().delete("plan_comidas", "id = ?", new String[]{String.valueOf(id)});
    }

    public int limpiarSemana(String lunes) {
        return helper.getWritableDatabase().delete("plan_comidas", "fecha BETWEEN ? AND ?",
                new String[]{lunes, Semana.sumarDias(lunes, 6)});
    }

    /**
     * Ingredientes de todas las comidas de la semana (JOIN de 4 tablas).
     * Cada cantidad se ajusta a las porciones planeadas:
     * cantidad × porciones del plan / porciones de la receta
     * y se suma por ingrediente y unidad.
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
