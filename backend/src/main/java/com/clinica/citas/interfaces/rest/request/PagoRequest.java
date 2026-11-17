package com.clinica.citas.interfaces.rest.request;

/** El metodo de pago es opcional: si no llega, se usa la tarjeta simulada. */
public record PagoRequest(String metodoPago) {
}
