package com.example.recetario;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recetario.datos.PlanDao;
import com.example.recetario.datos.RecetaDao;
import com.example.recetario.modelo.ComidaPlan;
import com.example.recetario.modelo.IngredienteReceta;
import com.example.recetario.modelo.Receta;
import com.example.recetario.util.Exportador;
import com.example.recetario.util.FotoStorage;
import com.example.recetario.util.Formato;
import com.example.recetario.util.Preferencias;
import com.example.recetario.util.Semana;
import com.example.recetario.util.Vistas;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** Detalle de una receta: foto, ingredientes ajustables por porciones y preparación. */
public class DetalleRecetaActivity extends BaseActivity {

    public static final String EXTRA_ID = "receta_id";
    private static final int REQ_EXPORTAR = 10;
    private static final String ESTADO_PORCIONES = "porciones";

    private RecetaDao dao;
    private PlanDao planDao;
    private long recetaId;
    private Receta receta;
    private int porciones;

    private FrameLayout marcoFoto;
    private ImageView ivFoto;
    private TextView tvInicial;
    private TextView tvNombre;
    private TextView tvCategoria;
    private TextView tvTiempo;
    private TextView tvDificultad;
    private TextView tvNumIngredientes;
    private TextView tvPorciones;
    private TextView tvPorcionesBase;
    private ImageButton btnMenos;
    private ImageButton btnMas;
    private LinearLayout contIngredientes;
    private LinearLayout contPasos;

    /**
     * Inicializa la actividad de detalle, recupera el ID de la receta y configura listeners.
     * @param savedInstanceState Estado guardado de porciones previamente ajustadas.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_receta);
        setTitle("");
        dao = new RecetaDao(this);
        planDao = new PlanDao(this);
        recetaId = getIntent().getLongExtra(EXTRA_ID, -1);

        porciones = savedInstanceState != null
                ? savedInstanceState.getInt(ESTADO_PORCIONES, prefs.getPorciones())
                : prefs.getPorciones();

        marcoFoto = (FrameLayout) findViewById(R.id.marcoFoto);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        tvInicial = (TextView) findViewById(R.id.tvInicial);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        tvCategoria = (TextView) findViewById(R.id.tvCategoria);
        tvTiempo = (TextView) findViewById(R.id.tvTiempo);
        tvDificultad = (TextView) findViewById(R.id.tvDificultad);
        tvNumIngredientes = (TextView) findViewById(R.id.tvNumIngredientes);
        tvPorciones = (TextView) findViewById(R.id.tvPorciones);
        tvPorcionesBase = (TextView) findViewById(R.id.tvPorcionesBase);
        btnMenos = (ImageButton) findViewById(R.id.btnMenos);
        btnMas = (ImageButton) findViewById(R.id.btnMas);
        contIngredientes = (LinearLayout) findViewById(R.id.contIngredientes);
        contPasos = (LinearLayout) findViewById(R.id.contPasos);

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarPorciones(porciones - 1);
            }
        });
        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarPorciones(porciones + 1);
            }
        });
        findViewById(R.id.btnPlanificar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirDia();
            }
        });
    }

    /**
     * Carga y refresca los datos de la receta al volver a la pantalla.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    /**
     * Obtiene los datos completos de la receta de la BD y actualiza las vistas de foto, información, ingredientes y pasos.
     */
    private void cargar() {
        receta = dao.obtener(recetaId);
        if (receta == null) {
            finish();
            return;
        }
        int color = Vistas.color(receta.colorCategoria, getColor(R.color.morado));

        Bitmap bmp = FotoStorage.cargar(this, receta.foto, getResources().getDisplayMetrics().widthPixels);
        if (bmp != null) {
            ivFoto.setImageBitmap(bmp);
            ivFoto.setVisibility(View.VISIBLE);
            tvInicial.setVisibility(View.GONE);
            marcoFoto.setBackgroundColor(colorDelTema(R.attr.colorChip));
        } else {
            ivFoto.setVisibility(View.GONE);
            tvInicial.setVisibility(View.VISIBLE);
            tvInicial.setText(Vistas.inicial(receta.nombre));
            marcoFoto.setBackgroundColor(color);
        }

        tvNombre.setText(receta.nombre);
        tvCategoria.setText(receta.categoria);
        tvCategoria.setBackground(Vistas.redondeado(color, dp(12)));
        tvTiempo.setText(Formato.tiempo(receta.tiempoMin));
        tvDificultad.setText(Receta.textoDificultad(receta.dificultad));
        tvNumIngredientes.setText(getResources().getQuantityString(
                R.plurals.num_ingredientes, receta.ingredientes.size(), receta.ingredientes.size()));
        tvPorcionesBase.setText(getString(R.string.porciones_base, receta.porciones));

        mostrarIngredientes();
        mostrarPasos();
        invalidateOptionsMenu();
    }

