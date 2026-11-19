package com.clinica.citas.domain.medicos;

import com.clinica.citas.domain.catalogo.EspecialidadId;
import com.clinica.citas.domain.shared.CorreoElectronico;
import com.clinica.citas.domain.shared.DomainException;
import com.clinica.citas.domain.shared.NombrePersona;

/** Raiz de agregado: un medico de la clinica, con su especialidad (referenciada por id). */
public final class Medico {

    private final MedicoId id;
    private NombrePersona nombre;
    private Colegiatura colegiatura;
    private String telefono;
    private CorreoElectronico correo;
    private EspecialidadId especialidadId;

    private Medico(MedicoId id, NombrePersona nombre, Colegiatura colegiatura, String telefono,
                   CorreoElectronico correo, EspecialidadId especialidadId) {
        this.id = id;
        this.nombre = nombre;
        this.colegiatura = colegiatura;
        this.telefono = telefono;
        this.correo = correo;
        this.especialidadId = especialidadId;
    }

    public static Medico registrar(NombrePersona nombre, Colegiatura colegiatura, String telefono,
                                   CorreoElectronico correo, EspecialidadId especialidadId) {
        exigirDatosBasicos(nombre, colegiatura, especialidadId);
        return new Medico(null, nombre, colegiatura, validarTelefono(telefono), correo, especialidadId);
    }

    public static Medico reconstruir(MedicoId id, NombrePersona nombre, Colegiatura colegiatura, String telefono,
                                     CorreoElectronico correo, EspecialidadId especialidadId) {
        return new Medico(id, nombre, colegiatura, telefono, correo, especialidadId);
    }

    /** Actualiza los datos; si no se indica una especialidad nueva (null), conserva la actual. */
    public void actualizarDatos(NombrePersona nombre, Colegiatura colegiatura, String telefono,
                                CorreoElectronico correo, EspecialidadId nuevaEspecialidadId) {
        exigirDatosBasicos(nombre, colegiatura, nuevaEspecialidadId != null ? nuevaEspecialidadId : this.especialidadId);
        this.nombre = nombre;
        this.colegiatura = colegiatura;
        this.telefono = validarTelefono(telefono);
        this.correo = correo;
        if (nuevaEspecialidadId != null) {
            this.especialidadId = nuevaEspecialidadId;
        }
    }

    private static void exigirDatosBasicos(NombrePersona nombre, Colegiatura colegiatura, EspecialidadId especialidadId) {
        if (nombre == null || colegiatura == null || especialidadId == null) {
            throw new DomainException("El medico necesita nombre, colegiatura y especialidad");
        }
    }

    private static String validarTelefono(String telefono) {
        if (telefono != null && telefono.length() > 20) {
            throw new DomainException("El telefono no puede pasar de 20 caracteres");
        }
        return telefono;
    }

    public MedicoId id() {
        return id;
    }

    public NombrePersona nombre() {
        return nombre;
    }

    public Colegiatura colegiatura() {
        return colegiatura;
    }

    public String telefono() {
        return telefono;
    }

    public CorreoElectronico correo() {
        return correo;
    }

    public EspecialidadId especialidadId() {
        return especialidadId;
    }
}
