package cl.duoc.rednorte.listaespera.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // Siempre encriptado con BCrypt

    @Column(nullable = false, length = 50)
    private String rol; // ROLE_ADMIN, ROLE_MEDICO, ROLE_PACIENTE

    private Boolean activo = true;
}