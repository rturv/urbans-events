package com.urbanevents.metricas.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndicadorRepository extends JpaRepository<Indicador, Long> {
	Optional<Indicador> findByTipo(String tipo);
}
