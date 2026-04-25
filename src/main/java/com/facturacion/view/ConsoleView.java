package com.facturacion.view;

import com.facturacion.model.*;
import com.facturacion.service.InvoiceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// vista: solo maneja entrada/salida. no hace calculs.
public class ConsoleView {

    private final InvoiceService servicio;
    private final Scanner scanner;

    public ConsoleView(InvoiceService servicio) {
        this.servicio = servicio;
        this.scanner  = new Scanner(System.in);
    }

    // punto de entrada principal
    public void iniciar() {
        System.out.println("========================================");
        System.out.println("   SISTEMA DE FACTURACIÓN - CLI v1.0   ");
        System.out.println("========================================");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> nuevaFactura();
                case "2" -> verHistorial();
                case "3" -> {
                    System.out.println("\nCerrando el sistema. ¡Hasta luego!");
                    continuar = false;
                }
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    // menu numerado tal como pide el documento
    private void mostrarMenu() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Nueva Factura");
        System.out.println("2. Ver Historial");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ---------------------------------------------------------------
    // opcion 1: nueva factura
    // ---------------------------------------------------------------
    private void nuevaFactura() {
        System.out.println("\n=== NUEVA FACTURA ===");

        // mostrar catalogo de clientes
        System.out.println("\nClientes disponibles:");
        servicio.getCatalogoClientes().forEach(c ->
            System.out.printf("  %-6s | %-20s | %s%n", c.id(), c.nombre(), c.email())
        );

        // buscar cliente con Optional (sin null checks)
        System.out.print("\nIngrese ID del cliente: ");
        String idCliente = scanner.nextLine().trim();

        Optional<Customer> clienteOpt = servicio.buscarClientePorId(idCliente);

        // si no se encontro el cliente, avisamos y regresamos
        if (clienteOpt.isEmpty()) {
            System.out.println("Cliente no encontrado. Verifique el ID.");
            return;
        }

        Customer cliente = clienteOpt.get();
        System.out.println("Cliente seleccionado: " + cliente.nombre());

        // mostrar catalogo de productos
        System.out.println("\nProductos disponibles:");
        System.out.printf("%-10s | %-25s | %-10s | %s%n",
                "SKU", "Nombre", "Precio", "Categoría");
        System.out.println("-".repeat(65));
        servicio.getCatalogoProductos().forEach(p ->
            System.out.printf("%-10s | %-25s | $%-9.2f | %s%n",
                    p.sku(), p.nombre(), p.precioUnitario(), p.categoria())
        );

        // agregar items a la factura
        List<InvoiceItem> items = new ArrayList<>();
        boolean agregarMas = true;

        while (agregarMas) {
            System.out.print("\nIngrese SKU del producto (o 'fin' para terminar): ");
            String sku = scanner.nextLine().trim();

            if (sku.equalsIgnoreCase("fin")) {
                agregarMas = false;
                continue;
            }

            Optional<Product> productoOpt = servicio.buscarProductoPorSku(sku);

            if (productoOpt.isEmpty()) {
                System.out.println("Producto no encontrado. Intente con otro SKU.");
                continue;
            }

            Product producto = productoOpt.get();
            System.out.print("Cantidad para '" + producto.nombre() + "': ");

            try {
                int cantidad = Integer.parseInt(scanner.nextLine().trim());
                
                Optional<InvoiceItem> itemExistente = items.stream()
                    .filter(i -> i.producto().sku().equals(producto.sku()))
                    .findFirst();

                if (itemExistente.isPresent()) {
                    // Si ya existe, actualizamos la cantidad
                    int nuevaCantidad = itemExistente.get().cantidad() + cantidad;
                    items.remove(itemExistente.get());
                    items.add(new InvoiceItem(producto, nuevaCantidad));
                    System.out.println("Cantidad actualizada correctamente en la factura.");
                } else {
                    // Si no existe, lo agregamos como nuevo
                    items.add(new InvoiceItem(producto, cantidad));
                    System.out.println("Producto agregado correctamente.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida. Ingrese un número entero.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (items.isEmpty()) {
            System.out.println("No se agregaron productos. Factura cancelada.");
            return;
        }

        // Generar la factura usando el servicio
        Invoice factura = servicio.generarFactura(cliente, items);

        // Mostrar resumen de la factura
        mostrarResumenFactura(factura);
    }

    // ---------------------------------------------------------------
    // muestra el resumen de una factura con printf formateado
    // ---------------------------------------------------------------
    private void mostrarResumenFactura(Invoice factura) {
        System.out.println("\n========================================");
        System.out.println("         RESUMEN DE FACTURA");
        System.out.println("========================================");
        System.out.printf("ID Factura : %s%n", factura.getId());
        System.out.printf("Cliente    : %s%n", factura.getCliente().nombre());
        System.out.printf("Fecha      : %s%n", factura.getFecha());
        System.out.printf("Status     : %s%n", factura.getStatus());

        System.out.println("\n--- Detalle de productos ---");
        System.out.printf("%-25s | %-6s | %-10s | %s%n",
                "Producto", "Cant.", "P.Unit.", "Subtotal");
        System.out.println("-".repeat(60));

        factura.getItems().forEach(item ->
            System.out.printf("%-25s | %-6d | $%-9.2f | $%.2f%n",
                    item.producto().nombre(),
                    item.cantidad(),
                    item.producto().precioUnitario(),
                    item.calcularSubtotal())
        );

        double subtotal = servicio.calcularSubtotal(factura.getItems());
        double iva      = servicio.calcularIva(factura.getItems());
        double total    = servicio.calcularTotal(factura.getItems());

        System.out.println("-".repeat(60));
        System.out.printf("%-38s $%.2f%n", "Subtotal:", subtotal);
        System.out.printf("%-38s $%.2f%n", "IVA (16%):", iva);
        System.out.printf("%-38s $%.2f%n", "TOTAL:", total);
        System.out.println("========================================");
    }

    // ---------------------------------------------------------------
    // opcion 2: ver historial
    // ---------------------------------------------------------------
    private void verHistorial() {
        System.out.println("\n=== HISTORIAL DE FACTURAS ===");

        List<Invoice> historial = servicio.obtenerHistorial();

        if (historial.isEmpty()) {
            System.out.println("No hay facturas registradas todavia.");
            return;
        }

        System.out.println("\n¿Desea filtrar por rango de fechas? (s/n): ");
        String respuesta = scanner.nextLine().trim();

        if (respuesta.equalsIgnoreCase("s")) {
            historial = pedirFiltroFechas();
            if (historial.isEmpty()) {
                System.out.println("No se encontraron facturas en ese rango.");
                return;
            }
        }

        // Mostrar cada factura del historial
        System.out.printf("%n%-10s | %-20s | %-12s | %-10s | %s%n",
                "ID", "Cliente", "Fecha", "Status", "Total");
        System.out.println("-".repeat(70));

        // Usamos stream + forEach para no usar bucles for/while
        historial.stream().forEach(f -> {
            double total = servicio.calcularTotal(f.getItems());
            System.out.printf("%-10s | %-20s | %-12s | %-10s | $%.2f%n",
                    f.getId(),
                    f.getCliente().nombre(),
                    f.getFecha(),
                    f.getStatus(),
                    total);
        });
    }

    // pide fechas al usuario para filtrar el historial
    private List<Invoice> pedirFiltroFechas() {
        try {
            System.out.print("Fecha inicio (YYYY-MM-DD): ");
            LocalDate desde = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Fecha fin    (YYYY-MM-DD): ");
            LocalDate hasta = LocalDate.parse(scanner.nextLine().trim());

            return servicio.filtrarPorFecha(desde, hasta);
        } catch (Exception e) {
            System.out.println("Formato de fecha inválido. Mostrando todo el historial.");
            return servicio.obtenerHistorial();
        }
    }
}
