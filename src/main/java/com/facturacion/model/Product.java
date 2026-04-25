package com.facturacion.model;

// Record inmutable para representar un producto del catálogo
public record Product(
    String sku,
    String nombre,
    double precioUnitario,
    ProductCategory categoria
) {
    public Product {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU no puede estar vacío");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }
}
