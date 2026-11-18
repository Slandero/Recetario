package com.example.recetario.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SemanaTest {

    @Test
    public void lunesDeCualquierDia() {
        assertEquals("2026-10-26", Semana.lunesDe("2026-10-26")); // lunes
        assertEquals("2026-10-26", Semana.lunesDe("2026-10-29")); // jueves
        assertEquals("2026-10-26", Semana.lunesDe("2026-11-01")); // domingo
        assertEquals("2026-12-28", Semana.lunesDe("2027-01-02")); // cambio de año
    }

    @Test
    public void diasYEtiquetas() {
        String[] dias = Semana.dias("2026-10-26");
        assertEquals(7, dias.length);
        assertEquals("2026-11-01", dias[6]);
        assertEquals("Lunes 26", Semana.nombreDia("2026-10-26"));
        assertEquals("domingo 1 nov", Semana.fechaCorta("2026-11-01"));
        assertEquals("26 oct – 1 nov 2026", Semana.etiqueta("2026-10-26"));
    }
}
