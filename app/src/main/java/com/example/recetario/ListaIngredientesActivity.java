package com.example.recetario;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.recetario.datos.PlanDao;
import com.example.recetario.util.Exportador;
import com.example.recetario.util.ListaCompras;
import com.example.recetario.util.Semana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Lista de ingredientes de las comidas planeadas en la semana. */
public class ListaIngredientesActivity extends BaseActivity {

    public static final String EXTRA_LUNES = "lunes";
    private static final int REQ_EXPORTAR = 30;

    private PlanDao planDao;
    private String lunes;
    private List<ListaCompras.Item> items = new ArrayList<>();
    private Set<String> marcados = new HashSet<>();
    private int numComidas;

    private TextView tvResumen;
    private ProgressBar pbAvance;
    private ItemsAdapter adapter;

    /**
     * Inicializa la vista de la lista de compras, configura el ListView y los eventos de tachado de insumos.
     * @param savedInstanceState Estado guardado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ingredientes);
        setTitle(R.string.lista_ingredientes);
        planDao = new PlanDao(this);

        lunes = getIntent().getStringExtra(EXTRA_LUNES);
        if (lunes == null) {
            lunes = Semana.lunesDe(Semana.hoy());
        }
        ((TextView) findViewById(R.id.tvTitulo)).setText(getString(R.string.semana_de, Semana.etiqueta(lunes)));
        tvResumen = (TextView) findViewById(R.id.tvResumen);
        pbAvance = (ProgressBar) findViewById(R.id.pbAvance);

        ListView lista = (ListView) findViewById(R.id.lvLista);
        adapter = new ItemsAdapter();
        lista.setAdapter(adapter);
        lista.setEmptyView(findViewById(R.id.tvVacio));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clave = items.get(position).clave();
                if (!marcados.remove(clave)) {
                    marcados.add(clave);
                }
                prefs.setMarcados(lunes, marcados);
                adapter.notifyDataSetChanged();
                actualizarResumen();
            }
        });
    }

    /**
     * Carga y refresca los datos de la lista de ingredientes al volver a la pantalla.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    /**
     * Consulta las comidas planificadas en la semana, agrupa sus ingredientes y recupera los elementos tachados.
     */
    private void cargar() {
        items = ListaCompras.agrupar(planDao.ingredientesSemana(lunes));
        marcados = prefs.getMarcados(lunes);
        numComidas = planDao.semana(lunes).size();
        adapter.notifyDataSetChanged();
        actualizarResumen();
    }

    /**
     * Cuenta el número de ingredientes marcados como comprados.
     * @return Cantidad de insumos comprados.
     */
    private int comprados() {
        int n = 0;
        for (ListaCompras.Item it : items) {
            if (marcados.contains(it.clave())) {
                n++;
            }
        }
        return n;
    }

    /**
     * Actualiza la etiqueta de resumen y la barra de progreso de compras.
     */
    private void actualizarResumen() {
        int comprados = comprados();
        tvResumen.setText(getString(R.string.resumen_lista, numComidas, items.size(), comprados));
        pbAvance.setMax(Math.max(1, items.size()));
        pbAvance.setProgress(comprados);
        pbAvance.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * Genera la etiqueta del título de la semana actual.
     * @return Título de la semana.
     */
    private String titulo() {
        return getString(R.string.semana_de, Semana.etiqueta(lunes));
    }

    /**
     * Infla el menú de opciones de la lista de compras (Compartir, Exportar .txt, Desmarcar todo).
     * @param menu Objeto Menu.
     * @return true para mostrar el menú.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    /**
     * Maneja las opciones del menú de la lista de ingredientes.
     * @param item Elemento de menú seleccionado.
     * @return true si fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accion_compartir || id == R.id.accion_exportar) {
            if (items.isEmpty()) {
                aviso(getString(R.string.lista_vacia));
                return true;
            }
        }
        if (id == R.id.accion_compartir) {
            Intent enviar = new Intent(Intent.ACTION_SEND);
            enviar.setType("text/plain");
            enviar.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lista_ingredientes));
            enviar.putExtra(Intent.EXTRA_TEXT, Exportador.listaTexto(titulo(), items, marcados));
            startActivity(Intent.createChooser(enviar, getString(R.string.compartir_lista)));
            return true;
        } else if (id == R.id.accion_exportar) {
            Intent crear = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            crear.addCategory(Intent.CATEGORY_OPENABLE);
            crear.setType("text/plain");
            crear.putExtra(Intent.EXTRA_TITLE, "lista_ingredientes_" + lunes + ".txt");
            try {
                startActivityForResult(crear, REQ_EXPORTAR);
            } catch (ActivityNotFoundException e) {
                aviso(getString(R.string.error_sin_app_archivos));
            }
            return true;
        } else if (id == R.id.accion_desmarcar) {
            marcados.clear();
            prefs.setMarcados(lunes, marcados);
            adapter.notifyDataSetChanged();
            actualizarResumen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Escribe el texto de la lista de compras en la URI seleccionada por el usuario (SAF).
     * @param requestCode Código de solicitud.
     * @param resultCode Código de resultado.
     * @param data Intent con la URI seleccionada.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXPORTAR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (items.isEmpty()) {
                cargar();
            }
            try {
                Exportador.escribir(this, data.getData(), Exportador.listaTexto(titulo(), items, marcados));
                aviso(getString(R.string.lista_exportada));
            } catch (IOException e) {
                aviso(getString(R.string.error_exportar, e.getMessage()));
            }
        }
    }

    /** Adaptador para renderizar cada ítem de la lista de compras con casilla y tachado. */
    private class ItemsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ListaCompras.Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_lista, parent, false);
            }
            ListaCompras.Item it = items.get(position);
            boolean comprado = marcados.contains(it.clave());
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.cbComprado);
            TextView tvIngrediente = (TextView) convertView.findViewById(R.id.tvIngrediente);
            TextView tvRecetas = (TextView) convertView.findViewById(R.id.tvRecetas);
            TextView tvCantidad = (TextView) convertView.findViewById(R.id.tvCantidad);

            cb.setChecked(comprado);
            tvIngrediente.setText(it.ingrediente);
            tvRecetas.setText(it.recetas);
            tvCantidad.setText(it.cantidades);
            if (comprado) {
                tvIngrediente.setPaintFlags(tvIngrediente.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvIngrediente.setPaintFlags(tvIngrediente.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            convertView.setAlpha(comprado ? 0.5f : 1f);
            return convertView;
        }
    }
}
