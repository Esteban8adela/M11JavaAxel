package com.facturacion.model;

// Record de Java: es inmutable por defecto y genera automáticamente
// equals(), hashCode() y toString()
public record Customer(
    String id,
    String nombre,
    String email
) {
    // El record ya genera equals, hashCode y toString automáticamente
    // Solo agregamos una validación básica en el constructor
    public Customer {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del cliente no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }
}
