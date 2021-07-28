package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.repositories.Repository;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

@SuperBuilder
public abstract class AbstractRepository<E, ID> implements Repository<E, ID> {
    private final Class<E> entityType = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @NonNull
    protected final EntityManager entityManager;

    /**
     * This method creates a new entity and makes it managed by the entity manager.
     *
     * @return a new managed entity
     */
    @Override
    @SneakyThrows({IllegalAccessException.class, InstantiationException.class, InvocationTargetException.class, NoSuchMethodException.class, })
    public E manageNew() {
        E entity = entityType.getConstructor().newInstance();
        entityManager.persist(entity);
        return entity;
    }

    /**
     * Get a managed entity based on the provided unmanaged (new or detached) entity.
     *
     * This method doesn't have any side effects, meaning that the entity given as a parameter
     * doesn't become managed itself. Only the returned entity will be managed.
     *
     * @param entity the managed entity
     * @return a managed entity of type &lt;E&gt;
     */
    @Override
    public E manage(E entity) {
        return entityManager.merge(entity);
    }

    /**
     * Get a list of managed entities for all stored rows in the table for that given entity type
     *
     * @return the list of managed entities
     */
    @Override
    public List<E> findAll() {
        CriteriaQuery<E> criteriaQuery =  entityManager.getCriteriaBuilder().createQuery(entityType);

        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * Get an optional managed entity from the table for that entity type,
     * based on the given property name and its value.
     *
     * @param propertyName the property name to filter on
     * @param value the value that the property of the given name should have
     * @return an optional managed entity
     */
    protected Optional<E> findBy(String propertyName, String value) {
        TypedQuery<E> query = findBy0(propertyName, value);
        return getSingleResult(query);
    }

    /**
     * Get an list of managed entity from the table for that entity type,
     * based on the given property name and its value.
     *
     * @param propertyName the property name to filter on
     * @param value the value that the property of the given name should have
     * @return an optional managed entity
     */
    protected List<E> findAllBy(String propertyName, String value) {
        TypedQuery<E> query = findBy0(propertyName, value);
        return query.getResultList();
    }

    private TypedQuery<E> findBy0(String propertyName, String value) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<E> criteriaQuery = criteriaBuilder
                .createQuery(entityType);

        Root<E> root = criteriaQuery.from(entityType);

        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(propertyName), value));

        return entityManager.createQuery(criteriaQuery);
    }

    /**
     * Get an optional managed entity for a given query
     *
     * @param query the query to be executed to retrieve the entity
     * @return an optional managed entity for the given query
     */
    protected Optional<E> getSingleResult(TypedQuery<E> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
