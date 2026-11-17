package com.clinica.citas.infrastructure.security;

import com.clinica.citas.application.identidad.EmisorDeTokens;
import com.clinica.citas.domain.identidad.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Implementacion del puerto EmisorDeTokens con JWT firmado. Tambien valida los tokens que llegan. */
@Component
public class JwtService implements EmisorDeTokens {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiration-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(claveDeAlMenos32Bytes(secreto));
        this.expiracionMs = expiracionMs;
    }

    private byte[] claveDeAlMenos32Bytes(String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return bytes;
        }
        // Relleno defensivo por si alguien configura un secreto corto; evita que la app truene al arrancar.
        byte[] relleno = new byte[32];
        System.arraycopy(bytes, 0, relleno, 0, bytes.length);
        return relleno;
    }

    @Override
    public String emitir(Usuario usuario) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(usuario.username())
                .claim("rol", usuario.rol().name())
                .claim("usuarioId", usuario.id() != null ? usuario.id().valor() : null)
                .claim("medicoId", usuario.medicoId() != null ? usuario.medicoId().valor() : null)
                .claim("pacienteId", usuario.pacienteId() != null ? usuario.pacienteId().valor() : null)
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(clave)
                .compact();
    }

    public String extraerUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, UsuarioPrincipal principal) {
        String username = extraerUsername(token);
        return username.equals(principal.getUsername()) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
