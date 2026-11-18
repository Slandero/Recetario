package com.example.recetario.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatoTest {

    @Test
    public void numerosEnterosYFracciones() {
        assertEquals("3", Formato.numero(3));
        assertEquals("1½", Formato.numero(1.5));
        assertEquals("¼", Formato.numero(0.25));
        assertEquals("¾", Formato.numero(0.75));
        assertEquals("2⅔", Formato.numero(2.6667));
        assertEquals("1.3", Formato.numero(1.3));
        assertEquals("6", Formato.numero(5.995));
    }

    @Test
    public void cantidadesConvierteUnidades() {
        assertEquals("1½ kg", Formato.cantidad(1500, "g"));
        assertEquals("500 g", Formato.cantidad(0.5, "kg"));
        assertEquals("1½ l", Formato.cantidad(1.5, "l"));
        assertEquals("250 ml", Formato.cantidad(0.25, "l"));
        assertEquals("333 g", Formato.cantidad(333.33, "g"));
        assertEquals("3 pz", Formato.cantidad(3, "pz"));
        assertEquals("2 dientes", Formato.cantidad(2, "diente"));
        assertEquals("1 diente", Formato.cantidad(1, "diente"));
        assertEquals("½ taza", Formato.cantidad(0.5, "taza"));
        assertEquals("al gusto", Formato.cantidad(0, "al gusto"));
    }

    @Test
    public void tiempoYNombreDeArchivo() {
        assertEquals("45 min", Formato.tiempo(45));
        assertEquals("2 h", Formato.tiempo(120));
        assertEquals("2 h 30 min", Formato.tiempo(150));
        assertEquals("chile_en_nogada", Formato.nombreArchivo("Chile en Nogada"));
        assertEquals("cafe_de_olla", Formato.nombreArchivo("¡Café de Olla!"));
        assertEquals("0.5", Formato.decimal(0.5));
        assertEquals("387", Formato.decimal(387));
    }
}
