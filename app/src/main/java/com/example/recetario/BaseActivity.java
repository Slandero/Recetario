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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new Preferencias(this);
        oscuroAplicado = prefs.isModoOscuro();
        // El tema se tiene que poner antes de super.onCreate() y setContentView()
        setTheme(oscuroAplicado ? R.style.Tema_Recetario_Oscuro : R.style.Tema_Recetario);
        super.onCreate(savedInstanceState);
        ActionBar barra = getActionBar();
        if (barra != null && mostrarRegresar()) {
            barra.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.isModoOscuro() != oscuroAplicado) {
            recreate();
        }
    }

    /**
     * Desde Android 15 (targetSdk 35 o más) la ventana se dibuja de orilla a orilla y el contenido
     * queda debajo de la barra de acción y de la barra de navegación. Con fitsSystemWindows la vista
     * raíz de cada pantalla recibe ese espacio como padding (y también el del teclado).
     * En versiones anteriores el sistema ya acomoda el contenido y el padding queda en 0.
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

    /** La pantalla principal no muestra la flecha de regresar. */
    protected boolean mostrarRegresar() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void aviso(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    protected int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    /** Lee un color del tema actual (por ejemplo R.attr.colorTarjeta). */
    protected int colorDelTema(int atributo) {
        // obtainStyledAttributes también funciona cuando el atributo apunta a un selector de colores
        TypedArray a = obtainStyledAttributes(new int[]{atributo});
        try {
            return a.getColor(0, 0xFF5B4F8F);
        } finally {
            a.recycle();
        }
    }
}
