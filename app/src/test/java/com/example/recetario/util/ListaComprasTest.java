package com.example.recetario.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ListaComprasTest {

    @Test
    public void juntaGramosConKilosYOrdenaPorNombre() {
        List<ListaCompras.Item> items = ListaCompras.agrupar(Arrays.asList(
                new ListaCompras.Fila("Pechuga de pollo", "g", 500, "Enchiladas Verdes"),
                new ListaCompras.Fila("pechuga de pollo", "kg", 0.75, "Sopa de Lima"),
                new ListaCompras.Fila("Aguacate", "pz", 3, "Guacamole con Totopos"),
                new ListaCompras.Fila("Sal", "cda", 1, "Tacos de Carnitas"),
                new ListaCompras.Fila("Sal", "al gusto", 0, "Mole Poblano,Enchiladas Verdes")));

        assertEquals(3, items.size());
        assertEquals("Aguacate", items.get(0).ingrediente);
        assertEquals("1¼ kg", items.get(1).cantidades);
        assertEquals("Enchiladas Verdes, Sopa de Lima", items.get(1).recetas);
        assertEquals("1 cda + al gusto", items.get(2).cantidades);
        assertEquals("pechuga de pollo", items.get(1).clave());
    }

    @Test
    public void ordenaConAcentos() {
        List<ListaCompras.Item> items = ListaCompras.agrupar(Arrays.asList(
                new ListaCompras.Fila("Azúcar", "g", 100, "Flan Napolitano"),
                new ListaCompras.Fila("Ajonjolí", "cda", 3, "Mole Poblano"),
                new ListaCompras.Fila("Ácido", "pz", 1, "x")));
        assertEquals("Ácido", items.get(0).ingrediente);
        assertEquals("Ajonjolí", items.get(1).ingrediente);
        assertEquals("Azúcar", items.get(2).ingrediente);
    }
}
