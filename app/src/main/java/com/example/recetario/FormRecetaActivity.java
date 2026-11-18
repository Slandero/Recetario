package com.example.recetario;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.recetario.datos.RecetaDao;
import com.example.recetario.modelo.Categoria;
import com.example.recetario.modelo.IngredienteReceta;
import com.example.recetario.modelo.Receta;
import com.example.recetario.util.FotoProvider;
import com.example.recetario.util.FotoStorage;
import com.example.recetario.util.Formato;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Crear o editar una receta: datos, foto, ingredientes y preparación. */
public class FormRecetaActivity extends BaseActivity {

    public static final String EXTRA_ID = "receta_id";
    private static final int REQ_GALERIA = 20;
    private static final int REQ_CAMARA = 21;
    private static final String ESTADO_FOTO_ORIGINAL = "foto_original";
    private static final String ESTADO_FOTO_ACTUAL = "foto_actual";
    private static final String ESTADO_ING_NOMBRES = "ing_nombres";
    private static final String ESTADO_ING_CANTIDADES = "ing_cantidades";
    private static final String ESTADO_ING_UNIDADES = "ing_unidades";

    private RecetaDao dao;
    private long recetaId = -1;
    /** Foto que la receta ya tenía guardada en la BD. */
    private String fotoOriginal;
    /** Foto que se va a guardar (puede ser una nueva, la original o ninguna). */
    private String fotoActual;
    private boolean guardado;

    private FrameLayout marcoFoto;
    private ImageView ivFoto;
    private View vacioFoto;
    private EditText etNombre;
    private Spinner spCategoria;
    private Spinner spDificultad;
    private EditText etTiempo;
    private EditText etPorciones;
    private LinearLayout contIngredientes;
    private EditText etPreparacion;

    private List<Categoria> categorias;
    private ArrayAdapter<String> adNombresIngredientes;
    private String[] unidades;

