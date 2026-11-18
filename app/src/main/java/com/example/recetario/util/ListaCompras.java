package com.example.recetario.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Junta los ingredientes de las comidas planeadas en una sola lista.
 * La consulta SQL ya suma por ingrediente y unidad; aquí se combinan
 * g con kg y ml con l para que un ingrediente salga en un solo renglón.
 */
public final class ListaCompras {

    /** Un renglón tal como sale de la consulta (ingrediente + unidad + total). */
    public static final class Fila {
        public final String ingrediente;
        public final String unidad;
        public final double cantidad;
        public final String recetas;

        public Fila(String ingrediente, String unidad, double cantidad, String recetas) {
            this.ingrediente = ingrediente;
            this.unidad = unidad;
            this.cantidad = cantidad;
            this.recetas = recetas;
        }
    }

    /** Un renglón ya listo para mostrar en la lista. */
    public static final class Item {
        public final String ingrediente;
        public final String cantidades;
        public final String recetas;

        public Item(String ingrediente, String cantidades, String recetas) {
            this.ingrediente = ingrediente;
            this.cantidades = cantidades;
            this.recetas = recetas;
        }

        /** Clave para guardar si ya se compró (no depende de mayúsculas). */
        public String clave() {
            return ingrediente.toLowerCase(Locale.ROOT);
        }
    }

    private static final class Acumulado {
        String nombre;
        double gramos;
        double mililitros;
        boolean alGusto;
        final Map<String, Double> otras = new LinkedHashMap<>();
        final Set<String> recetas = new LinkedHashSet<>();
    }

    private ListaCompras() {
    }

    public static List<Item> agrupar(List<Fila> filas) {
        Map<String, Acumulado> mapa = new LinkedHashMap<>();
        for (Fila f : filas) {
            String clave = f.ingrediente.toLowerCase(Locale.ROOT);
            Acumulado a = mapa.get(clave);
            if (a == null) {
                a = new Acumulado();
                a.nombre = f.ingrediente;
                mapa.put(clave, a);
            }
            if (Formato.AL_GUSTO.equals(f.unidad) || f.cantidad <= 0) {
                a.alGusto = true;
            } else if ("g".equals(f.unidad)) {
                a.gramos += f.cantidad;
            } else if ("kg".equals(f.unidad)) {
                a.gramos += f.cantidad * 1000;
            } else if ("ml".equals(f.unidad)) {
                a.mililitros += f.cantidad;
            } else if ("l".equals(f.unidad)) {
                a.mililitros += f.cantidad * 1000;
            } else {
                Double previo = a.otras.get(f.unidad);
                a.otras.put(f.unidad, (previo == null ? 0 : previo) + f.cantidad);
            }
            if (f.recetas != null) {
                for (String r : f.recetas.split(",")) {
                    if (!r.trim().isEmpty()) {
                        a.recetas.add(r.trim());
                    }
                }
            }
        }

        List<Item> items = new ArrayList<>();
        for (Acumulado a : mapa.values()) {
            List<String> partes = new ArrayList<>();
            if (a.gramos > 0) {
                partes.add(Formato.cantidad(a.gramos, "g"));
            }
            if (a.mililitros > 0) {
                partes.add(Formato.cantidad(a.mililitros, "ml"));
            }
            for (Map.Entry<String, Double> e : a.otras.entrySet()) {
                partes.add(Formato.cantidad(e.getValue(), e.getKey()));
            }
            if (a.alGusto) {
                partes.add(Formato.AL_GUSTO);
            }
            items.add(new Item(a.nombre, unir(partes, " + "), unir(new ArrayList<>(a.recetas), ", ")));
        }

        final Collator collator = Collator.getInstance(new Locale("es", "MX"));
        collator.setStrength(Collator.PRIMARY);
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item x, Item y) {
                return collator.compare(x.ingrediente, y.ingrediente);
            }
        });
        return items;
    }

    private static String unir(List<String> partes, String separador) {
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (sb.length() > 0) {
                sb.append(separador);
            }
            sb.append(p);
        }
        return sb.toString();
    }
}
