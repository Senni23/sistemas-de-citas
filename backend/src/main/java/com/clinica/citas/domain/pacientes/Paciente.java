package com.clinica.citas.domain.pacientes;

import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.NombrePersona;

import java.time.LocalDate;

/** Raiz de agregado: un paciente de la clinica. */
public final class Paciente {

    private final PacienteId id;
    private NombrePersona nombre;
    private Dni dni;
    private LocalDate fechaNacimiento;
    private String telefono;
    private CorreoElectronico correo;
    private String direccion;

    private Paciente(PacienteId id, NombrePersona nombre, Dni dni, LocalDate fechaNacimiento, String telefono,
                     CorreoElectronico correo, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public static Paciente registrar(NombrePersona nombre, Dni dni, LocalDate fechaNacimiento, String telefono,
                                     CorreoElectronico correo, String direccion) {
        exigirDatosBasicos(nombre, dni);
        return new Paciente(null, nombre, dni, fechaNacimiento, validarTelefono(telefono), correo, validarDireccion(direccion));
    }

    public static Paciente reconstruir(PacienteId id, NombrePersona nombre, Dni dni, LocalDate fechaNacimiento,
                                       String telefono, CorreoElectronico correo, String direccion) {
        return new Paciente(id, nombre, dni, fechaNacimiento, telefono, correo, direccion);
    }

    public void actualizarDatos(NombrePersona nombre, Dni dni, LocalDate fechaNacimiento, String telefono,
                                CorreoElectronico correo, String direccion) {
        exigirDatosBasicos(nombre, dni);
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = validarTelefono(telefono);
        this.correo = correo;
        this.direccion = validarDireccion(direccion);
    }

    private static void exigirDatosBasicos(NombrePersona nombre, Dni dni) {
        if (nombre == null || dni == null) {
            throw new DomainException("El paciente necesita nombre y DNI");
        }
    }

    private static String validarTelefono(String telefono) {
        if (telefono != null && telefono.length() > 20) {
            throw new DomainException("El telefono no puede pasar de 20 caracteres");
        }
        return telefono;
    }

    private static String validarDireccion(String direccion) {
        if (direccion != null && direccion.length() > 255) {
            throw new DomainException("La direccion no puede pasar de 255 caracteres");
        }
        return direccion;
    }

    public PacienteId id() {
        return id;
    }

    public NombrePersona nombre() {
        return nombre;
    }

    public Dni dni() {
        return dni;
    }

    public LocalDate fechaNacimiento() {
        return fechaNacimiento;
    }

    public String telefono() {
        return telefono;
    }

    public CorreoElectronico correo() {
        return correo;
    }

    public String direccion() {
        return direccion;
    }
}
