package com.github.cc007.headsplugin.integration.database.services.jpa;

import com.github.cc007.headsplugin.business.utils.CollectionUtils;
import com.github.cc007.headsplugin.config.properties.ConfigProperties;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;
import lombok.val;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public class JpaQueryService implements QueryService {
    private static final int CHUNK_SIZE = 500;

    private final EntityManager entityManager;
    private final ConfigProperties configProperties;

    @Override
    public <E> List<E> findAll(Class<E> entityType) {
        final var criteriaBuilder = entityManager.getCriteriaBuilder();

        final var criteriaQuery = criteriaBuilder.createQuery(entityType);
        final var root = criteriaQuery.from(entityType);

        final var query = entityManager.createQuery(criteriaQuery);

        return getMutableResultList(query);
    }

    @Override
    public <E> Optional<E> findByProperty(Class<E> entityType, String propertyName, String value) {
        val query = queryByCondition(entityType, (cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
        return getSingleResult(query);
    }

    @Override
    public <E> List<E> findAllByProperty(Class<E> entityType, String propertyName, String value) {
        val query = queryByCondition(entityType, (cb, r) -> cb.equal(
                r.get(propertyName),
                value
        ));
        return  getMutableResultList(query);
    }

    @Override
    public <E> List<E> findAllByPropertyContaining(Class<E> entityType, String propertyName, String value) {
        val query = queryByCondition(entityType, (criteriaBuilder, root) -> criteriaBuilder.like(
                root.get(propertyName),
                "%" + value + "%"
        ));
        return getMutableResultList(query);
    }

    @Override
    public <E> List<E> findAllByPropertyIn(Class<E> entityType, String propertyName, Collection<String> values) {
        val resultList = new ArrayList<E>();
        val valueGroups = CollectionUtils.partitionCollection(values, configProperties.getDatabase().getChunkSize());
        for (val valueGroup : valueGroups) {
            val query = queryByCondition(entityType, (criteriaBuilder, root) ->
                    root.get(propertyName).in(valueGroup)
            );
            resultList.addAll(getMutableResultList(query));
        }
        return resultList;
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
        val criteriaBuilder = entityManager.getCriteriaBuilder();

        val criteriaQuery = criteriaBuilder
                .createQuery(selectPropertyType);

        val root = criteriaQuery.from(entityType);

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

    @Override
    public <T> List<T> getMutableResultList(TypedQuery<T> query) {
        return new ArrayList<>(query.getResultList());
    }
}