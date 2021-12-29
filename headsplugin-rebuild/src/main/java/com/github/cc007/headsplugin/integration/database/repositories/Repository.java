package com.github.cc007.headsplugin.integration.database.repositories;

public interface Repository<T, ID>  {

    /**
     * This method creates a new entity and makes it managed by the entity manager.
     *
     * @return a new managed entity
     */
    T manageNew();
}
