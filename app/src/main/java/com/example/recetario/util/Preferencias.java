package com.example.recetario.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Persistencia con SharedPreferences: porciones predeterminadas, filtros,
 * orden de la lista y modo oscuro. Se guardan en
 * /data/data/com.example.recetario/shared_prefs/recetario_prefs.xml
 */
public class Preferencias {

    private static final String ARCHIVO = "recetario_prefs";

    private static final String PORCIONES = "porciones_predeterminadas";
    private static final String MODO_OSCURO = "modo_oscuro";
    private static final String ORDEN = "orden";
    private static final String RECORDAR_FILTROS = "recordar_filtros";
    private static final String FILTRO_CATEGORIA = "filtro_categoria";
    private static final String FILTRO_DIFICULTAD = "filtro_dificultad";
    private static final String FILTRO_FAVORITAS = "filtro_favoritas";
    private static final String LISTA_MARCADOS = "lista_marcados_";

    public static final int ORDEN_NOMBRE = 0;
    public static final int ORDEN_RECIENTES = 1;
    public static final int ORDEN_TIEMPO = 2;

    public static final int PORCIONES_MIN = 1;
    public static final int PORCIONES_MAX = 20;

    private final SharedPreferences prefs;

    public Preferencias(Context contexto) {
        prefs = contexto.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    // ---- Porciones predeterminadas ----
    public int getPorciones() {
        return prefs.getInt(PORCIONES, 4);
    }

    public void setPorciones(int porciones) {
        int p = Math.max(PORCIONES_MIN, Math.min(PORCIONES_MAX, porciones));
        prefs.edit().putInt(PORCIONES, p).apply();
    }

    // ---- Modo oscuro ----
    public boolean isModoOscuro() {
        return prefs.getBoolean(MODO_OSCURO, false);
    }

    public void setModoOscuro(boolean activo) {
        prefs.edit().putBoolean(MODO_OSCURO, activo).apply();
    }

    // ---- Orden de la lista de recetas ----
    public int getOrden() {
        return prefs.getInt(ORDEN, ORDEN_NOMBRE);
    }

    public void setOrden(int orden) {
        prefs.edit().putInt(ORDEN, orden).apply();
    }

    // ---- Filtros de la pantalla principal ----
    public boolean isRecordarFiltros() {
        return prefs.getBoolean(RECORDAR_FILTROS, true);
    }

    public void setRecordarFiltros(boolean recordar) {
        prefs.edit().putBoolean(RECORDAR_FILTROS, recordar).apply();
        if (!recordar) {
            limpiarFiltros();
        }
    }

    public long getFiltroCategoria() {
        return prefs.getLong(FILTRO_CATEGORIA, -1);
    }

    public int getFiltroDificultad() {
        return prefs.getInt(FILTRO_DIFICULTAD, 0);
    }

    public boolean isFiltroFavoritas() {
        return prefs.getBoolean(FILTRO_FAVORITAS, false);
    }

    public void guardarFiltros(long categoriaId, int dificultad, boolean soloFavoritas) {
        if (!isRecordarFiltros()) {
            return;
        }
        prefs.edit()
                .putLong(FILTRO_CATEGORIA, categoriaId)
                .putInt(FILTRO_DIFICULTAD, dificultad)
                .putBoolean(FILTRO_FAVORITAS, soloFavoritas)
                .apply();
    }

    public void limpiarFiltros() {
        prefs.edit()
                .remove(FILTRO_CATEGORIA)
                .remove(FILTRO_DIFICULTAD)
                .remove(FILTRO_FAVORITAS)
                .apply();
    }

    // ---- Lista de ingredientes: qué ya se compró en cada semana ----
    public Set<String> getMarcados(String lunes) {
        // getStringSet regresa un Set que no se debe modificar, por eso se copia
        return new HashSet<>(prefs.getStringSet(LISTA_MARCADOS + lunes, new HashSet<String>()));
    }

    public void setMarcados(String lunes, Set<String> marcados) {
        prefs.edit().putStringSet(LISTA_MARCADOS + lunes, new HashSet<>(marcados)).apply();
    }
}
