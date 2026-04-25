package com.facturacion;

import com.facturacion.service.InvoiceService;
import com.facturacion.view.ConsoleView;

// Punto de entrada de la aplicacio
public class Main {

    public static void main(String[] args) {
        InvoiceService servicio = new InvoiceService();
        ConsoleView vista       = new ConsoleView(servicio);
        vista.iniciar();
    }
}
