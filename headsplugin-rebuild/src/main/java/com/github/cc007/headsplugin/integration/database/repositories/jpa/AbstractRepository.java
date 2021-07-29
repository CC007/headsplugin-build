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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

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
        TypedQuery<E> query = find((cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
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
        TypedQuery<E> query = find((cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
        return query.getResultList();
    }

    /**
     * Get an list of managed entity from the table for that entity type,
     * based on the given property name and the value it needs to contain.
     *
     * @param propertyName the property name to filter on
     * @param value the value that the property of the given name should contain
     * @return an optional managed entity
     */
    protected List<E> findAllByContaining(String propertyName, String value) {
        TypedQuery<E> query = find((criteriaBuilder, root) -> criteriaBuilder.like(
                root.get(propertyName),
                "%" + value + "%"
        ));
        return query.getResultList();
    }

    /**
     * Get an list of managed entity from the table for that entity type,
     * based on the given property name and the case-insenitive value it needs to contain.
     *
     * @param propertyName the property name to filter on
     * @param value the case-insensitive value that the property of the given name should contain
     * @return an optional managed entity
     */
    protected List<E> findAllByIgnoreCaseContaining(String propertyName, String value) {
        TypedQuery<E> query = find((criteriaBuilder, root) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get(propertyName)),
                "%" + value.toLowerCase() + "%"
        ));
        return query.getResultList();
    }

    /**
     * Get an list of managed entity from the table for that entity type,
     * based on the given property name and the given collection of values.
     *
     * @param propertyName the property name to filter on
     * @param values the values, one of which the property of the given name should have
     * @return an optional managed entity
     */
    protected List<E> findAllByIn(String propertyName, Collection<String> values) {
        TypedQuery<E> query = find((criteriaBuilder, root) ->
                root.get(propertyName)
                    .in(values));
        return query.getResultList();
    }

    /**
     * Create a typed query based on the given where condition for a given entity type
     *
     * @param whereCondition a bifunction that generates the predicate to be used in the where condition
     *                       of the criteria query
     * @return the typed query
     */
    protected TypedQuery<E> find(BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<E> criteriaQuery = criteriaBuilder
                .createQuery(entityType);

        Root<E> root = criteriaQuery.from(entityType);

        criteriaQuery.select(root).where(whereCondition.apply(criteriaBuilder, root));

        return entityManager.createQuery(criteriaQuery);
    }

    /**
     * Create a typed query based on the given where condition for a given entity type
     *
     * @param whereCondition a bifunction that generates the predicate to be used in the where condition
     *                       of the criteria query
     * @return the typed query
     */
    protected <T> TypedQuery<T> findProperty(String selectPropertyName, Class<T> selectPropertyType, BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> criteriaQuery = criteriaBuilder
                .createQuery(selectPropertyType);

        Root<E> root = criteriaQuery.from(entityType);

        criteriaQuery.select(root.get(selectPropertyName)).where(whereCondition.apply(criteriaBuilder, root));

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
