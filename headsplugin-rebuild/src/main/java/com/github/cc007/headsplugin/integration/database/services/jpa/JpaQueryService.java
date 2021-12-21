package com.github.cc007.headsplugin.integration.database.services.jpa;

import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public class JpaQueryService implements QueryService {
    protected final EntityManager entityManager;

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(entityType);
        Root<E> root = criteriaQuery.from(entityType);

        final TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    @Override
    public <E> Optional<E> findByProperty(Class<E> entityType, String propertyName, String value) {
        TypedQuery<E> query = queryByCondition(entityType, (cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
        return getSingleResult(query);
    }

    @Override
    public <E> List<E> findAllByProperty(Class<E> entityType, String propertyName, String value) {
        TypedQuery<E> query = queryByCondition(entityType, (cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
        return query.getResultList();
    }

    @Override
    public <E> List<E> findAllByPropertyContaining(Class<E> entityType, String propertyName, String value) {
        TypedQuery<E> query = queryByCondition(entityType, (criteriaBuilder, root) -> criteriaBuilder.like(
                root.get(propertyName),
                "%" + value + "%"
        ));
        return query.getResultList();
    }

    @Override
    public <E> List<E> findAllByPropertyIn(Class<E> entityType, String propertyName, Collection<String> values) {
        TypedQuery<E> query = queryByCondition(entityType, (criteriaBuilder, root) ->
                root.get(propertyName).in(values)
        );
        return query.getResultList();
    }

    @Override
    public <E> TypedQuery<E> queryByCondition(Class<E> entityType,
                                              BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition) {
        return querySelectionByCondition(entityType, root -> root, entityType, whereCondition);
    }

    @Override
    public <E, T> TypedQuery<T> querySelectionByCondition(
            Class<E> entityType,
            Function<Root<E>, Path<T>> selection,
            Class<T> selectPropertyType,
            BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> criteriaQuery = criteriaBuilder
                .createQuery(selectPropertyType);

        Root<E> root = criteriaQuery.from(entityType);

        criteriaQuery.select(selection.apply(root)).where(whereCondition.apply(criteriaBuilder, root));

        return entityManager.createQuery(criteriaQuery);
    }

    @Override
    public <E> Optional<E> getSingleResult(TypedQuery<E> query) {
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}