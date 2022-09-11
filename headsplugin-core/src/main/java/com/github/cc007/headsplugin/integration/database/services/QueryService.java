package com.github.cc007.headsplugin.integration.database.services;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface QueryService {

    /**
     * Get a list of managed entities for all stored rows in the table for that given entity type
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @return the list of managed entities
     */
    <E> List<E> findAll(Class<E> entityType);

    /**
     * Get an optional managed entity from the table for that entity type,
     * based on the given property name and its value.
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @param propertyName the property name to filter on
     * @param value        the value that the property of the given name should have
     * @return an optional managed entity
     */
    <E> Optional<E> findByProperty(Class<E> entityType, String propertyName, String value);

    /**
     * Get a list of managed entity from the table for that entity type,
     * based on the given property name and its value.
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @param propertyName the property name to filter on
     * @param value        the value that the property of the given name should have
     * @return an optional managed entity
     */
    <E> List<E> findAllByProperty(Class<E> entityType, String propertyName, String value);

    /**
     * Get a list of managed entity from the table for that entity type,
     * based on the given property name and the value it needs to contain.
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @param propertyName the property name to filter on
     * @param value        the value that the property of the given name should contain
     * @return an optional managed entity
     */
    <E> List<E> findAllByPropertyContaining(Class<E> entityType, String propertyName, String value);

    /**
     * Get a list of managed entity from the table for that entity type,
     * based on the given property name and the given collection of values.
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @param propertyName the property name to filter on
     * @param values       the values, one of which the property of the given name should have
     * @return an optional managed entity
     */
    <E> List<E> findAllByPropertyIn(Class<E> entityType, String propertyName, Collection<String> values);

    /**
     * Create a typed query based on the given where condition for a given entity type
     *
     * @param <E> type of the entity
     * @param entityType class object associated with the type of the entity
     * @param whereCondition a biFunction that generates the predicate to be used in the where condition
     *                       of the criteria query
     * @return the typed query
     */
    <E> TypedQuery<E> queryByCondition(Class<E> entityType, BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition);

    /**
     * Create a typed query based on the given selection and where condition for a given entity type
     *
     * @param <E> type of the entity
     * @param <T> type of the query result
     * @param entityType class object associated with the type of the entity
     * @param selection a function that returns the specified selection, based on a given root
     * @param selectPropertyType the type of the selection
     * @param whereCondition a biFunction that generates the predicate to be used in the where condition
     *                       of the criteria query
     * @return the typed query
     */
    <E, T> TypedQuery<T> querySelectionByCondition(Class<E> entityType, Function<Root<E>, Path<T>> selection, Class<T> selectPropertyType, BiFunction<CriteriaBuilder, Root<E>, Predicate> whereCondition);

    /**
     * Get an optional managed entity for a given query
     *
     * @param <T> type of the query result
     * @param query the query to be executed to retrieve the entity
     * @return an optional managed entity for the given query
     */
    <T> Optional<T> getSingleResult(TypedQuery<T> query);

    /**
     * Get a result list as a mutable list.
     * Normally the result list that you get from {@link TypedQuery#getResultList()} is immutable.
     * This method creates a new {@link java.util.ArrayList} with all elements from that result list.
     * Doing this will probably impact performance, so only do this when needed.
     *
     * @param query the query to get the results from.
     * @param <T> the type of the query result
     * @return a mutable list of {@link T} typed elements
     */
    <T> List<T> getMutableResultList(TypedQuery<T> query);
}
