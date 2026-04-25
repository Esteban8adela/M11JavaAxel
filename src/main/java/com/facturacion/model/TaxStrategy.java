package com.facturacion.model;

// Interfaz funcional propia para calcular impuestos
// @FunctionalInterface indica que solo tiene un método abstracto
// Esto nos permite usarla con lambdas
@FunctionalInterface
public interface TaxStrategy {
    double calcular(double subtotal);
}
