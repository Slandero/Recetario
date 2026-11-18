package com.example.recetario;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recetario.datos.RecetaDao;
import com.example.recetario.modelo.Categoria;
import com.example.recetario.modelo.Receta;

import java.util.ArrayList;
import java.util.List;

/** Pantalla principal: lista de recetas con búsqueda, filtros y favoritas. */
public class MainActivity extends BaseActivity implements RecetaAdapter.OnFavoritaListener {

    private static final String ESTADO_BUSQUEDA = "busqueda";

    private RecetaDao dao;
    private RecetaAdapter adapter;
    private Spinner spCategoria;
    private Spinner spDificultad;
    private CheckBox cbFavoritas;
    private TextView tvConteo;
    private TextView tvVacio;
    private String textoBusqueda = "";

    /**
     * Inicializa la interfaz principal, configura la lista de recetas y asigna eventos de clic y creación.
     * @param savedInstanceState Estado guardado previa recreación.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.recetario);
        if (getActionBar() != null) {
            getActionBar().setSubtitle(R.string.mexico_tipico);
        }
        dao = new RecetaDao(this);

        spCategoria = (Spinner) findViewById(R.id.spCategoria);
        spDificultad = (Spinner) findViewById(R.id.spDificultad);
        cbFavoritas = (CheckBox) findViewById(R.id.cbFavoritas);
        tvConteo = (TextView) findViewById(R.id.tvConteo);
        tvVacio = (TextView) findViewById(R.id.tvVacio);

        ListView lista = (ListView) findViewById(R.id.lvRecetas);
        adapter = new RecetaAdapter(this, this);
        lista.setAdapter(adapter);
        lista.setEmptyView(findViewById(R.id.vacio));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, DetalleRecetaActivity.class);
                i.putExtra(DetalleRecetaActivity.EXTRA_ID, id);
                startActivity(i);
            }
        });

        findViewById(R.id.fabNueva).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FormRecetaActivity.class));
            }
        });

        if (savedInstanceState != null) {
            textoBusqueda = savedInstanceState.getString(ESTADO_BUSQUEDA, "");
        }
        configurarFiltros();
    }

    /**
     * Inhabilita la flecha de regreso en la pantalla principal.
     * @return false indicando que no hay botón de retorno en el ActionBar.
     */
    @Override
    protected boolean mostrarRegresar() {
        return false;
    }

    /**
     * Carga los adaptadores de categorías y dificultad, aplicando las preferencias del usuario si están activas.
     */
    private void configurarFiltros() {
        List<Categoria> opciones = new ArrayList<>();
        opciones.add(new Categoria(-1, getString(R.string.todas_categorias), null));
        opciones.addAll(dao.categorias());
        ArrayAdapter<Categoria> adCategorias = new ArrayAdapter<>(this, R.layout.spinner_chip, opciones);
        adCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adCategorias);

        ArrayAdapter<CharSequence> adDificultad = ArrayAdapter.createFromResource(
                this, R.array.filtro_dificultad, R.layout.spinner_chip);
        adDificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adDificultad);

        if (prefs.isRecordarFiltros()) {
            long categoriaGuardada = prefs.getFiltroCategoria();
            for (int i = 0; i < opciones.size(); i++) {
                if (opciones.get(i).id == categoriaGuardada) {
                    spCategoria.setSelection(i);
                }
            }
            int dificultad = prefs.getFiltroDificultad();
            if (dificultad >= 0 && dificultad < adDificultad.getCount()) {
                spDificultad.setSelection(dificultad);
            }
            cbFavoritas.setChecked(prefs.isFiltroFavoritas());
        }

        AdapterView.OnItemSelectedListener alCambiar = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrosCambiaron();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spCategoria.setOnItemSelectedListener(alCambiar);
        spDificultad.setOnItemSelectedListener(alCambiar);
        cbFavoritas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filtrosCambiaron();
            }
        });
    }

    /**
     * Notifica el cambio en la selección de los filtros, guardando las preferencias y recargando la lista.
     */
    private void filtrosCambiaron() {
        prefs.guardarFiltros(categoriaElegida(), spDificultad.getSelectedItemPosition(), cbFavoritas.isChecked());
        cargar();
    }

    /**
     * Obtiene el ID de la categoría actualmente seleccionada en el Spinner.
     * @return ID de la categoría (-1 si es "Todas").
     */
    private long categoriaElegida() {
        Categoria c = (Categoria) spCategoria.getSelectedItem();
        return c == null ? -1 : c.id;
    }

    /**
     * Recarga los datos de la lista al volver a la pantalla.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    /**
     * Consulta las recetas a la base de datos aplicando los filtros y texto de búsqueda actuales.
     */
    private void cargar() {
        List<Receta> recetas = dao.buscar(textoBusqueda, categoriaElegida(),
                spDificultad.getSelectedItemPosition(), cbFavoritas.isChecked(), prefs.getOrden());
        adapter.setRecetas(recetas);
        tvConteo.setText(getResources().getQuantityString(R.plurals.num_recetas, recetas.size(), recetas.size()));

        boolean hayFiltros = categoriaElegida() > 0 || spDificultad.getSelectedItemPosition() > 0
                || cbFavoritas.isChecked() || !textoBusqueda.trim().isEmpty();
        tvVacio.setText(hayFiltros ? R.string.vacio_filtros : R.string.vacio_recetas);
    }

    /**
     * Maneja el evento de marcar/desmarcar receta favorita desde el botón de la tarjeta en la lista.
     * @param receta Receta que fue marcada/desmarcada.
     */
    @Override
    public void onFavorita(Receta receta) {
        boolean ahora = dao.alternarFavorita(receta.id);
        aviso(getString(ahora ? R.string.agregada_favoritas : R.string.quitada_favoritas));
        cargar();
    }

    /**
     * Infla el menú de opciones superior y configura la barra de búsqueda (SearchView).
     * @param menu Menú a inflar.
     * @return true para mostrar el menú.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem buscar = menu.findItem(R.id.accion_buscar);
        final SearchView sv = (SearchView) buscar.getActionView();
        sv.setQueryHint(getString(R.string.buscar_hint));
        if (!textoBusqueda.isEmpty()) {
            buscar.expandActionView();
            sv.setQuery(textoBusqueda, false);
        }
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textoBusqueda = newText;
                cargar();
                return true;
            }
        });
        return true;
    }

    /**
     * Maneja la navegación a las pantallas del planificador, lista de compras, catálogo de ingredientes y ajustes.
     * @param item Elemento de menú seleccionado.
     * @return true si fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accion_planificador) {
            startActivity(new Intent(this, PlanificadorActivity.class));
            return true;
        } else if (id == R.id.accion_lista) {
            startActivity(new Intent(this, ListaIngredientesActivity.class));
            return true;
        } else if (id == R.id.accion_ingredientes) {
            startActivity(new Intent(this, IngredientesActivity.class));
            return true;
        } else if (id == R.id.accion_ajustes) {
            startActivity(new Intent(this, AjustesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Preserva el estado de la búsqueda en cambios de orientación.
     * @param outState Bundle de salida.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_BUSQUEDA, textoBusqueda);
    }
}
