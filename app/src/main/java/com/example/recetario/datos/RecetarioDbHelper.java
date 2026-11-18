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

    /** Una sola instancia para toda la app (evita abrir la BD varias veces). */
    public static synchronized RecetarioDbHelper get(Context c) {
        if (instancia == null) {
            instancia = new RecetarioDbHelper(c.getApplicationContext());
        }
        return instancia;
    }

    private RecetarioDbHelper(Context contexto) {
        super(contexto, NOMBRE_BD, null, VERSION);
        this.contexto = contexto;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // SQLite trae las llaves foráneas apagadas; sin esto no funcionan ON DELETE CASCADE ni RESTRICT
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate ya corre dentro de una transacción
        ejecutarScript(db, R.raw.esquema);
        ejecutarScript(db, R.raw.semilla);
    }

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

    private void ejecutarScript(SQLiteDatabase db, int recursoRaw) {
        for (String sentencia : ScriptSql.dividir(leerRaw(recursoRaw))) {
            db.execSQL(sentencia);
        }
    }

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
