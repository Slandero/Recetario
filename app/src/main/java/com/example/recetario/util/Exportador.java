package com.example.recetario.util;

import android.content.Context;
import android.net.Uri;

import com.example.recetario.modelo.IngredienteReceta;
import com.example.recetario.modelo.Receta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Persistencia con archivos: exportar recetas y la lista de ingredientes. */
public final class Exportador {

    private Exportador() {
    }

    /** Receta en texto plano, ajustada a las porciones elegidas. */
    public static String recetaTexto(Receta r, int porciones) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.nombre.toUpperCase(new java.util.Locale("es", "MX"))).append('\n');
        sb.append(r.categoria).append(" · ").append(Receta.textoDificultad(r.dificultad))
                .append(" · ").append(Formato.tiempo(r.tiempoMin)).append('\n');
        sb.append("Porciones: ").append(porciones).append("\n\n");
        sb.append("INGREDIENTES\n");
        for (IngredienteReceta ir : r.ingredientes) {
            sb.append("- ").append(Formato.cantidad(ir.cantidadPara(porciones, r.porciones), ir.unidad))
                    .append("  ").append(ir.nombre).append('\n');
        }
        sb.append("\nPREPARACIÓN\n");
        int n = 1;
        for (String paso : r.pasos()) {
            sb.append(n++).append(". ").append(paso).append('\n');
        }
        sb.append("\n— Recetario México Típico\n");
        return sb.toString();
    }

    /** Lista de ingredientes en texto plano. */
    public static String listaTexto(String titulo, List<ListaCompras.Item> items, java.util.Set<String> marcados) {
        StringBuilder sb = new StringBuilder();
        sb.append("LISTA DE INGREDIENTES\n").append(titulo).append("\n\n");
        for (ListaCompras.Item it : items) {
            sb.append(marcados.contains(it.clave()) ? "[x] " : "[ ] ")
                    .append(it.ingrediente).append(" — ").append(it.cantidades).append('\n');
        }
        sb.append("\n— Recetario México Típico\n");
        return sb.toString();
    }

    /** Todas las recetas en JSON (sirve como respaldo). */
    public static String recetasJson(List<Receta> recetas) throws JSONException {
        JSONArray arreglo = new JSONArray();
        for (Receta r : recetas) {
            JSONObject o = new JSONObject();
            o.put("nombre", r.nombre);
            o.put("categoria", r.categoria);
            o.put("dificultad", Receta.textoDificultad(r.dificultad));
            o.put("tiempo_min", r.tiempoMin);
            o.put("porciones", r.porciones);
            o.put("favorita", r.favorita);
            o.put("foto", r.foto == null ? JSONObject.NULL : r.foto);
            JSONArray ings = new JSONArray();
            for (IngredienteReceta ir : r.ingredientes) {
                JSONObject oi = new JSONObject();
                oi.put("nombre", ir.nombre);
                oi.put("cantidad", ir.cantidad);
                oi.put("unidad", ir.unidad);
                ings.put(oi);
            }
            o.put("ingredientes", ings);
            JSONArray pasos = new JSONArray();
            for (String p : r.pasos()) {
                pasos.put(p);
            }
            o.put("preparacion", pasos);
            arreglo.put(o);
        }
        JSONObject raiz = new JSONObject();
        raiz.put("app", "Recetario México Típico");
        raiz.put("exportado", System.currentTimeMillis());
        raiz.put("total", recetas.size());
        raiz.put("recetas", arreglo);
        return raiz.toString(2);
    }

    /** Escribe el texto en el archivo que el usuario eligió (Storage Access Framework). */
    public static void escribir(Context c, Uri destino, String contenido) throws IOException {
        // El archivo lo acaba de crear ACTION_CREATE_DOCUMENT, así que viene vacío
        OutputStream out = c.getContentResolver().openOutputStream(destino);
        if (out == null) {
            throw new IOException("No se pudo abrir el archivo de destino");
        }
        try (Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            w.write(contenido);
        }
    }
}