    /**
     * Actualiza el valor de porciones deseadas dentro de los límites válidos.
     * @param nuevas Nuevo número de porciones.
     */
    private void cambiarPorciones(int nuevas) {
        porciones = Math.max(Preferencias.PORCIONES_MIN, Math.min(Preferencias.PORCIONES_MAX, nuevas));
        mostrarIngredientes();
    }

    /**
     * Recalcula y muestra las cantidades de cada ingrediente escaladas según las porciones elegidas.
     */
    private void mostrarIngredientes() {
        tvPorciones.setText(String.valueOf(porciones));
        btnMenos.setEnabled(porciones > Preferencias.PORCIONES_MIN);
        btnMas.setEnabled(porciones < Preferencias.PORCIONES_MAX);
        btnMenos.setAlpha(btnMenos.isEnabled() ? 1f : 0.35f);
        btnMas.setAlpha(btnMas.isEnabled() ? 1f : 0.35f);

        contIngredientes.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        List<IngredienteReceta> lista = receta.ingredientes;
        for (int i = 0; i < lista.size(); i++) {
            IngredienteReceta ir = lista.get(i);
            View fila = inflater.inflate(R.layout.item_ingrediente_detalle, contIngredientes, false);
            ((TextView) fila.findViewById(R.id.tvNombre)).setText(ir.nombre);
            ((TextView) fila.findViewById(R.id.tvCantidad)).setText(
                    Formato.cantidad(ir.cantidadPara(porciones, receta.porciones), ir.unidad));
            fila.findViewById(R.id.divisor).setVisibility(i == lista.size() - 1 ? View.GONE : View.VISIBLE);
            contIngredientes.addView(fila);
        }
    }

    /**
     * Infla y enumera los pasos del procedimiento de preparación.
     */
    private void mostrarPasos() {
        contPasos.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        int n = 1;
        for (String paso : receta.pasos()) {
            View fila = inflater.inflate(R.layout.item_paso, contPasos, false);
            ((TextView) fila.findViewById(R.id.tvNumero)).setText(String.valueOf(n++));
            ((TextView) fila.findViewById(R.id.tvPaso)).setText(paso);
            contPasos.addView(fila);
        }
    }

    // ------------------------------------------------------------------ planificador

