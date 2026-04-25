package com.facturacion.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Anotación personalizada para simular un log de auditoría
// Se aplica sobre métodos (ElementType.METHOD)
// RUNTIME permite leerla con reflexión en tiempo de ejecución
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    String descripcion() default "Operación auditada";
}
