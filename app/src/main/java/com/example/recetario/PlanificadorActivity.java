package com.example.recetario;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.recetario.datos.PlanDao;
import com.example.recetario.datos.RecetaDao;
import com.example.recetario.modelo.ComidaPlan;
import com.example.recetario.modelo.Receta;
import com.example.recetario.util.Preferencias;
import com.example.recetario.util.Semana;
import com.example.recetario.util.Vistas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Planificador semanal: desayuno, comida y cena de lunes a domingo. */
public class PlanificadorActivity extends BaseActivity {

    private static final String ESTADO_LUNES = "lunes";

    private PlanDao planDao;
    private RecetaDao recetaDao;
    private String lunes;
    private int comidasEnSemana;

    private TextView tvSemana;
    private TextView tvResumen;
    private LinearLayout contDias;

    /**
     * Inicializa la pantalla del planificador, recupera el lunes de la semana actual y asigna navegadores.
     * @param savedInstanceState Estado guardado previamente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificador);
        setTitle(R.string.planificador);
        planDao = new PlanDao(this);
        recetaDao = new RecetaDao(this);

        lunes = savedInstanceState != null
                ? savedInstanceState.getString(ESTADO_LUNES)
                : Semana.lunesDe(Semana.hoy());

        tvSemana = (TextView) findViewById(R.id.tvSemana);
        tvResumen = (TextView) findViewById(R.id.tvResumen);
        contDias = (LinearLayout) findViewById(R.id.contDias);

        findViewById(R.id.btnAnterior).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunes = Semana.sumarDias(lunes, -7);
                mostrar();
            }
        });
        findViewById(R.id.btnSiguiente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunes = Semana.sumarDias(lunes, 7);
                mostrar();
            }
        });
        findViewById(R.id.btnLista).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comidasEnSemana == 0) {
                    aviso(getString(R.string.plan_vacio));
                    return;
                }
                Intent i = new Intent(PlanificadorActivity.this, ListaIngredientesActivity.class);
                i.putExtra(ListaIngredientesActivity.EXTRA_LUNES, lunes);
                startActivity(i);
            }
        });
    }

    /**
     * Refresca las vistas de la semana al volver a la pantalla.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mostrar();
    }

    /**
     * Consulta las comidas agendadas en la semana actual y genera la vista dinámica para los 7 días.
     * Permite visualizar y agregar múltiples platillos/bebidas por tiempo (Desayuno, Comida, Cena).
     */
    private void mostrar() {
        tvSemana.setText(Semana.etiqueta(lunes));
        List<ComidaPlan> comidas = planDao.semana(lunes);
        comidasEnSemana = comidas.size();
        tvResumen.setText(comidas.isEmpty()
                ? getString(R.string.plan_instrucciones)
                : getResources().getQuantityString(R.plurals.num_comidas, comidas.size(), comidas.size()));

        Map<String, List<ComidaPlan>> porLugar = new HashMap<>();
        for (ComidaPlan cp : comidas) {
            String clave = cp.fecha + "#" + cp.tiempo;
            List<ComidaPlan> lista = porLugar.get(clave);
            if (lista == null) {
                lista = new ArrayList<>();
                porLugar.put(clave, lista);
            }
            lista.add(cp);
        }

        contDias.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        String hoy = Semana.hoy();
        int acento = colorDelTema(android.R.attr.colorAccent);
        int textoNormal = colorDelTema(android.R.attr.textColorPrimary);

        for (final String fecha : Semana.dias(lunes)) {
            View dia = inflater.inflate(R.layout.item_dia_plan, contDias, false);
            TextView tvDia = (TextView) dia.findViewById(R.id.tvDia);
            boolean esHoy = fecha.equals(hoy);
            tvDia.setText(esHoy ? Semana.nombreDia(fecha) + " · " + getString(R.string.hoy) : Semana.nombreDia(fecha));
            if (esHoy) {
                tvDia.setTextColor(acento);
            }
            LinearLayout cont = (LinearLayout) dia.findViewById(R.id.contComidas);

            for (int t = 0; t < 3; t++) {
                final int tiempo = t;
                List<ComidaPlan> lista = porLugar.get(fecha + "#" + t);

                if (lista == null || lista.isEmpty()) {
                    View fila = inflater.inflate(R.layout.item_comida_plan, cont, false);
                    TextView tvTiempo = (TextView) fila.findViewById(R.id.tvTiempo);
                    TextView tvReceta = (TextView) fila.findViewById(R.id.tvReceta);
                    TextView tvPorciones = (TextView) fila.findViewById(R.id.tvPorciones);
                    View marca = fila.findViewById(R.id.marca);

                    tvTiempo.setText(ComidaPlan.TIEMPOS[t]);
                    tvReceta.setText(R.string.agregar_receta_plan);
                    tvReceta.setTextColor(acento);
                    tvPorciones.setVisibility(View.GONE);
                    marca.setVisibility(View.INVISIBLE);
                    fila.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            elegirReceta(fecha, tiempo, null);
                        }
                    });
                    cont.addView(fila);
                } else {
                    for (int i = 0; i < lista.size(); i++) {
                        final ComidaPlan cp = lista.get(i);
                        View fila = inflater.inflate(R.layout.item_comida_plan, cont, false);
                        TextView tvTiempo = (TextView) fila.findViewById(R.id.tvTiempo);
                        TextView tvReceta = (TextView) fila.findViewById(R.id.tvReceta);
                        TextView tvPorciones = (TextView) fila.findViewById(R.id.tvPorciones);
                        View marca = fila.findViewById(R.id.marca);

                        tvTiempo.setText(i == 0 ? ComidaPlan.TIEMPOS[t] : "");
                        tvReceta.setText(cp.recetaNombre);
                        tvReceta.setTextColor(textoNormal);
                        tvPorciones.setText(getString(R.string.porciones_corto, cp.porciones));
                        tvPorciones.setVisibility(View.VISIBLE);
                        marca.setVisibility(View.VISIBLE);
                        marca.setBackground(Vistas.redondeado(Vistas.color(cp.colorCategoria, acento), dp(2)));

                        fila.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                opcionesComida(cp);
                            }
                        });
                        cont.addView(fila);
                    }

                    View filaMas = inflater.inflate(R.layout.item_comida_plan, cont, false);
                    TextView tvTiempo = (TextView) filaMas.findViewById(R.id.tvTiempo);
                    TextView tvReceta = (TextView) filaMas.findViewById(R.id.tvReceta);
                    TextView tvPorciones = (TextView) filaMas.findViewById(R.id.tvPorciones);
                    View marca = filaMas.findViewById(R.id.marca);

                    tvTiempo.setText("");
                    tvReceta.setText(R.string.agregar_receta_plan);
                    tvReceta.setTextColor(acento);
                    tvPorciones.setVisibility(View.GONE);
                    marca.setVisibility(View.INVISIBLE);

                    filaMas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            elegirReceta(fecha, tiempo, null);
                        }
                    });
                    cont.addView(filaMas);
                }
            }
            contDias.addView(dia);
        }
    }

    /**
     * Muestra un diálogo de selección con todas las recetas disponibles para asignar a la fecha y tiempo dados.
     * @param fecha Fecha ISO.
     * @param tiempo Tiempo de comida (0, 1 o 2).
     * @param actual Objeto ComidaPlan existente si se trata de un reemplazo (null para inserción).
     */
    private void elegirReceta(final String fecha, final int tiempo, final ComidaPlan actual) {
        final List<Receta> recetas = recetaDao.buscar(null, -1, 0, false, Preferencias.ORDEN_NOMBRE);
        if (recetas.isEmpty()) {
            aviso(getString(R.string.vacio_recetas));
            return;
        }
        Collections.sort(recetas, new Comparator<Receta>() {
            @Override
            public int compare(Receta a, Receta b) {
                int c = Long.compare(a.categoriaId, b.categoriaId);
                return c != 0 ? c : a.nombre.compareToIgnoreCase(b.nombre);
            }
        });
        String[] nombres = new String[recetas.size()];
        for (int i = 0; i < recetas.size(); i++) {
            nombres[i] = recetas.get(i).nombre + "  ·  " + recetas.get(i).categoria;
        }
        new AlertDialog.Builder(this)
                .setTitle(ComidaPlan.TIEMPOS[tiempo] + " · " + Semana.nombreDia(fecha))
                .setItems(nombres, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (actual != null) {
                            planDao.cambiarReceta(actual.id, recetas.get(which).id);
                        } else {
                            int porciones = prefs.getPorciones();
                            planDao.asignar(fecha, tiempo, recetas.get(which).id, porciones);
                        }
                        mostrar();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Muestra el diálogo de opciones para una entrada específica (Ver receta, Cambiar receta, Ajustar porciones o Quitar).
     * @param cp Entrada seleccionada del planificador.
     */
    private void opcionesComida(final ComidaPlan cp) {
        String[] opciones = {
                getString(R.string.ver_receta),
                getString(R.string.cambiar_receta),
                getString(R.string.cambiar_porciones, cp.porciones),
                getString(R.string.quitar_del_plan)
        };
        new AlertDialog.Builder(this)
                .setTitle(cp.recetaNombre)
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent i = new Intent(PlanificadorActivity.this, DetalleRecetaActivity.class);
                                i.putExtra(DetalleRecetaActivity.EXTRA_ID, cp.recetaId);
                                startActivity(i);
                                break;
                            case 1:
                                elegirReceta(cp.fecha, cp.tiempo, cp);
                                break;
                            case 2:
                                elegirPorciones(cp);
                                break;
                            default:
                                planDao.quitar(cp.id);
                                mostrar();
                        }
                    }
                })
                .show();
    }

    /**
     * Muestra un selector numérico (NumberPicker) para cambiar las porciones de la receta agendada.
     * @param cp Entrada seleccionada del planificador.
     */
    private void elegirPorciones(final ComidaPlan cp) {
        final NumberPicker np = new NumberPicker(this);
        np.setMinValue(Preferencias.PORCIONES_MIN);
        np.setMaxValue(Preferencias.PORCIONES_MAX);
        np.setValue(cp.porciones);
        np.setWrapSelectorWheel(false);
        FrameLayout marco = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        marco.addView(np, lp);
        marco.setPadding(0, dp(12), 0, dp(4));

        new AlertDialog.Builder(this)
                .setTitle(R.string.porciones)
                .setView(marco)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planDao.cambiarPorciones(cp.id, np.getValue());
                        mostrar();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Infla el menú superior del planificador.
     * @param menu Objeto Menu.
     * @return true para mostrarlo.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_planificador, menu);
        return true;
    }

    /**
     * Maneja las opciones del menú superior (Ir a semana actual o Vaciar semana).
     * @param item Elemento de menú seleccionado.
     * @return true si fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accion_hoy) {
            lunes = Semana.lunesDe(Semana.hoy());
            mostrar();
            return true;
        } else if (id == R.id.accion_vaciar) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.vaciar_semana)
                    .setMessage(R.string.confirmar_vaciar)
                    .setPositiveButton(R.string.vaciar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            planDao.limpiarSemana(lunes);
                            mostrar();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Preserva la fecha del lunes de la semana que se está visualizando.
     * @param outState Bundle de salida.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_LUNES, lunes);
    }
}
