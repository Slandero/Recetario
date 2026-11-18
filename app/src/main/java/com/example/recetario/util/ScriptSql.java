package com.example.recetario.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Divide un script .sql en sentencias para ejecutarlas una por una con execSQL().
 * Respeta los textos entre comillas simples (pueden traer ; o saltos de línea)
 * e ignora los comentarios que empiezan con --.
 */
public final class ScriptSql {

    private ScriptSql() {
    }

    public static List<String> dividir(String script) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enTexto = false;
        int i = 0;
        int n = script.length();
        while (i < n) {
            char c = script.charAt(i);
            if (!enTexto && c == '-' && i + 1 < n && script.charAt(i + 1) == '-') {
                // Comentario: brinca hasta el final del renglón
                while (i < n && script.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }
            if (c == '\'') {
                // '' dentro de un texto abre y cierra, así que el estado queda igual
                enTexto = !enTexto;
            }
            if (c == ';' && !enTexto) {
                agregar(sentencias, actual);
                actual.setLength(0);
            } else {
                actual.append(c);
            }
            i++;
        }
        agregar(sentencias, actual);
        return sentencias;
    }

    private static void agregar(List<String> sentencias, StringBuilder sb) {
        String s = sb.toString().trim();
        if (!s.isEmpty()) {
            sentencias.add(s);
        }
    }
}
