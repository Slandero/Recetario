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

    public static Uri uriCamara(Context c) {
        return Uri.parse("content://" + c.getPackageName() + ".fotos/captura.jpg");
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"captura.jpg".equals(uri.getLastPathSegment())) {
            throw new FileNotFoundException(uri.toString());
        }
        File archivo = FotoStorage.archivoCamara(getContext());
        return ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.parseMode(mode));
    }

    @Override
    public String getType(Uri uri) {
        return "image/jpeg";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        File archivo = FotoStorage.archivoCamara(getContext());
        MatrixCursor cursor = new MatrixCursor(new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE});
        cursor.addRow(new Object[]{archivo.getName(), archivo.length()});
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
