package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.CategoryEntity;
import com.github.cc007.headsplugin.integration.database.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@SuperBuilder
public class JpaCategoryRepository extends AbstractRepository<CategoryEntity, Long> implements CategoryRepository {


    @Override
    public Optional<CategoryEntity> findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<CategoryEntity> criteriaQuery = criteriaBuilder
                .createQuery(CategoryEntity.class);

        Root<CategoryEntity> root = criteriaQuery.from(CategoryEntity.class);

        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), name));

        final TypedQuery<CategoryEntity> query = entityManager.createQuery(criteriaQuery);
        return getSingleResult(query);
    }

}
