package com.facturacion.model;

// Un renglón de la factura: producto + cantidad
// También es un Record, así que es inmutable
public record InvoiceItem(
    Product producto,
    int cantidad
) {
    public InvoiceItem {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
    }

    // Método de conveniencia para calcular el subtotal de este renglón
    public double calcularSubtotal() {
        return cantidad * producto.precioUnitario();
    }
}