    /**
     * Muestra un diálogo para elegir el día de la semana al agregar la receta al planificador.
     */
    private void elegirDia() {
        final String[] fechas = new String[7];
        String[] etiquetas = new String[7];
        String hoy = Semana.hoy();
        for (int i = 0; i < 7; i++) {
            fechas[i] = Semana.sumarDias(hoy, i);
            String corta = Semana.fechaCorta(fechas[i]);
            corta = corta.substring(0, 1).toUpperCase(new Locale("es", "MX")) + corta.substring(1);
            if (i == 0) {
                etiquetas[i] = getString(R.string.hoy) + " · " + corta;
            } else if (i == 1) {
                etiquetas[i] = getString(R.string.manana) + " · " + corta;
            } else {
                etiquetas[i] = corta;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.que_dia)
                .setItems(etiquetas, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        elegirTiempo(fechas[which]);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Muestra un diálogo para seleccionar el tiempo de comida (Desayuno, Comida, Cena) al agendar la receta.
     * @param fecha Fecha elegida (YYYY-MM-DD).
     */
    private void elegirTiempo(final String fecha) {
        String[] ocupados = planDao.recetasDelDia(fecha);
        String[] etiquetas = new String[3];
        for (int t = 0; t < 3; t++) {
            etiquetas[t] = ocupados[t] == null
                    ? ComidaPlan.TIEMPOS[t]
                    : getString(R.string.tiempo_ocupado, ComidaPlan.TIEMPOS[t], ocupados[t]);
        }
        new AlertDialog.Builder(this)
                .setTitle(Semana.nombreDia(fecha))
                .setItems(etiquetas, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planDao.asignar(fecha, which, receta.id, porciones);
                        aviso(getString(R.string.agregada_plan, Semana.nombreDia(fecha), ComidaPlan.TIEMPOS[which]));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // ------------------------------------------------------------------ menú

    /**
     * Infla el menú de opciones superior de la pantalla de detalle.
     * @param menu Objeto Menu.
     * @return true para mostrarlo.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        return true;
    }

    /**
     * Actualiza dinámicamente el ícono de favorita en la barra superior.
     * @param menu Objeto Menu.
     * @return true si fue procesado.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem fav = menu.findItem(R.id.accion_favorita);
        if (receta != null && fav != null) {
            fav.setIcon(receta.favorita ? R.drawable.ic_favorito : R.drawable.ic_favorito_borde);
            fav.setTitle(receta.favorita ? R.string.quitar_favorita : R.string.marcar_favorita);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Maneja las acciones del menú superior (favorita, editar, compartir, exportar a txt, eliminar).
     * @param item Elemento de menú presionado.
     * @return true si fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (receta == null) {
            return super.onOptionsItemSelected(item);
        }
        int id = item.getItemId();
        if (id == R.id.accion_favorita) {
            receta.favorita = dao.alternarFavorita(receta.id);
            aviso(getString(receta.favorita ? R.string.agregada_favoritas : R.string.quitada_favoritas));
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.accion_editar) {
            Intent i = new Intent(this, FormRecetaActivity.class);
            i.putExtra(FormRecetaActivity.EXTRA_ID, receta.id);
            startActivity(i);
            return true;
        } else if (id == R.id.accion_compartir) {
            Intent enviar = new Intent(Intent.ACTION_SEND);
            enviar.setType("text/plain");
            enviar.putExtra(Intent.EXTRA_SUBJECT, receta.nombre);
            enviar.putExtra(Intent.EXTRA_TEXT, Exportador.recetaTexto(receta, porciones));
            startActivity(Intent.createChooser(enviar, getString(R.string.compartir_receta)));
            return true;
        } else if (id == R.id.accion_exportar) {
            Intent crear = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            crear.addCategory(Intent.CATEGORY_OPENABLE);
            crear.setType("text/plain");
            crear.putExtra(Intent.EXTRA_TITLE, Formato.nombreArchivo(receta.nombre) + ".txt");
            try {
                startActivityForResult(crear, REQ_EXPORTAR);
            } catch (ActivityNotFoundException e) {
                aviso(getString(R.string.error_sin_app_archivos));
            }
            return true;
        } else if (id == R.id.accion_eliminar) {
            confirmarEliminar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar la receta de la base de datos.
     */
    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_receta)
                .setMessage(getString(R.string.confirmar_eliminar, receta.nombre))
                .setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String foto = dao.eliminar(receta.id);
                        FotoStorage.eliminar(DetalleRecetaActivity.this, foto);
                        aviso(getString(R.string.receta_eliminada));
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Recibe la URI seleccionada por el usuario para guardar el archivo exportado en .txt.
     * @param requestCode Código de solicitud.
     * @param resultCode Resultado de la actividad.
     * @param data Intent con la URI seleccionada.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXPORTAR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri destino = data.getData();
            if (receta == null) {
                receta = dao.obtener(recetaId);
                if (receta == null) {
                    return;
                }
            }
            try {
                Exportador.escribir(this, destino, Exportador.recetaTexto(receta, porciones));
                aviso(getString(R.string.receta_exportada));
            } catch (IOException e) {
                aviso(getString(R.string.error_exportar, e.getMessage()));
            }
        }
    }

    /**
     * Preserva el número de porciones seleccionadas ante giros de pantalla.
     * @param outState Bundle de salida.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ESTADO_PORCIONES, porciones);
    }
}
