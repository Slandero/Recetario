package com.example.recetario;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recetario.modelo.Receta;
import com.example.recetario.util.FotoStorage;
import com.example.recetario.util.Formato;
import com.example.recetario.util.Vistas;

import java.util.ArrayList;
import java.util.List;

/** Adaptador de la lista de recetas de la pantalla principal. */
public class RecetaAdapter extends BaseAdapter {

    public interface OnFavoritaListener {
        void onFavorita(Receta receta);
    }

    private final Context contexto;
    private final LayoutInflater inflater;
    private final OnFavoritaListener listener;
    private final int ladoFoto;
    private final float radio;
    private List<Receta> recetas = new ArrayList<>();

    /** Miniaturas ya cargadas para no leer el archivo cada vez que se hace scroll. */
    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
        @Override
        protected int sizeOf(String clave, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };

    public RecetaAdapter(Context contexto, OnFavoritaListener listener) {
        this.contexto = contexto;
        this.inflater = LayoutInflater.from(contexto);
        this.listener = listener;
        float densidad = contexto.getResources().getDisplayMetrics().density;
        this.ladoFoto = Math.round(64 * densidad);
        this.radio = 14 * densidad;
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recetas.size();
    }

    @Override
    public Receta getItem(int position) {
        return recetas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return recetas.get(position).id;
    }

    private static class Holder {
        FrameLayout marco;
        ImageView foto;
        TextView inicial;
        TextView nombre;
        TextView categoria;
        TextView info;
        ImageButton favorita;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_receta, parent, false);
            h = new Holder();
            h.marco = (FrameLayout) convertView.findViewById(R.id.marcoFoto);
            h.foto = (ImageView) convertView.findViewById(R.id.ivFoto);
            h.inicial = (TextView) convertView.findViewById(R.id.tvInicial);
            h.nombre = (TextView) convertView.findViewById(R.id.tvNombre);
            h.categoria = (TextView) convertView.findViewById(R.id.tvCategoria);
            h.info = (TextView) convertView.findViewById(R.id.tvInfo);
            h.favorita = (ImageButton) convertView.findViewById(R.id.btnFavorita);
            h.marco.setClipToOutline(true);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }

        final Receta r = recetas.get(position);
        h.nombre.setText(r.nombre);
        h.categoria.setText(r.categoria + " · " + Receta.textoDificultad(r.dificultad));
        h.info.setText(contexto.getString(R.string.info_receta,
                Formato.tiempo(r.tiempoMin), r.porciones, r.numIngredientes));

        int color = Vistas.color(r.colorCategoria, 0xFF5B4F8F);
        h.marco.setBackground(Vistas.redondeado(color, radio));
        Bitmap bmp = miniatura(r.foto);
        if (bmp != null) {
            h.foto.setImageBitmap(bmp);
            h.foto.setVisibility(View.VISIBLE);
            h.inicial.setVisibility(View.GONE);
        } else {
            h.foto.setImageDrawable(null);
            h.foto.setVisibility(View.GONE);
            h.inicial.setVisibility(View.VISIBLE);
            h.inicial.setText(Vistas.inicial(r.nombre));
        }

        h.favorita.setImageResource(r.favorita ? R.drawable.ic_favorito : R.drawable.ic_favorito_borde);
        h.favorita.setContentDescription(contexto.getString(
                r.favorita ? R.string.quitar_favorita : R.string.marcar_favorita));
        h.favorita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFavorita(r);
            }
        });
        return convertView;
    }

    private Bitmap miniatura(String foto) {
        if (foto == null) {
            return null;
        }
        Bitmap bmp = cache.get(foto);
        if (bmp == null) {
            bmp = FotoStorage.cargar(contexto, foto, ladoFoto);
            if (bmp != null) {
                cache.put(foto, bmp);
            }
        }
        return bmp;
    }
}
