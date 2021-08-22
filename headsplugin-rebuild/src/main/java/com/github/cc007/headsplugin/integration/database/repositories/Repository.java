package com.github.cc007.headsplugin.integration.database.repositories;

public interface Repository<T, ID>  {

    /**
     * This method creates a new entity and makes it managed by the entity manager.
     *
     * @return a new managed entity
     */
    T manageNew();

    /**
     * Get a managed entity based on the provided unmanaged (new or detached) entity.
     *
     * This method doesn't have any side effects, meaning that the entity given as a parameter
     * doesn't become managed itself. Only the returned entity will be managed.
     *
     * @param entity the managed entity
     * @return a managed entity of type &lt;E&gt;
     */
    T manage(T entity);
}
