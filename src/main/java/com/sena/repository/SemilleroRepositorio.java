package com.sena.repository;

import com.sena.model.Semillero;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SemilleroRepositorio extends MongoRepository<Semillero, String> {
}