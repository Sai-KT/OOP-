package com.college.sms.repository;

import java.util.List;
import java.util.Optional;

/**
 * ==============================================================================
 * Repository<T, ID> (OOP Concept: Generic Interface & Abstract Data Type)
 * ==============================================================================
 * Generic persistence contract defining standard CRUD operations.
 * Demonstrates:
 * 1. Generics: Type safety for entities (T) and primary identifier types (ID).
 * 2. Interface Abstraction: Decouples business logic from persistence implementation.
 * 3. Collection Contracts: Defines operations returning generic List<T> collections.
 * ==============================================================================
 *
 * @param <T>  The domain entity type
 * @param <ID> The primary key / identifier type
 */
public interface Repository<T, ID> {

    /**
     * Retrieves all managed entities.
     *
     * @return List of all domain entities
     */
    List<T> findAll();

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id Identifier of entity
     * @return Optional containing entity if found, empty otherwise
     */
    Optional<T> findById(ID id);

    /**
     * Saves a new entity or updates an existing entity.
     *
     * @param entity Entity to persist
     * @return Saved entity instance
     */
    T save(T entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id Identifier of entity to remove
     */
    void deleteById(ID id);

    /**
     * Checks if an entity exists for given identifier.
     *
     * @param id Identifier to check
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Returns total count of entities.
     *
     * @return Total record count
     */
    long count();
}
