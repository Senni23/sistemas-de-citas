package com.clinica.citas.service;

import com.clinica.citas.dto.MedicoRegistroRequest;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.exception.RegistroDuplicadoException;
import com.clinica.citas.model.Especialidad;
import com.clinica.citas.model.Medico;
import com.clinica.citas.model.Rol;
import com.clinica.citas.model.Usuario;
import com.clinica.citas.repository.MedicoRepository;
import com.clinica.citas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadService especialidadService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadService especialidadService,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.especialidadService = especialidadService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Medico> listar() {
        return medicoRepository.findAll();
    }

    public Medico obtener(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado: " + id));
    }

    @Transactional
    public Medico crear(MedicoRegistroRequest request) {
        if (usuarioRepository.findByUsername(request.email()).isPresent()) {
            throw new RegistroDuplicadoException("Ya existe una cuenta con ese correo");
        }

        Especialidad especialidad = especialidadService.obtener(request.especialidadId());

        Medico medico = new Medico();
        medico.setNombres(request.nombres());
        medico.setApellidos(request.apellidos());
        medico.setColegiatura(request.colegiatura());
        medico.setTelefono(request.telefono());
        medico.setEmail(request.email());
        medico.setEspecialidad(especialidad);
        medico = medicoRepository.save(medico);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.MEDICO);
        usuario.setMedico(medico);
        usuarioRepository.save(usuario);

        return medico;
    }

    public Medico actualizar(Long id, Medico datos) {
        Medico medico = obtener(id);
        medico.setNombres(datos.getNombres());
        medico.setApellidos(datos.getApellidos());
        medico.setColegiatura(datos.getColegiatura());
        medico.setTelefono(datos.getTelefono());
        medico.setEmail(datos.getEmail());
        if (datos.getEspecialidad() != null && datos.getEspecialidad().getId() != null) {
            medico.setEspecialidad(especialidadService.obtener(datos.getEspecialidad().getId()));
        }
        return medicoRepository.save(medico);
    }

    public void eliminar(Long id) {
        Medico medico = obtener(id);
        medicoRepository.delete(medico);
    }
}
