package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;

import lombok.experimental.SuperBuilder;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuperBuilder
public class JpaHeadRepository extends AbstractRepository<HeadEntity, Long> implements HeadRepository {

    @Override
    public List<HeadEntity> findAllByNameIgnoreCaseContaining(String name) {
        return findAllByIgnoreCaseContaining("name", name);
    }

    @Override
    public Optional<HeadEntity> findByHeadOwner(String headOwner) {
        return findBy("headOwner", headOwner);
    }

    @Override
    public List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners) {
        return findAllByIn("headOwner", headOwners);
    }

    @Override
    public List<String> findAllHeadOwnersByHeadOwnerIn(Collection<String> headOwners) {
        TypedQuery<String> query = findProperty("headOwner", String.class,
                (criteriaBuilder, root) ->
                        root.get("headOwner")
                                .in(headOwners));
        return query.getResultList();
    }

    @Override
    public List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners) {
        TypedQuery<HeadEntity> query = find(((criteriaBuilder, headEntityRoot) -> criteriaBuilder.and(
                criteriaBuilder.equal(
                        headEntityRoot.get("databases").get("name"),
                        databaseName
                ),
                headEntityRoot.get("headOwner")
                        .in(headOwners)
        )));
        return query.getResultList();
    }

}
