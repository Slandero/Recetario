package com.example.recetario.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recetario.R;
import com.example.recetario.util.ScriptSql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Crea y abre la base de datos SQLite "recetario.db".
 * El esquema y los datos iniciales están en res/raw/esquema.sql y res/raw/semilla.sql.
 */
public class RecetarioDbHelper extends SQLiteOpenHelper {

    public static final String NOMBRE_BD = "recetario.db";
    public static final int VERSION = 2;

    private static RecetarioDbHelper instancia;
    private final Context contexto;

    /**
     * Retorna la instancia única (Singleton) del Helper de la base de datos.
     * Previene la apertura concurrente de múltiples conexiones a la misma base de datos.
     * @param c Contexto de la aplicación.
     * @return Instancia única de RecetarioDbHelper.
     */
    public static synchronized RecetarioDbHelper get(Context c) {
        if (instancia == null) {
            instancia = new RecetarioDbHelper(c.getApplicationContext());
        }
        return instancia;
    }

    /**
     * Constructor privado para forzar el patrón Singleton.
     * @param contexto Contexto de la aplicación.
     */
    private RecetarioDbHelper(Context contexto) {
        super(contexto, NOMBRE_BD, null, VERSION);
        this.contexto = contexto;
    }

    /**
     * Habilita las restricciones de claves foráneas (Foreign Keys) en SQLite.
     * Requerido para soportar ON DELETE CASCADE y ON DELETE RESTRICT.
     * @param db Instancia de la base de datos SQLite.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Se ejecuta la primera vez que se crea la base de datos.
     * Carga el esquema SQL de las 6 tablas e inserta los datos iniciales de las recetas de semilla.
     * @param db Instancia de la base de datos SQLite recién creada.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ejecutarScript(db, R.raw.esquema);
        ejecutarScript(db, R.raw.semilla);
    }

    /**
     * Se ejecuta cuando se incrementa la versión de la base de datos.
     * Ejecuta la migración de esquemas sin perder los datos existentes del usuario.
     * @param db Instancia de la base de datos SQLite.
     * @param versionAnterior Número de versión previo.
     * @param versionNueva Número de versión nuevo.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        if (versionAnterior < 2) {
            // Migración de v1 a v2: permite múltiples platillos por tiempo de comida eliminando UNIQUE(fecha, tiempo)
            db.execSQL("CREATE TABLE IF NOT EXISTS plan_comidas_tmp ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "fecha TEXT NOT NULL, "
                    + "tiempo INTEGER NOT NULL CHECK (tiempo BETWEEN 0 AND 2), "
                    + "receta_id INTEGER NOT NULL REFERENCES recetas(id) ON DELETE CASCADE, "
                    + "porciones INTEGER NOT NULL CHECK (porciones > 0));");
            db.execSQL("INSERT INTO plan_comidas_tmp SELECT id, fecha, tiempo, receta_id, porciones FROM plan_comidas;");
            db.execSQL("DROP TABLE plan_comidas;");
            db.execSQL("ALTER TABLE plan_comidas_tmp RENAME TO plan_comidas;");
            db.execSQL("CREATE INDEX idx_plan_fecha ON plan_comidas(fecha);");
        }
    }

    /**
     * Lee un archivo .sql de la carpeta res/raw, lo divide en sentencias individuales y las ejecuta.
     * @param db Instancia de la base de datos donde se ejecutarán las sentencias.
     * @param recursoRaw Identificador del recurso raw (ej. R.raw.esquema).
     */
    private void ejecutarScript(SQLiteDatabase db, int recursoRaw) {
        for (String sentencia : ScriptSql.dividir(leerRaw(recursoRaw))) {
            db.execSQL(sentencia);
        }
    }

    /**
     * Lee el contenido completo de un archivo en res/raw y lo convierte a una cadena UTF-8.
     * @param recursoRaw Identificador del recurso en res/raw.
     * @return El contenido en texto plano del archivo de recurso.
     */
    private String leerRaw(int recursoRaw) {
        try (InputStream in = contexto.getResources().openRawResource(recursoRaw);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int leidos;
            while ((leidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el script SQL", e);
        }
    }
}
