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

    /**
     * Constructor que inicializa la referencia a SharedPreferences con el archivo 'recetario_prefs'.
     * @param contexto Contexto de la aplicación.
     */
    public Preferencias(Context contexto) {
        prefs = contexto.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    // ---- Porciones predeterminadas ----

    /**
     * Obtiene el valor global de porciones predeterminadas guardado (por defecto: 4).
     * @return El número de porciones predeterminadas.
     */
    public int getPorciones() {
        return prefs.getInt(PORCIONES, 4);
    }

    /**
     * Guarda el número de porciones predeterminadas, limitándolo al rango válido (1 a 20).
     * @param porciones Número de porciones a guardar.
     */
    public void setPorciones(int porciones) {
        int p = Math.max(PORCIONES_MIN, Math.min(PORCIONES_MAX, porciones));
        prefs.edit().putInt(PORCIONES, p).apply();
    }

    // ---- Modo oscuro ----

    /**
     * Indica si el tema de Modo Oscuro está activo en la aplicación.
     * @return true si el modo oscuro está activo, false de lo contrario.
     */
    public boolean isModoOscuro() {
        return prefs.getBoolean(MODO_OSCURO, false);
    }

    /**
     * Establece y guarda el estado del Modo Oscuro.
     * @param activo true para activar el modo oscuro, false para tema claro.
     */
    public void setModoOscuro(boolean activo) {
        prefs.edit().putBoolean(MODO_OSCURO, activo).apply();
    }

    // ---- Orden de la lista de recetas ----

    /**
     * Obtiene el criterio de ordenamiento guardado para la lista de recetas (0: Nombre, 1: Recientes, 2: Tiempo).
     * @return Código del orden seleccionado.
     */
    public int getOrden() {
        return prefs.getInt(ORDEN, ORDEN_NOMBRE);
    }

    /**
     * Guarda el criterio de ordenamiento seleccionado para la lista de recetas.
     * @param orden Código de ordenamiento (ORDEN_NOMBRE, ORDEN_RECIENTES o ORDEN_TIEMPO).
     */
    public void setOrden(int orden) {
        prefs.edit().putInt(ORDEN, orden).apply();
    }

    // ---- Filtros de la pantalla principal ----

    /**
     * Indica si la app debe recordar los filtros aplicados al cerrarse.
     * @return true si la opción está activada.
     */
    public boolean isRecordarFiltros() {
        return prefs.getBoolean(RECORDAR_FILTROS, true);
    }

    /**
     * Activa o desactiva la opción de recordar los últimos filtros aplicados.
     * @param recordar true para recordar filtros, false de lo contrario.
     */
    public void setRecordarFiltros(boolean recordar) {
        prefs.edit().putBoolean(RECORDAR_FILTROS, recordar).apply();
        if (!recordar) {
            limpiarFiltros();
        }
    }

    /**
     * Obtiene la categoría guardada en los filtros (-1 si no hay filtro de categoría).
     * @return ID de la categoría filtrada.
     */
    public long getFiltroCategoria() {
        return prefs.getLong(FILTRO_CATEGORIA, -1);
    }

    /**
     * Obtiene el nivel de dificultad guardado en los filtros (0 si no hay filtro de dificultad).
     * @return Código del filtro de dificultad.
     */
    public int getFiltroDificultad() {
        return prefs.getInt(FILTRO_DIFICULTAD, 0);
    }

    /**
     * Indica si el filtro de solo mostrar favoritas estaba activo.
     * @return true si solo se muestran favoritas.
     */
    public boolean isFiltroFavoritas() {
        return prefs.getBoolean(FILTRO_FAVORITAS, false);
    }

    /**
     * Guarda los filtros actuales aplicados en la pantalla principal.
     * @param categoriaId ID de la categoría.
     * @param dificultad Nivel de dificultad.
     * @param soloFavoritas Si solo se filtran recetas favoritas.
     */
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

    /**
     * Remueve los filtros guardados en SharedPreferences.
     */
    public void limpiarFiltros() {
        prefs.edit()
                .remove(FILTRO_CATEGORIA)
                .remove(FILTRO_DIFICULTAD)
                .remove(FILTRO_FAVORITAS)
                .apply();
    }

    // ---- Lista de ingredientes: qué ya se compró en cada semana ----

    /**
     * Obtiene el conjunto de identificadores de ingredientes tachación/comprados para una semana específica.
     * @param lunes Fecha de inicio de la semana (YYYY-MM-DD).
     * @return Conjunto de claves de ingredientes marcados como comprados.
     */
    public Set<String> getMarcados(String lunes) {
        return new HashSet<>(prefs.getStringSet(LISTA_MARCADOS + lunes, new HashSet<String>()));
    }

    /**
     * Guarda el conjunto de ingredientes comprados para una semana específica.
     * @param lunes Fecha de inicio de la semana (YYYY-MM-DD).
     * @param marcados Conjunto de claves de ingredientes a guardar.
     */
    public void setMarcados(String lunes, Set<String> marcados) {
        prefs.edit().putStringSet(LISTA_MARCADOS + lunes, new HashSet<>(marcados)).apply();
    }
}
