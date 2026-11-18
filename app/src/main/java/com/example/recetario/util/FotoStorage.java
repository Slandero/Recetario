package com.example.recetario.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Persistencia con archivos: las fotos de las recetas se guardan como JPEG en el
 * almacenamiento interno de la app (/data/data/com.example.recetario/files/fotos).
 * En la base de datos solo se guarda el nombre del archivo.
 */
public final class FotoStorage {

    private static final String CARPETA = "fotos";
    private static final int LADO_MAXIMO = 1280;
    private static final int CALIDAD_JPEG = 85;

    private FotoStorage() {
    }

    public static File carpeta(Context c) {
        File dir = new File(c.getFilesDir(), CARPETA);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("No se pudo crear la carpeta de fotos");
        }
        return dir;
    }

    public static File archivo(Context c, String nombre) {
        return new File(carpeta(c), nombre);
    }

    /** Archivo temporal donde la app de cámara deja la foto (lo comparte FotoProvider). */
    public static File archivoCamara(Context c) {
        File dir = new File(c.getCacheDir(), "camara");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "captura.jpg");
    }

    /** Copia una imagen elegida de la galería al almacenamiento de la app. Regresa el nombre del archivo. */
    public static String guardarDesdeUri(Context c, Uri uri) throws IOException {
        File temporal = File.createTempFile("foto", ".tmp", c.getCacheDir());
        try {
            InputStream in = c.getContentResolver().openInputStream(uri);
            if (in == null) {
                throw new IOException("No se pudo abrir la imagen");
            }
            try (InputStream entrada = in; OutputStream salida = new FileOutputStream(temporal)) {
                copiar(entrada, salida);
            }
            return guardarDesdeArchivo(c, temporal);
        } finally {
            temporal.delete();
        }
    }

    /** Reduce, endereza (EXIF) y guarda como JPEG. Regresa el nombre del archivo. */
    public static String guardarDesdeArchivo(Context c, File origen) throws IOException {
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(origen.getPath(), opciones);
        if (opciones.outWidth <= 0 || opciones.outHeight <= 0) {
            throw new IOException("El archivo no es una imagen válida");
        }
        opciones.inSampleSize = muestreo(opciones.outWidth, opciones.outHeight, LADO_MAXIMO);
        opciones.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(origen.getPath(), opciones);
        if (bitmap == null) {
            throw new IOException("No se pudo leer la imagen");
        }

        int grados = gradosExif(origen.getPath());
        if (grados != 0) {
            Matrix m = new Matrix();
            m.postRotate(grados);
            Bitmap girado = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (girado != bitmap) {
                bitmap.recycle();
                bitmap = girado;
            }
        }

        String nombre = "receta_" + System.currentTimeMillis() + ".jpg";
        File destino = archivo(c, nombre);
        try (FileOutputStream salida = new FileOutputStream(destino)) {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)) {
                throw new IOException("No se pudo guardar la foto");
            }
        } finally {
            bitmap.recycle();
        }
        return nombre;
    }

    /** Carga la foto reducida al tamaño que se va a mostrar (para no gastar memoria). */
    public static Bitmap cargar(Context c, String nombre, int ladoPx) {
        if (nombre == null) {
            return null;
        }
        File f = archivo(c, nombre);
        if (!f.exists()) {
            return null;
        }
        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f.getPath(), opciones);
        opciones.inSampleSize = muestreo(opciones.outWidth, opciones.outHeight, ladoPx);
        opciones.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(f.getPath(), opciones);
    }

    public static void eliminar(Context c, String nombre) {
        if (nombre != null) {
            archivo(c, nombre).delete();
        }
    }

    public static int contarFotos(Context c) {
        File[] archivos = carpeta(c).listFiles();
        return archivos == null ? 0 : archivos.length;
    }

    public static long tamanoTotal(Context c) {
        long total = 0;
        File[] archivos = carpeta(c).listFiles();
        if (archivos != null) {
            for (File f : archivos) {
                total += f.length();
            }
        }
        return total;
    }

    private static int muestreo(int ancho, int alto, int ladoMaximo) {
        int muestra = 1;
        while (Math.max(ancho, alto) / (muestra * 2) >= ladoMaximo) {
            muestra *= 2;
        }
        return muestra;
    }

    private static int gradosExif(String ruta) {
        try {
            ExifInterface exif = new ExifInterface(ruta);
            int orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientacion) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    private static void copiar(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int leidos;
        while ((leidos = in.read(buffer)) != -1) {
            out.write(buffer, 0, leidos);
        }
    }
}
