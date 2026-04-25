package com.facturacion.repository;

import java.util.ArrayList;
import java.util.List;

// Repositorio genérico con <T> para manejar el historial en memoria
// PECS: Producer Extends, Consumer Super
public class Repository<T> {

    // Lista interna donde guardamos los elementos
    private final List<T> almacenamiento = new ArrayList<>();

    // Guarda un elemento en el repositorio
    public void guardar(T elemento) {
        almacenamiento.add(elemento);
    }

    // Devuelve todos los elementos como lista inmutable
    public List<T> obtenerTodos() {
        return List.copyOf(almacenamiento);
    }

    // PECS - Producer (extends): lee facturas de una fuente externa
    // La fuente "produce" elementos de tipo T o subtipos de T
    public void cargarDesde(List<? extends T> fuente) {
        almacenamiento.addAll(fuente);
    }

    // PECS - Consumer (super): exporta facturas a una lista de destino
    // El destino "consume" elementos de tipo T o supertipos de T
    public void exportarHacia(List<? super T> destino) {
        destino.addAll(almacenamiento);
    }

    // Cuántos elementos hay guardados
    public int total() {
        return almacenamiento.size();
    }
}
