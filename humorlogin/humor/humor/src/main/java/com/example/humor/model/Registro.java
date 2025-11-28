package com.example.humor.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento: cada registro pertence a 1 usuário
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    // Status do humor
    @Enumerated(EnumType.STRING)
    private StatusHumor status;

    // Texto explicativo
    @Column(columnDefinition = "TEXT")
    private String texto;

    // Lista de tags (pode simplificar como string separada por vírgula, se quiser)
    @ElementCollection
    private List<String> tags;

    private LocalDateTime dataHora;

    // construtores
    public Registro() {
        this.dataHora = LocalDateTime.now();
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public StatusHumor getStatus() { return status; }
    public void setStatus(StatusHumor status) { this.status = status; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}