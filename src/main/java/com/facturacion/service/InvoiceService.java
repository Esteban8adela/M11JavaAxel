package com.facturacion.service;

import com.facturacion.model.*;
import com.facturacion.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// servisio principal: aqui va toda la logica de negocio
public class InvoiceService {

    // catalogos inmutables con List.of() — al menos 5 clientes y 8 productos
    private final List<Customer> catalogoClientes = List.of(
        new Customer("C001", "Carlos Mendoza",    "carlos@empresa.com"),
        new Customer("C002", "Ana García",        "ana@empresa.com"),
        new Customer("C003", "Luis Torres",       "luis@empresa.com"),
        new Customer("C004", "María López",       "maria@empresa.com"),
        new Customer("C005", "Jorge Ramírez",     "jorge@empresa.com")
    );

    private final List<Product> catalogoProductos = List.of(
        new Product("SKU-001", "Licencia Windows",      2500.00, ProductCategory.LICENCIA),
        new Product("SKU-002", "Consultoría Básica",    1800.00, ProductCategory.CONSULTORIA),
        new Product("SKU-003", "Soporte Mensual",        950.00, ProductCategory.SOPORTE),
        new Product("SKU-004", "Laptop Dell",           18000.00, ProductCategory.HARDWARE),
        new Product("SKU-005", "Mouse Inalámbrico",       350.00, ProductCategory.HARDWARE),
        new Product("SKU-006", "Software Contable",     4200.00, ProductCategory.SOFTWARE),
        new Product("SKU-007", "Teclado Mecánico",        780.00, ProductCategory.HARDWARE),
        new Product("SKU-008", "Consultoría Avanzada",  3500.00, ProductCategory.CONSULTORIA)
    );

    // repositorio que guarda el historial de facturas generadas
    private final Repository<Invoice> repositorio = new Repository<>();

    // estrategia de impuesto: lambda con 16% de IVA
    // esto cumple el requisito de TaxStrategy como lambda
    private final TaxStrategy ivaStrategy = subtotal -> subtotal * 0.16;

    // ---------------------------------------------------------------
    // busquedas con Streams y Optional (sin null checks tradicionales)
    // ---------------------------------------------------------------

    public Optional<Customer> buscarClientePorId(String id) {
        return catalogoClientes.stream()
                .filter(c -> c.id().equals(id))
                .findFirst();
    }

    public Optional<Product> buscarProductoPorSku(String sku) {
        return catalogoProductos.stream()
                .filter(p -> p.sku().equals(sku))
                .findFirst();
    }

    // ---------------------------------------------------------------
    // calculos con Streams (sin bucles for/while)
    // ---------------------------------------------------------------

    public double calcularSubtotal(List<InvoiceItem> items) {
        // mapToDouble transforma cada item en su subtotal y los suma
        return items.stream()
                .mapToDouble(InvoiceItem::calcularSubtotal)
                .sum();
    }

    public double calcularTotal(List<InvoiceItem> items) {
        double subtotal = calcularSubtotal(items);
        // Total = Subtotal * (1 + Tax)  donde Tax = 0.16
        return subtotal * (1 + 0.16);
    }

    public double calcularIva(List<InvoiceItem> items) {
        // usamos la lambda de TaxStrategy
        return ivaStrategy.calcular(calcularSubtotal(items));
    }

    // ---------------------------------------------------------------
    // generar factura — tiene @Auditable como pide el documento
    // ---------------------------------------------------------------

    @Auditable(descripcion = "Generar nueva factura")
    public Invoice generarFactura(Customer cliente, List<InvoiceItem> items) {
        // simulamos el log que haria la anotacion @Auditable
        System.out.println("[AUDIT] Generando factura para: " + cliente.nombre()
                + " | Fecha: " + LocalDate.now());

        Invoice factura = new Invoice(cliente, items, InvoiceStatus.PENDING);
        repositorio.guardar(factura);
        return factura;
    }

    // ---------------------------------------------------------------
    // historial y filtrado por rango de fechas
    // ---------------------------------------------------------------

    public List<Invoice> obtenerHistorial() {
        return repositorio.obtenerTodos();
    }

    // filtra por rango de fechas con stream().filter()
    // devuelve lista inmutable como pide el documento
    public List<Invoice> filtrarPorFecha(LocalDate desde, LocalDate hasta) {
        return repositorio.obtenerTodos().stream()
                .filter(f -> !f.getFecha().isBefore(desde) && !f.getFecha().isAfter(hasta))
                .collect(java.util.stream.Collectors.toUnmodifiableList());
    }

    // getters de catalogos para usarlos en la vista
    public List<Customer> getCatalogoClientes()   { return catalogoClientes; }
    public List<Product>  getCatalogoProductos()  { return catalogoProductos; }
}
