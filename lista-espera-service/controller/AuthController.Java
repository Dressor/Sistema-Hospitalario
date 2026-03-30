package cl.duoc.rednorte.listaespera.controller;

import cl.duoc.rednorte.listaespera.config.JwtService;
import cl.duoc.rednorte.listaespera.model.Usuario;
import cl.duoc.rednorte.listaespera.repository.UsuarioRepository;
import lombok.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(req.getEmail());
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user)));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail()))
            return ResponseEntity.badRequest().body("Email ya registrado");

        usuarioRepository.save(Usuario.builder()
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .rol("ROLE_MEDICO")
            .activo(true)
            .build());
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @Data public static class LoginRequest    { private String email, password; }
    @Data public static class RegisterRequest { private String email, password; }
    @Data @AllArgsConstructor public static class AuthResponse { private String token; }
}