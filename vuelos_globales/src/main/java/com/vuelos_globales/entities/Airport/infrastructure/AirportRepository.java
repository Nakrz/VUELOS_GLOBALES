package com.vuelos_globales.entities.Airport.infrastructure;

import java.util.List;
import java.util.Optional;

import com.vuelos_globales.entities.Airport.domain.Airport;

public interface AirportRepository {
    void save(Airport airport);
    void update(Airport airport);
    Optional<Airport> findById(String id);
    void delete(String id);
    List<Airport> findAll();
}
