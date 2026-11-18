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

/**
 * Objeto de Acceso a Datos (DAO) para realizar consultas, inserciones, modificaciones
 * y eliminaciones en las tablas de recetas, categorías, ingredientes y favoritos.
 */
public class RecetaDao {

    private final RecetarioDbHelper helper;

    /**
     * Constructor del DAO que inicializa el ayudante de base de datos Singleton.
     * @param c Contexto de la aplicación.
     */
    public RecetaDao(Context c) {
        helper = RecetarioDbHelper.get(c);
    }

    // ------------------------------------------------------------------ categorías

    /**
     * Obtiene la lista completa de categorías de la base de datos ordenadas por ID.
     * @return Lista de objetos Categoria.
     */
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
     * Busca y filtra recetas en la base de datos según criterios de búsqueda textual, categoría, dificultad o favoritos.
     * El texto busca por coincidencia parcial tanto en el nombre de la receta como en sus ingredientes.
     * @param texto Texto de búsqueda (nombre o ingrediente).
     * @param categoriaId ID de la categoría seleccionada (-1 para todas).
     * @param dificultad Nivel de dificultad (0 para todas).
     * @param soloFavoritas Si es true, solo retorna recetas marcadas como favoritas.
     * @param orden Criterio de ordenamiento (nombre, fecha de creación o tiempo).
     * @return Lista de recetas filtradas.
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

    /**
     * Escapa caracteres especiales (%, _, \) para usar en cláusulas SQL LIKE.
     * @param s Cadena original.
     * @return Cadena escapada.
     */
    private static String escaparLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * Convierte la fila actual de un Cursor en un objeto Receta con sus datos básicos.
     * @param c Cursor posicionado en la fila actual.
     * @return Objeto Receta cargado.
     */
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

    /**
     * Obtiene una receta completa por su ID, incluyendo sus instrucciones de preparación e ingredientes asociados.
     * @param id ID de la receta.
     * @return Objeto Receta completo o null si no se encuentra.
     */
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

    /**
     * Obtiene la lista de ingredientes asociados a una receta desde la tabla intermedia receta_ingrediente.
     * @param db Instancia de la base de datos en modo lectura.
     * @param recetaId ID de la receta.
     * @return Lista de IngredienteReceta.
     */
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

    /**
     * Obtiene la lista completa de todas las recetas almacenadas con sus ingredientes e instrucciones (para exportar a JSON).
     * @return Lista de todas las recetas completas.
     */
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
     * Inserta o actualiza una receta junto con sus ingredientes dentro de una transacción atómica.
     * @param r Objeto Receta a guardar.
     * @return El ID generado o actualizado de la receta.
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

    /**
     * Busca un ingrediente por nombre o lo inserta en el catálogo si no existe (evita duplicados insensible a mayúsculas).
     * @param db Instancia de la base de datos en modo escritura.
     * @param nombre Nombre del ingrediente.
     * @return El ID del ingrediente existente o recién creado.
     */
    private long obtenerOCrearIngrediente(SQLiteDatabase db, String nombre) {
        String limpio = nombre.trim();
        try (Cursor c = db.rawQuery("SELECT id FROM ingredientes WHERE nombre = ?", new String[]{limpio})) {
            if (c.moveToFirst()) {
                return c.getLong(0);
            }
        }
        ContentValues v = new ContentValues();
        v.put("nombre", limpio);
        return db.insertOrThrow("ingredientes", null, v);
    }

    /**
     * Elimina del catálogo los ingredientes que no están asociados a ninguna receta.
     * @param db Instancia de la base de datos.
     */
    private void borrarIngredientesSinUso(SQLiteDatabase db) {
        db.execSQL("DELETE FROM ingredientes WHERE id NOT IN "
                + "(SELECT DISTINCT ingrediente_id FROM receta_ingrediente)");
    }

    /**
     * Elimina una receta de la base de datos.
     * Por las llaves foráneas con ON DELETE CASCADE, también elimina sus referencias en favoritos y planificador.
     * @param id ID de la receta a eliminar.
     * @return El nombre del archivo de foto asociado si existe, para eliminarlo del almacenamiento.
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

    /**
     * Marca o desmarca una receta como favorita.
     * @param recetaId ID de la receta.
     * @return true si la receta quedó marcada como favorita, false si fue quitada.
     */
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

    /**
     * Obtiene la lista completa de nombres de ingredientes únicos para el autocompletado en formularios.
     * @return Lista de cadenas con los nombres de ingredientes.
     */
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

    /**
     * Obtiene el catálogo de ingredientes indicando cuántas recetas registradas utilizan cada ingrediente.
     * @param filtro Texto opcional para filtrar los ingredientes por nombre.
     * @return Lista de objetos IngredienteCatalogo.
     */
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

    /**
     * Obtiene la lista de recetas que contienen un ingrediente específico por su ID.
     * @param ingredienteId ID del ingrediente a consultar.
     * @return Lista de recetas que usan dicho ingrediente.
     */
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
