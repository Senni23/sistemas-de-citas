package com.clinica.citas.domain.citas;

/** Criterios permitidos para ordenar un listado de citas. La lista cerrada evita pedir un campo que no existe. */
public enum OrdenDeCitas {
    /** Por fecha y, dentro de cada dia, por hora. */
    FECHA,
    ESTADO,
    /** Por orden de creacion. */
    ID
}
