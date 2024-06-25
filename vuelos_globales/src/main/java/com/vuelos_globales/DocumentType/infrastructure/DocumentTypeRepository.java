package com.vuelos_globales.DocumentType.infrastructure;

import java.util.List;
import java.util.Optional;

import com.vuelos_globales.DocumentType.domain.DocumentType;

public interface DocumentTypeRepository {
    void save(DocumentType documentType);
    void update(DocumentType documentType);
    Optional<DocumentType> findById(int id);
    void delete(int id);
    List<DocumentType> findAll();
}
