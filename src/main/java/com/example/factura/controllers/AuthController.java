package com.example.factura.controllers;

import com.example.factura.dto.LoginRequest;
import com.example.factura.dto.LoginResponse;
import com.example.factura.services.TokenService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager manager;
    private final TokenService tokens;

    public AuthController(AuthenticationManager manager, TokenService tokens) {
        this.manager = manager;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest datos) {
        Authentication auth = manager.authenticate(
            new UsernamePasswordAuthenticationToken(datos.username(), datos.password()));
        return ResponseEntity.ok(new LoginResponse(tokens.crearToken(auth), "Bearer"));
    }

    /** Reto adicional: devuelve el usuario y los roles del JWT recibido. */
    @GetMapping("/perfil")
    public Map<String, Object> perfil(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        Map<String, Object> perfil = new LinkedHashMap<>();
        perfil.put("username", jwt.getSubject());
        perfil.put("roles", roles == null ? List.of() : roles);
        perfil.put("emitido", jwt.getIssuedAt());
        perfil.put("expira", jwt.getExpiresAt());
        return perfil;
    }
}