    /**
     * Inicializa las vistas del formulario de recetas, adaptadores de Spinners y listeners.
     * Carga los datos existentes si se trata de una edición o asigna porciones predeterminadas si es nueva.
     * @param savedInstanceState Estado guardado en cambios de configuración.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_receta);
        dao = new RecetaDao(this);
        recetaId = getIntent().getLongExtra(EXTRA_ID, -1);

        marcoFoto = (FrameLayout) findViewById(R.id.marcoFoto);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        vacioFoto = findViewById(R.id.vacioFoto);
        etNombre = (EditText) findViewById(R.id.etNombre);
        spCategoria = (Spinner) findViewById(R.id.spCategoria);
        spDificultad = (Spinner) findViewById(R.id.spDificultad);
        etTiempo = (EditText) findViewById(R.id.etTiempo);
        etPorciones = (EditText) findViewById(R.id.etPorciones);
        contIngredientes = (LinearLayout) findViewById(R.id.contIngredientes);
        etPreparacion = (EditText) findViewById(R.id.etPreparacion);

        categorias = dao.categorias();
        ArrayAdapter<Categoria> adCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adCat);

        ArrayAdapter<CharSequence> adDif = ArrayAdapter.createFromResource(
                this, R.array.dificultades, android.R.layout.simple_spinner_item);
        adDif.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adDif);

        unidades = getResources().getStringArray(R.array.unidades);
        adNombresIngredientes = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, dao.nombresIngredientes());

        marcoFoto.setClipToOutline(true);
        marcoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesFoto();
            }
        });
        findViewById(R.id.btnAgregarIngrediente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View fila = agregarFila(null);
                fila.findViewById(R.id.actvNombre).requestFocus();
            }
        });
        findViewById(R.id.btnGuardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        boolean restaurar = savedInstanceState != null && savedInstanceState.containsKey(ESTADO_ING_NOMBRES);
        if (recetaId > 0) {
            setTitle(R.string.editar_receta);
            cargar(!restaurar);
        } else {
            setTitle(R.string.nueva_receta);
            etPorciones.setText(String.valueOf(prefs.getPorciones()));
            if (!restaurar) {
                for (int i = 0; i < 3; i++) {
                    agregarFila(null);
                }
            }
        }
        if (savedInstanceState != null) {
            fotoOriginal = savedInstanceState.getString(ESTADO_FOTO_ORIGINAL);
            fotoActual = savedInstanceState.getString(ESTADO_FOTO_ACTUAL);
        }
        if (restaurar) {
            restaurarFilas(savedInstanceState);
        }
        mostrarFoto();
    }

    /**
     * Carga los datos de una receta existente para su edición.
     * @param conIngredientes Indica si también se deben poblar los renglones de ingredientes desde la BD.
     */
    private void cargar(boolean conIngredientes) {
        Receta r = dao.obtener(recetaId);
        if (r == null) {
            finish();
            return;
        }
        etNombre.setText(r.nombre);
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).id == r.categoriaId) {
                spCategoria.setSelection(i);
            }
        }
        spDificultad.setSelection(r.dificultad - 1);
        etTiempo.setText(String.valueOf(r.tiempoMin));
        etPorciones.setText(String.valueOf(r.porciones));
        etPreparacion.setText(r.preparacion);
        fotoOriginal = r.foto;
        fotoActual = r.foto;
        if (conIngredientes) {
            for (IngredienteReceta ir : r.ingredientes) {
                agregarFila(ir);
            }
        }
    }

    /**
     * Restaura los renglones de ingredientes previamente escritos tras una recreación de la pantalla.
     * @param estado Bundle con las listas de nombres, cantidades y unidades.
     */
    private void restaurarFilas(Bundle estado) {
        ArrayList<String> nombres = estado.getStringArrayList(ESTADO_ING_NOMBRES);
        ArrayList<String> cantidades = estado.getStringArrayList(ESTADO_ING_CANTIDADES);
        ArrayList<String> unidadesGuardadas = estado.getStringArrayList(ESTADO_ING_UNIDADES);
        if (nombres == null || cantidades == null || unidadesGuardadas == null) {
            return;
        }
        for (int i = 0; i < nombres.size(); i++) {
            View fila = agregarFila(null);
            ((AutoCompleteTextView) fila.findViewById(R.id.actvNombre)).setText(nombres.get(i));
            ((EditText) fila.findViewById(R.id.etCantidad)).setText(cantidades.get(i));
            Spinner sp = (Spinner) fila.findViewById(R.id.spUnidad);
            for (int u = 0; u < unidades.length; u++) {
                if (unidades[u].equals(unidadesGuardadas.get(i))) {
                    sp.setSelection(u);
                }
            }
        }
    }

    /**
     * Infla e inserta un nuevo renglón de ingrediente dinámico en el contenedor de la pantalla.
     * @param ir Ingrediente con datos iniciales (null para renglón vacío).
     * @return La vista del renglón creado.
     */
    private View agregarFila(IngredienteReceta ir) {
        final View fila = getLayoutInflater().inflate(R.layout.item_ingrediente_form, contIngredientes, false);
        AutoCompleteTextView actvNombre = (AutoCompleteTextView) fila.findViewById(R.id.actvNombre);
        EditText etCantidad = (EditText) fila.findViewById(R.id.etCantidad);
        Spinner spUnidad = (Spinner) fila.findViewById(R.id.spUnidad);

        actvNombre.setAdapter(adNombresIngredientes);
        ArrayAdapter<String> adUnidad = new ArrayAdapter<>(this, R.layout.spinner_compacto, unidades);
        adUnidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adUnidad);

        if (ir != null) {
            actvNombre.setText(ir.nombre);
            if (ir.cantidad > 0) {
                etCantidad.setText(Formato.decimal(ir.cantidad));
            }
            for (int i = 0; i < unidades.length; i++) {
                if (unidades[i].equals(ir.unidad)) {
                    spUnidad.setSelection(i);
                }
            }
        }

        fila.findViewById(R.id.btnQuitar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contIngredientes.removeView(fila);
            }
        });
        contIngredientes.addView(fila);
        return fila;
    }

    // ------------------------------------------------------------------ foto

    /**
     * Muestra un diálogo de opciones para la fotografía (Tomar foto con cámara, Elegir de galería o Quitar foto).
     */
    private void opcionesFoto() {
        List<String> opciones = new ArrayList<>();
        opciones.add(getString(R.string.tomar_foto));
        opciones.add(getString(R.string.elegir_galeria));
        if (fotoActual != null) {
            opciones.add(getString(R.string.quitar_foto));
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.foto_receta)
                .setItems(opciones.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            abrirCamara();
                        } else if (which == 1) {
                            abrirGaleria();
                        } else {
                            reemplazarFoto(null);
                        }
                    }
                })
                .show();
    }

    /**
     * Inicia el Intent para capturar una imagen usando la aplicación de cámara nativa.
     */
    private void abrirCamara() {
        File destino = FotoStorage.archivoCamara(this);
        destino.delete();
        Uri uri = FotoProvider.uriCamara(this);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        i.setClipData(ClipData.newRawUri("foto", uri));
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(i, REQ_CAMARA);
        } catch (ActivityNotFoundException e) {
            aviso(getString(R.string.error_sin_camara));
        }
    }

    /**
     * Inicia el Intent para seleccionar una imagen desde la galería.
     */
    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(i, REQ_GALERIA);
        } catch (ActivityNotFoundException e) {
            aviso(getString(R.string.error_sin_galeria));
        }
    }

    /**
     * Procesa la respuesta tras capturar con la cámara o seleccionar de la galería.
     * @param requestCode Código de petición.
     * @param resultCode Código de resultado.
     * @param data Intent con los datos de respuesta.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQ_GALERIA && data != null && data.getData() != null) {
            procesarFoto(data.getData(), null);
        } else if (requestCode == REQ_CAMARA) {
            File archivo = FotoStorage.archivoCamara(this);
            if (archivo.length() > 0) {
                procesarFoto(null, archivo);
            } else {
                aviso(getString(R.string.error_foto));
            }
        }
    }

    /**
     * Ejecuta la compresión, escalado y rotación de la fotografía en un hilo en segundo plano.
     * @param uri URI de la galería (null si proviene de la cámara).
     * @param archivo Archivo de la cámara (null si proviene de la galería).
     */
    private void procesarFoto(final Uri uri, final File archivo) {
        aviso(getString(R.string.guardando_foto));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String nombre = null;
                try {
                    nombre = uri != null
                            ? FotoStorage.guardarDesdeUri(FormRecetaActivity.this, uri)
                            : FotoStorage.guardarDesdeArchivo(FormRecetaActivity.this, archivo);
                } catch (IOException | RuntimeException e) {
                    nombre = null;
                } finally {
                    if (archivo != null) {
                        archivo.delete();
                    }
                }
                final String resultado = nombre;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing() || isDestroyed()) {
                            FotoStorage.eliminar(FormRecetaActivity.this, resultado);
                            return;
                        }
                        if (resultado == null) {
                            aviso(getString(R.string.error_foto));
                        } else {
                            reemplazarFoto(resultado);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Reemplaza la referencia a la foto actual por una nueva y elimina la temporal previa.
     * @param nueva Nombre del nuevo archivo de foto (o null para quitar).
     */
    private void reemplazarFoto(String nueva) {
        if (fotoActual != null && !fotoActual.equals(fotoOriginal)) {
            FotoStorage.eliminar(this, fotoActual);
        }
        fotoActual = nueva;
        mostrarFoto();
    }

    /**
     * Muestra la foto actual en el ImageView o restablece el contenedor indicando ausencia de foto.
     */
    private void mostrarFoto() {
        Bitmap bmp = FotoStorage.cargar(this, fotoActual, getResources().getDisplayMetrics().widthPixels);
        if (bmp != null) {
            ivFoto.setImageBitmap(bmp);
            ivFoto.setVisibility(View.VISIBLE);
            vacioFoto.setVisibility(View.GONE);
        } else {
            ivFoto.setImageDrawable(null);
            ivFoto.setVisibility(View.GONE);
            vacioFoto.setVisibility(View.VISIBLE);
        }
    }

    // ------------------------------------------------------------------ guardar

    /**
     * Valida los campos del formulario, construye el objeto Receta y lo guarda en la BD mediante el DAO.
     */
    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError(getString(R.string.error_nombre));
            etNombre.requestFocus();
            return;
        }
        int porciones = entero(etPorciones, 0);
        if (porciones < 1) {
            etPorciones.setError(getString(R.string.error_porciones));
            etPorciones.requestFocus();
            return;
        }
        int tiempo = entero(etTiempo, 0);

        List<IngredienteReceta> ingredientes = new ArrayList<>();
        for (int i = 0; i < contIngredientes.getChildCount(); i++) {
            View fila = contIngredientes.getChildAt(i);
            AutoCompleteTextView actv = (AutoCompleteTextView) fila.findViewById(R.id.actvNombre);
            EditText etCant = (EditText) fila.findViewById(R.id.etCantidad);
            Spinner spUni = (Spinner) fila.findViewById(R.id.spUnidad);
            String ing = actv.getText().toString().trim();
            String textoCant = etCant.getText().toString().trim().replace(',', '.');
            String unidad = (String) spUni.getSelectedItem();
            if (ing.isEmpty() && textoCant.isEmpty()) {
                continue;
            }
            if (ing.isEmpty()) {
                actv.setError(getString(R.string.error_ingrediente));
                actv.requestFocus();
                return;
            }
            double cantidad = 0;
            if (!Formato.AL_GUSTO.equals(unidad)) {
                try {
                    cantidad = Double.parseDouble(textoCant);
                } catch (NumberFormatException e) {
                    cantidad = -1;
                }
                if (cantidad <= 0) {
                    etCant.setError(getString(R.string.error_cantidad));
                    etCant.requestFocus();
                    return;
                }
            }
            ingredientes.add(new IngredienteReceta(ing, cantidad, unidad));
        }
        if (ingredientes.isEmpty()) {
            aviso(getString(R.string.error_sin_ingredientes));
            return;
        }
        String preparacion = etPreparacion.getText().toString().trim();
        if (preparacion.isEmpty()) {
            etPreparacion.setError(getString(R.string.error_preparacion));
            etPreparacion.requestFocus();
            return;
        }

        Receta r = new Receta();
        r.id = recetaId;
        r.nombre = nombre;
        r.categoriaId = ((Categoria) spCategoria.getSelectedItem()).id;
        r.dificultad = spDificultad.getSelectedItemPosition() + 1;
        r.tiempoMin = tiempo;
        r.porciones = porciones;
        r.preparacion = preparacion;
        r.foto = fotoActual;
        r.ingredientes = ingredientes;

        long id;
        try {
            id = dao.guardar(r);
        } catch (SQLException e) {
            aviso(getString(R.string.error_guardar, e.getMessage()));
            return;
        }
        if (fotoOriginal != null && !fotoOriginal.equals(fotoActual)) {
            FotoStorage.eliminar(this, fotoOriginal);
        }
        guardado = true;
        aviso(getString(R.string.receta_guardada));
        if (recetaId <= 0) {
            Intent i = new Intent(this, DetalleRecetaActivity.class);
            i.putExtra(DetalleRecetaActivity.EXTRA_ID, id);
            startActivity(i);
        }
        finish();
    }

    /**
     * Parsea un entero desde un EditText o retorna un valor por defecto si falla.
     * @param et EditText con el texto.
     * @param porDefecto Valor devuelto si ocurre un error.
     * @return El valor entero parseado.
     */
    private static int entero(EditText et, int porDefecto) {
        try {
            return Integer.parseInt(et.getText().toString().trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    /**
     * Infla el menú de la barra superior para guardar la receta.
     * @param menu Menú a inflar.
     * @return true para mostrarlo.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    /**
     * Maneja el clic en la opción de guardar del menú superior.
     * @param item Elemento seleccionado.
     * @return true si fue procesado.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accion_guardar) {
            guardar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Preserva el estado de la foto y los ingredientes capturados en el formulario ante giros o pausado.
     * @param outState Bundle de salida.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_FOTO_ORIGINAL, fotoOriginal);
        outState.putString(ESTADO_FOTO_ACTUAL, fotoActual);
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<String> cantidades = new ArrayList<>();
        ArrayList<String> unidadesFila = new ArrayList<>();
        for (int i = 0; i < contIngredientes.getChildCount(); i++) {
            View fila = contIngredientes.getChildAt(i);
            nombres.add(((AutoCompleteTextView) fila.findViewById(R.id.actvNombre)).getText().toString());
            cantidades.add(((EditText) fila.findViewById(R.id.etCantidad)).getText().toString());
            Object unidad = ((Spinner) fila.findViewById(R.id.spUnidad)).getSelectedItem();
            unidadesFila.add(unidad == null ? "" : unidad.toString());
        }
        outState.putStringArrayList(ESTADO_ING_NOMBRES, nombres);
        outState.putStringArrayList(ESTADO_ING_CANTIDADES, cantidades);
        outState.putStringArrayList(ESTADO_ING_UNIDADES, unidadesFila);
    }

    /**
     * Limpia fotos temporales huérfanas si la actividad finaliza sin haber guardado los cambios.
     */
    @Override
    protected void onDestroy() {
        if (isFinishing() && !guardado && fotoActual != null && !fotoActual.equals(fotoOriginal)) {
            FotoStorage.eliminar(this, fotoActual);
        }
        super.onDestroy();
    }
}
