package com.example.recetario.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class ScriptSqlTest {

    @Test
    public void respetaTextosYComentarios() {
        String sql = "-- comentario; con punto y coma\n"
                + "CREATE TABLE t (a TEXT);\n"
                + "INSERT INTO t VALUES ('uno; dos');\n"
                + "INSERT INTO t VALUES ('l''apostrofe -- no es comentario');\n"
                + "INSERT INTO t VALUES ('renglón 1\nrenglón 2')";
        List<String> s = ScriptSql.dividir(sql);
        assertEquals(4, s.size());
        assertEquals("INSERT INTO t VALUES ('uno; dos')", s.get(1));
        assertEquals("INSERT INTO t VALUES ('l''apostrofe -- no es comentario')", s.get(2));
        assertEquals("INSERT INTO t VALUES ('renglón 1\nrenglón 2')", s.get(3));
    }
}
