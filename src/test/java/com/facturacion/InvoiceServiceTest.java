package com.facturacion;

import com.facturacion.model.*;
import com.facturacion.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias, validamos los 3 casos:
// 1. Calculo correcto de subtotal
// 2. Calculo correcto del total con IVA
// 3. Filtrado del historial por fecha
public class InvoiceServiceTest {

    private InvoiceService servicio;

    // Se ejecuta antes de cada prueba
    @BeforeEach
    void setUp() {
        servicio = new InvoiceService();
    }

    // ---------------------------------------------------------------
    // Prueba 1: el subtotal debe ser la suma de (cantidad × precio)
    // ---------------------------------------------------------------
    @Test
    void testCalcularSubtotalCorrecto() {
        //buscamos un producto real del catalogo
        Product producto = servicio.buscarProductoPorSku("SKU-003").get(); // $950
        List<InvoiceItem> items = List.of(new InvoiceItem(producto, 3));

        double subtotal = servicio.calcularSubtotal(items);

        // 3 × 950 = 2850
        assertEquals(2850.00, subtotal, 0.001,
                "El subtotal deberia ser 2850.00");
    }

    // ---------------------------------------------------------------
    // Prueba 2:  El total debe ser subtotal × (1 + 0.16)
    // ---------------------------------------------------------------
    @Test
    void testCalcularTotalConIva() {
        Product producto = servicio.buscarProductoPorSku("SKU-001").get(); // $2500
        List<InvoiceItem> items = List.of(new InvoiceItem(producto, 2));

        double total = servicio.calcularTotal(items);

        // Subtotal = 2 × 2500 = 5000
        // Total    = 5000 × 1.16 = 5800
        assertEquals(5800.00, total, 0.001,
                "El total con IVA deberia ser 5800.00");
    }

    // ---------------------------------------------------------------
    // Prueba 3: El filtrado por fecha devuelve solo las facturas
    //           que caen dentro del rango indicado
    // ---------------------------------------------------------------
    @Test
    void testFiltradoHistorialPorFecha() {
        // Generamos una factura (queda guardada con fecha de hoy)
        Customer cliente  = servicio.buscarClientePorId("C001").get();
        Product  producto = servicio.buscarProductoPorSku("SKU-002").get();
        List<InvoiceItem> items = List.of(new InvoiceItem(producto, 1));

        servicio.generarFactura(cliente, items);

        // Filtramos por hoy +- 1 dia para asegurarnos de incluirla
        LocalDate desde = LocalDate.now().minusDays(1);
        LocalDate hasta = LocalDate.now().plusDays(1);

        List<Invoice> resultado = servicio.filtrarPorFecha(desde, hasta);

        // Debe haber al menos 1 factura en ese rango
        assertFalse(resultado.isEmpty(),
                "Deberia haber al menos una factura en el rango de fechas");

        // in rango en el pasado no debe devolver nada
        List<Invoice> sinResultado = servicio.filtrarPorFecha(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 12, 31)
        );
        assertTrue(sinResultado.isEmpty(),
                "No debe haber facturas en un rango pasado");
    }
}
