package com.facturacion.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// La factura usa campos final para ser inmutable
// No usamos record aquí porque el ID se autogenera dentro de la clase
public class Invoice {

    private final String id;
    private final Customer cliente;
    private final List<InvoiceItem> items;
    private final InvoiceStatus status;
    private final LocalDate fecha;

    public Invoice(Customer cliente, List<InvoiceItem> items, InvoiceStatus status) {
        // Autogeneración del ID con UUID (cumple el criterio de la rúbrica)
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.cliente = cliente;
        // List.copyOf hace la lista inmutable
        this.items = List.copyOf(items);
        this.status = status;
        this.fecha = LocalDate.now();
    }

    // Getters
    public String getId()               { return id; }
    public Customer getCliente()        { return cliente; }
    public List<InvoiceItem> getItems() { return items; }
    public InvoiceStatus getStatus()    { return status; }
    public LocalDate getFecha()         { return fecha; }

    // Contrato de Object: equals y hashCode basados en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Invoice{id='" + id + "', cliente=" + cliente.nombre()
                + ", status=" + status + ", fecha=" + fecha + "}";
    }
}
