package com.example.humor.repository;

import com.example.humor.model.Registro;
import com.example.humor.model.StatusHumor;
import com.example.humor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    // Buscar registros de um usuário específico
    List<Registro> findByUsuario(User usuario);

    // Buscar registros de um usuário filtrados por status
    List<Registro> findByUsuarioAndStatus(User usuario, StatusHumor status);

    // Buscar registros por intervalo de tempo
    List<Registro> findByUsuarioAndDataHoraBetween(User usuario, LocalDateTime inicio, LocalDateTime fim);

    List<Registro> findByUsuarioAndTextoContainingIgnoreCase(User usuario, String texto);

    // Buscar registros por tags (como você usou @ElementCollection para tags)
    List<Registro> findByUsuarioAndTagsContaining(User usuario, String tag);


}