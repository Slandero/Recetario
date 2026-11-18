package com.example.recetario;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recetario.datos.RecetaDao;
import com.example.recetario.modelo.IngredienteCatalogo;
import com.example.recetario.modelo.Receta;

import java.util.ArrayList;
import java.util.List;

/** Catálogo de ingredientes: consulta sin Internet de qué recetas usan cada uno. */
public class IngredientesActivity extends BaseActivity {

    private RecetaDao dao;
    private EditText etBuscar;
    private TextView tvConteo;
    private List<IngredienteCatalogo> ingredientes = new ArrayList<>();
    private CatalogoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes);
        setTitle(R.string.ingredientes);
        dao = new RecetaDao(this);

        etBuscar = (EditText) findViewById(R.id.etBuscar);
        tvConteo = (TextView) findViewById(R.id.tvConteo);
        ListView lista = (ListView) findViewById(R.id.lvIngredientes);
        adapter = new CatalogoAdapter();
        lista.setAdapter(adapter);
        lista.setEmptyView(findViewById(R.id.tvVacio));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarRecetas(ingredientes.get(position));
            }
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cargar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        ingredientes = dao.catalogo(etBuscar.getText().toString());
        adapter.notifyDataSetChanged();
        tvConteo.setText(getResources().getQuantityString(
                R.plurals.num_ingredientes, ingredientes.size(), ingredientes.size()));
    }

    private void mostrarRecetas(IngredienteCatalogo ing) {
        final List<Receta> recetas = dao.recetasConIngrediente(ing.id);
        if (recetas.isEmpty()) {
            aviso(getString(R.string.ingrediente_sin_recetas));
            return;
        }
        String[] nombres = new String[recetas.size()];
        for (int i = 0; i < recetas.size(); i++) {
            nombres[i] = recetas.get(i).nombre;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.recetas_con, ing.nombre))
                .setItems(nombres, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(IngredientesActivity.this, DetalleRecetaActivity.class);
                        i.putExtra(DetalleRecetaActivity.EXTRA_ID, recetas.get(which).id);
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.cerrar, null)
                .show();
    }

    private class CatalogoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ingredientes.size();
        }

        @Override
        public IngredienteCatalogo getItem(int position) {
            return ingredientes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ingredientes.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_ingrediente_catalogo, parent, false);
            }
            IngredienteCatalogo ing = ingredientes.get(position);
            ((TextView) convertView.findViewById(R.id.tvNombre)).setText(ing.nombre);
            ((TextView) convertView.findViewById(R.id.tvConteoRecetas)).setText(
                    getResources().getQuantityString(R.plurals.num_recetas, ing.numRecetas, ing.numRecetas));
            return convertView;
        }
    }
}
