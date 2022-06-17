package com.github.cc007.headsplugin.integration.database.services;

public interface ManagedEntityService {

    /**
     * This method creates a new entity and makes it managed by the entity manager.
     *
     * @return a new managed entity
     */
    <E> E manageNew(Class<E> entityType);
}
