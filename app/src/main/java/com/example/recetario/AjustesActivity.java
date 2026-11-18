package com.example.recetario;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.recetario.datos.RecetaDao;
import com.example.recetario.datos.RecetarioDbHelper;
import com.example.recetario.util.Exportador;
import com.example.recetario.util.FotoStorage;
import com.example.recetario.util.Preferencias;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/** Ajustes guardados con SharedPreferences y datos de los archivos de la app. */
public class AjustesActivity extends BaseActivity {

    private static final int REQ_EXPORTAR_TODO = 40;

    private TextView tvPorciones;
    private ImageButton btnMenos;
    private ImageButton btnMas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        setTitle(R.string.ajustes);

        // ---- Modo oscuro ----
        Switch swOscuro = (Switch) findViewById(R.id.swModoOscuro);
        swOscuro.setChecked(prefs.isModoOscuro());
        swOscuro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setModoOscuro(isChecked);
                recreate(); // se vuelve a crear la pantalla con el otro tema
            }
        });

        // ---- Porciones predeterminadas ----
        tvPorciones = (TextView) findViewById(R.id.tvPorciones);
        btnMenos = (ImageButton) findViewById(R.id.btnMenos);
        btnMas = (ImageButton) findViewById(R.id.btnMas);
        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.setPorciones(prefs.getPorciones() - 1);
                mostrarPorciones();
            }
        });
        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.setPorciones(prefs.getPorciones() + 1);
                mostrarPorciones();
            }
        });
        mostrarPorciones();

        // ---- Orden de la lista ----
        Spinner spOrden = (Spinner) findViewById(R.id.spOrden);
        ArrayAdapter<CharSequence> adOrden = ArrayAdapter.createFromResource(
                this, R.array.orden_recetas, android.R.layout.simple_spinner_item);
        adOrden.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOrden.setAdapter(adOrden);
        spOrden.setSelection(prefs.getOrden());
        spOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.setOrden(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ---- Filtros ----
        Switch swRecordar = (Switch) findViewById(R.id.swRecordarFiltros);
        swRecordar.setChecked(prefs.isRecordarFiltros());
        swRecordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setRecordarFiltros(isChecked);
            }
        });
        findViewById(R.id.btnBorrarFiltros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.limpiarFiltros();
                aviso(getString(R.string.filtros_borrados));
            }
        });

        // ---- Archivos ----
        findViewById(R.id.btnExportarTodo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent crear = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                crear.addCategory(Intent.CATEGORY_OPENABLE);
                crear.setType("application/json");
                crear.putExtra(Intent.EXTRA_TITLE, "recetario_mexico_tipico.json");
                try {
                    startActivityForResult(crear, REQ_EXPORTAR_TODO);
                } catch (ActivityNotFoundException e) {
                    aviso(getString(R.string.error_sin_app_archivos));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarAlmacenamiento();
    }

    private void mostrarPorciones() {
        int p = prefs.getPorciones();
        tvPorciones.setText(String.valueOf(p));
        btnMenos.setEnabled(p > Preferencias.PORCIONES_MIN);
        btnMas.setEnabled(p < Preferencias.PORCIONES_MAX);
        btnMenos.setAlpha(btnMenos.isEnabled() ? 1f : 0.35f);
        btnMas.setAlpha(btnMas.isEnabled() ? 1f : 0.35f);
    }

    private void mostrarAlmacenamiento() {
        File carpeta = FotoStorage.carpeta(this);
        File bd = getDatabasePath(RecetarioDbHelper.NOMBRE_BD);
        ((TextView) findViewById(R.id.tvFotos)).setText(getString(R.string.info_fotos,
                FotoStorage.contarFotos(this),
                Formatter.formatShortFileSize(this, FotoStorage.tamanoTotal(this)),
                carpeta.getAbsolutePath()));
        ((TextView) findViewById(R.id.tvBaseDatos)).setText(getString(R.string.info_bd,
                Formatter.formatShortFileSize(this, bd.length()),
                bd.getAbsolutePath()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXPORTAR_TODO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                String json = Exportador.recetasJson(new RecetaDao(this).todasCompletas());
                Exportador.escribir(this, data.getData(), json);
                aviso(getString(R.string.recetas_exportadas));
            } catch (IOException | JSONException e) {
                aviso(getString(R.string.error_exportar, e.getMessage()));
            }
        }
    }
}
