package com.github.cc007.headsplugin.integration.database.repositories.jpa;

import com.github.cc007.headsplugin.integration.database.entities.HeadEntity;
import com.github.cc007.headsplugin.integration.database.repositories.HeadRepository;
import com.github.cc007.headsplugin.integration.database.services.ManagedEntityService;
import com.github.cc007.headsplugin.integration.database.services.QueryService;

import lombok.RequiredArgsConstructor;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaHeadRepository implements HeadRepository {

    private final QueryService queryService;
    private final ManagedEntityService managedEntityService;

    @Override
    public Optional<HeadEntity> findByHeadOwner(String headOwner) {
        return queryService.findByProperty(HeadEntity.class, "headOwner", headOwner);
    }

    @Override
    public List<HeadEntity> findAllByHeadOwnerIn(Collection<String> headOwners) {
        return queryService.findAllByPropertyIn(HeadEntity.class, "headOwner", headOwners);
    }

    @Override
    public List<String> findAllHeadOwnersByHeadOwnerIn(Collection<String> headOwners) {
        TypedQuery<String> query = queryService.querySelectionByCondition(HeadEntity.class,
                root -> root.get("headOwner"), String.class,
                (criteriaBuilder, root) -> root.get("headOwner").in(headOwners)
        );
        return query.getResultList();
    }

    @Override
    public List<HeadEntity> findAllByDatabases_NameAndHeadOwnerIn(String databaseName, Collection<String> headOwners) {
        TypedQuery<HeadEntity> query = queryService.queryByCondition(HeadEntity.class, ((criteriaBuilder, headEntityRoot) -> criteriaBuilder.and(
                criteriaBuilder.equal(
                        headEntityRoot.get("databases").get("name"),
                        databaseName
                ),
                headEntityRoot.get("headOwner").in(headOwners)
        )));
        return query.getResultList();
    }

    @Override
    public List<HeadEntity> findAllByNameIgnoreCaseContaining(String name) {
        TypedQuery<HeadEntity> query = queryService.queryByCondition(HeadEntity.class, (criteriaBuilder, root) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
        ));
        return query.getResultList();
    }

    @Override
    public HeadEntity manageNew() {
        return managedEntityService.manageNew(HeadEntity.class);
    }
}
