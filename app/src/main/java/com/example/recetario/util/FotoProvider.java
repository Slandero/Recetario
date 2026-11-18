package com.example.recetario.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Proveedor mínimo para que la app de cámara pueda escribir la foto en un archivo
 * de la app sin pedir permisos de almacenamiento. Solo comparte un archivo:
 * cache/camara/captura.jpg
 */
public class FotoProvider extends ContentProvider {

    /**
     * Retorna la URI del proveedor de contenido para compartir el archivo de la cámara.
     * @param c Contexto de la aplicación.
     * @return Objeto Uri accesible por la app de cámara.
     */
    public static Uri uriCamara(Context c) {
        return Uri.parse("content://" + c.getPackageName() + ".fotos/captura.jpg");
    }

    /**
     * Inicialización del proveedor de contenido.
     * @return true si se inicializa correctamente.
     */
    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Abre y retorna el descriptor del archivo especificado por la URI.
     * @param uri URI solicitada por la aplicación externa de cámara.
     * @param mode Modo de acceso (lectura/escritura).
     * @return ParcelFileDescriptor del archivo temporal de la cámara.
     * @throws FileNotFoundException Si el recurso no es captura.jpg.
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"captura.jpg".equals(uri.getLastPathSegment())) {
            throw new FileNotFoundException(uri.toString());
        }
        File archivo = FotoStorage.archivoCamara(getContext());
        return ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.parseMode(mode));
    }

    /**
     * Retorna el tipo MIME del archivo compartido (image/jpeg).
     * @param uri URI solicitada.
     * @return Tipo MIME en cadena.
     */
    @Override
    public String getType(Uri uri) {
        return "image/jpeg";
    }

    /**
     * Consulta de metadatos básicos del archivo compartido (nombre y tamaño).
     * @param uri URI del recurso.
     * @param projection Columnas solicitadas.
     * @param selection Selección SQL.
     * @param selectionArgs Argumentos de selección.
     * @param sortOrder Orden de resultados.
     * @return Cursor con los metadatos del archivo.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        File archivo = FotoStorage.archivoCamara(getContext());
        MatrixCursor cursor = new MatrixCursor(new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE});
        cursor.addRow(new Object[]{archivo.getName(), archivo.length()});
        return cursor;
    }

    /**
     * Inserción no soportada en este proveedor simplificado.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    /**
     * Borrado de archivos del proveedor.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Actualización de registros del proveedor.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
