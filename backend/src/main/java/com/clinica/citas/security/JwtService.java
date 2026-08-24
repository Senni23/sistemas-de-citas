package com.clinica.citas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

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

    public String generarToken(UsuarioPrincipal principal) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("rol", principal.getUsuario().getRol().name())
                .claim("usuarioId", principal.getUsuarioId())
                .claim("medicoId", principal.getMedicoId())
                .claim("pacienteId", principal.getPacienteId())
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
