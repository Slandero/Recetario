package com.example.recetario;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recetario.util.Preferencias;

/**
 * Base de todas las pantallas: aplica el tema claro u oscuro según la preferencia
 * guardada y vuelve a crear la pantalla si el usuario lo cambió en Ajustes.
 */
public abstract class BaseActivity extends Activity {

    protected Preferencias prefs;
    private boolean oscuroAplicado;

    /**
     * Inicializa la actividad aplicando la preferencia del tema (claro u oscuro) antes del layout.
     * Habilita el botón de navegación hacia atrás en la barra de acción si la subclase lo permite.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new Preferencias(this);
        oscuroAplicado = prefs.isModoOscuro();
        setTheme(oscuroAplicado ? R.style.Tema_Recetario_Oscuro : R.style.Tema_Recetario);
        super.onCreate(savedInstanceState);
        ActionBar barra = getActionBar();
        if (barra != null && mostrarRegresar()) {
            barra.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Verifica si la preferencia de tema cambió mientras la pantalla estaba en segundo plano.
     * Si cambió, recrea la actividad para aplicar los nuevos colores.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.isModoOscuro() != oscuroAplicado) {
            recreate();
        }
    }

    /**
     * Establece la vista de contenido e indica fitsSystemWindows en la vista raíz
     * para asegurar el comportamiento Edge-to-Edge adecuado en Android 15+.
     * @param layoutResID Identificador del recurso de diseño layout.
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ViewGroup contenido = (ViewGroup) findViewById(android.R.id.content);
        View raiz = contenido.getChildAt(0);
        if (raiz != null) {
            raiz.setFitsSystemWindows(true);
        }
    }

    /**
     * Determina si se debe mostrar la flecha de regreso en la barra de acción.
     * Las subclases pueden sobrescribirlo (ej. MainActivity retorna false).
     * @return true si se muestra la flecha de regreso.
     */
    protected boolean mostrarRegresar() {
        return true;
    }

    /**
     * Maneja la pulsación de opciones de menú. Cierra la actividad si se presiona el botón Home/Atrás.
     * @param item Elemento de menú seleccionado.
     * @return true si el evento fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Muestra un mensaje flotante Toast breve al usuario.
     * @param mensaje Texto del mensaje.
     */
    protected void aviso(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    /**
     * Convierte un valor en densidad de píxeles independientes (dp) a píxeles reales (px).
     * @param valor Valor en dp.
     * @return Equivalente en píxeles.
     */
    protected int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    /**
     * Obtiene el valor entero de un color definido en el tema actual (ej. colorTarjeta).
     * @param atributo Atributo de recurso del tema (ej. R.attr.colorTarjeta).
     * @return Color entero parseado.
     */
    protected int colorDelTema(int atributo) {
        TypedArray a = obtainStyledAttributes(new int[]{atributo});
        try {
            return a.getColor(0, 0xFF5B4F8F);
        } finally {
            a.recycle();
        }
    }
}
