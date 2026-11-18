package com.example.recetario.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.recetario.modelo.Categoria;
import com.example.recetario.modelo.IngredienteCatalogo;
import com.example.recetario.modelo.IngredienteReceta;
import com.example.recetario.modelo.Receta;
import com.example.recetario.util.Preferencias;

import java.util.ArrayList;
import java.util.List;

/** Consultas y cambios de recetas, ingredientes y favoritos. */
public class RecetaDao {

    private final RecetarioDbHelper helper;

    public RecetaDao(Context c) {
        helper = RecetarioDbHelper.get(c);
    }

    // ------------------------------------------------------------------ categorías

    public List<Categoria> categorias() {
        List<Categoria> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT id, nombre, color FROM categorias ORDER BY id", null)) {
            while (c.moveToNext()) {
                lista.add(new Categoria(c.getLong(0), c.getString(1), c.getString(2)));
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------ búsqueda

    private static final String SELECT_RESUMEN =
            "SELECT r.id, r.nombre, r.dificultad, r.tiempo_min, r.porciones, r.foto, r.creada_en, "
                    + "       c.id, c.nombre, c.color, "
                    + "       (f.receta_id IS NOT NULL) AS favorita, "
                    + "       (SELECT COUNT(*) FROM receta_ingrediente ri WHERE ri.receta_id = r.id) AS num_ing "
                    + "FROM recetas r "
                    + "JOIN categorias c ON c.id = r.categoria_id "
                    + "LEFT JOIN favoritos f ON f.receta_id = r.id ";

    /**
     * Lista de recetas con filtros. El texto busca en el nombre de la receta
     * y también en los nombres de sus ingredientes.
     */
    public List<Receta> buscar(String texto, long categoriaId, int dificultad, boolean soloFavoritas, int orden) {
        StringBuilder sql = new StringBuilder(SELECT_RESUMEN).append("WHERE 1 = 1 ");
        List<String> args = new ArrayList<>();
        if (categoriaId > 0) {
            sql.append("AND r.categoria_id = ? ");
            args.add(String.valueOf(categoriaId));
        }
        if (dificultad > 0) {
            sql.append("AND r.dificultad = ? ");
            args.add(String.valueOf(dificultad));
        }
        if (soloFavoritas) {
            sql.append("AND f.receta_id IS NOT NULL ");
        }
        if (texto != null && !texto.trim().isEmpty()) {
            String patron = "%" + escaparLike(texto.trim()) + "%";
            sql.append("AND (r.nombre LIKE ? ESCAPE '\\' OR EXISTS ("
                    + "SELECT 1 FROM receta_ingrediente ri "
                    + "JOIN ingredientes i ON i.id = ri.ingrediente_id "
                    + "WHERE ri.receta_id = r.id AND i.nombre LIKE ? ESCAPE '\\')) ");
            args.add(patron);
            args.add(patron);
        }
        switch (orden) {
            case Preferencias.ORDEN_RECIENTES:
                sql.append("ORDER BY r.creada_en DESC");
                break;
            case Preferencias.ORDEN_TIEMPO:
                sql.append("ORDER BY r.tiempo_min, r.nombre COLLATE NOCASE");
                break;
            default:
                sql.append("ORDER BY r.nombre COLLATE NOCASE");
        }

        List<Receta> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (c.moveToNext()) {
                lista.add(leerResumen(c));
            }
        }
        return lista;
    }

    private static String escaparLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static Receta leerResumen(Cursor c) {
        Receta r = new Receta();
        r.id = c.getLong(0);
        r.nombre = c.getString(1);
        r.dificultad = c.getInt(2);
        r.tiempoMin = c.getInt(3);
        r.porciones = c.getInt(4);
        r.foto = c.isNull(5) ? null : c.getString(5);
        r.creadaEn = c.getLong(6);
        r.categoriaId = c.getLong(7);
        r.categoria = c.getString(8);
        r.colorCategoria = c.getString(9);
        r.favorita = c.getInt(10) == 1;
        r.numIngredientes = c.getInt(11);
        return r;
    }

    // ------------------------------------------------------------------ detalle

    /** Receta completa con sus ingredientes (JOIN de 2 tablas). */
    public Receta obtener(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Receta r;
        try (Cursor c = db.rawQuery(SELECT_RESUMEN + "WHERE r.id = ?", new String[]{String.valueOf(id)})) {
            if (!c.moveToFirst()) {
                return null;
            }
            r = leerResumen(c);
        }
        try (Cursor c = db.rawQuery("SELECT preparacion FROM recetas WHERE id = ?",
                new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) {
                r.preparacion = c.getString(0);
            }
        }
        r.ingredientes = ingredientesDe(db, id);
        return r;
    }

    private List<IngredienteReceta> ingredientesDe(SQLiteDatabase db, long recetaId) {
        List<IngredienteReceta> lista = new ArrayList<>();
        try (Cursor c = db.rawQuery(
                "SELECT i.id, i.nombre, ri.cantidad, ri.unidad "
                        + "FROM receta_ingrediente ri "
                        + "JOIN ingredientes i ON i.id = ri.ingrediente_id "
                        + "WHERE ri.receta_id = ? "
                        + "ORDER BY ri.orden, ri.id",
                new String[]{String.valueOf(recetaId)})) {
            while (c.moveToNext()) {
                IngredienteReceta ir = new IngredienteReceta();
                ir.ingredienteId = c.getLong(0);
                ir.nombre = c.getString(1);
                ir.cantidad = c.getDouble(2);
                ir.unidad = c.getString(3);
                lista.add(ir);
            }
        }
        return lista;
    }

    /** Todas las recetas completas (para exportarlas). */
    public List<Receta> todasCompletas() {
        List<Receta> resumen = buscar(null, -1, 0, false, Preferencias.ORDEN_NOMBRE);
        List<Receta> completas = new ArrayList<>();
        for (Receta r : resumen) {
            completas.add(obtener(r.id));
        }
        return completas;
    }

    // ------------------------------------------------------------------ guardar / eliminar

    /**
     * Inserta o actualiza una receta junto con sus ingredientes en una sola transacción:
     * si algo falla no queda la receta a medias.
     */
    public long guardar(Receta r) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues v = new ContentValues();
            v.put("nombre", r.nombre);
            v.put("categoria_id", r.categoriaId);
            v.put("dificultad", r.dificultad);
            v.put("tiempo_min", r.tiempoMin);
            v.put("porciones", r.porciones);
            v.put("preparacion", r.preparacion);
            if (r.foto == null) {
                v.putNull("foto");
            } else {
                v.put("foto", r.foto);
            }

            long id = r.id;
            if (id <= 0) {
                v.put("creada_en", System.currentTimeMillis());
                id = db.insertOrThrow("recetas", null, v);
            } else {
                db.update("recetas", v, "id = ?", new String[]{String.valueOf(id)});
                db.delete("receta_ingrediente", "receta_id = ?", new String[]{String.valueOf(id)});
            }

            int orden = 0;
            for (IngredienteReceta ir : r.ingredientes) {
                ContentValues vi = new ContentValues();
                vi.put("receta_id", id);
                vi.put("ingrediente_id", obtenerOCrearIngrediente(db, ir.nombre));
                vi.put("cantidad", ir.cantidad);
                vi.put("unidad", ir.unidad);
                vi.put("orden", orden++);
                db.insertOrThrow("receta_ingrediente", null, vi);
            }

            borrarIngredientesSinUso(db);
            db.setTransactionSuccessful();
            r.id = id;
            return id;
        } finally {
            db.endTransaction();
        }
    }

    private long obtenerOCrearIngrediente(SQLiteDatabase db, String nombre) {
        String limpio = nombre.trim();
        // La columna tiene COLLATE NOCASE, así que "ajo" y "Ajo" son el mismo ingrediente
        try (Cursor c = db.rawQuery("SELECT id FROM ingredientes WHERE nombre = ?", new String[]{limpio})) {
            if (c.moveToFirst()) {
                return c.getLong(0);
            }
        }
        ContentValues v = new ContentValues();
        v.put("nombre", limpio);
        return db.insertOrThrow("ingredientes", null, v);
    }

    private void borrarIngredientesSinUso(SQLiteDatabase db) {
        db.execSQL("DELETE FROM ingredientes WHERE id NOT IN "
                + "(SELECT DISTINCT ingrediente_id FROM receta_ingrediente)");
    }

    /**
     * Borra la receta. Por las llaves foráneas con ON DELETE CASCADE también se borran
     * sus ingredientes, su favorito y sus comidas del planificador.
     * Regresa el nombre de la foto para que se borre el archivo.
     */
    public String eliminar(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String foto = null;
        db.beginTransaction();
        try {
            try (Cursor c = db.rawQuery("SELECT foto FROM recetas WHERE id = ?", new String[]{String.valueOf(id)})) {
                if (c.moveToFirst() && !c.isNull(0)) {
                    foto = c.getString(0);
                }
            }
            db.delete("recetas", "id = ?", new String[]{String.valueOf(id)});
            borrarIngredientesSinUso(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return foto;
    }

    // ------------------------------------------------------------------ favoritos

    /** Marca o desmarca la receta como favorita. Regresa el nuevo estado. */
    public boolean alternarFavorita(long recetaId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int borrados = db.delete("favoritos", "receta_id = ?", new String[]{String.valueOf(recetaId)});
        if (borrados > 0) {
            return false;
        }
        ContentValues v = new ContentValues();
        v.put("receta_id", recetaId);
        v.put("agregada_en", System.currentTimeMillis());
        db.insertOrThrow("favoritos", null, v);
        return true;
    }

    // ------------------------------------------------------------------ ingredientes

    public List<String> nombresIngredientes() {
        List<String> nombres = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT nombre FROM ingredientes ORDER BY nombre COLLATE NOCASE", null)) {
            while (c.moveToNext()) {
                nombres.add(c.getString(0));
            }
        }
        return nombres;
    }

    /** Catálogo de ingredientes con cuántas recetas usan cada uno (LEFT JOIN + GROUP BY). */
    public List<IngredienteCatalogo> catalogo(String filtro) {
        String sql = "SELECT i.id, i.nombre, COUNT(DISTINCT ri.receta_id) AS recetas "
                + "FROM ingredientes i "
                + "LEFT JOIN receta_ingrediente ri ON ri.ingrediente_id = i.id "
                + (filtro == null || filtro.trim().isEmpty() ? "" : "WHERE i.nombre LIKE ? ESCAPE '\\' ")
                + "GROUP BY i.id "
                + "ORDER BY i.nombre COLLATE NOCASE";
        String[] args = filtro == null || filtro.trim().isEmpty()
                ? null : new String[]{"%" + escaparLike(filtro.trim()) + "%"};
        List<IngredienteCatalogo> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                lista.add(new IngredienteCatalogo(c.getLong(0), c.getString(1), c.getInt(2)));
            }
        }
        return lista;
    }

    /** Recetas que llevan un ingrediente. */
    public List<Receta> recetasConIngrediente(long ingredienteId) {
        List<Receta> lista = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                SELECT_RESUMEN + "WHERE r.id IN (SELECT receta_id FROM receta_ingrediente WHERE ingrediente_id = ?) "
                        + "ORDER BY r.nombre COLLATE NOCASE",
                new String[]{String.valueOf(ingredienteId)})) {
            while (c.moveToNext()) {
                lista.add(leerResumen(c));
            }
        }
        return lista;
    }
}
