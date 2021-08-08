package com.github.cc007.headsplugin.integration.database.entities;

import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.time.LocalDateTime;
import java.util.Set;

@Matches(CategoryEntity.class)
public final class CategoryEntityMatcher extends CompositePropertyMatcher<CategoryEntity> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a CategoryEntity";
    private final PropertyMatcher<Set<DatabaseEntity>> databasesMatcher = new ReflectingPropertyMatcher<>("databases", this);
    private final PropertyMatcher<Set<HeadEntity>> headsMatcher = new ReflectingPropertyMatcher<>("heads", this);
    private final PropertyMatcher<Long> idMatcher = new ReflectingPropertyMatcher<>("id", this);
    private final PropertyMatcher<LocalDateTime> lastUpdatedMatcher = new ReflectingPropertyMatcher<>("lastUpdated", this);
    private final PropertyMatcher<String> nameMatcher = new ReflectingPropertyMatcher<>("name", this);
    private final PropertyMatcher<Long> versionMatcher = new ReflectingPropertyMatcher<>("version", this);

    private CategoryEntityMatcher(final String matchedObjectDescription, final CategoryEntity template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasDatabases(template.getDatabases());
            hasHeads(template.getHeads());
            hasId(template.getId());
            hasLastUpdated(template.getLastUpdated());
            hasName(template.getName());
            hasVersion(template.getVersion());
        }
    }

    public static CategoryEntityMatcher aCategoryEntityThat() {
        return new CategoryEntityMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static CategoryEntityMatcher aCategoryEntityLike(final CategoryEntity template) {
        return new CategoryEntityMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public CategoryEntityMatcher hasDatabases(final Set<DatabaseEntity> databases) {
        return hasDatabases(equalTo(databases));
    }

    public CategoryEntityMatcher hasDatabases(final Matcher<? super Set<DatabaseEntity>> databasesMatcher) {
        this.databasesMatcher.setMatcher(databasesMatcher);
        return this;
    }

    public CategoryEntityMatcher hasHeads(final Set<HeadEntity> heads) {
        return hasHeads(equalTo(heads));
    }

    public CategoryEntityMatcher hasHeads(final Matcher<? super Set<HeadEntity>> headsMatcher) {
        this.headsMatcher.setMatcher(headsMatcher);
        return this;
    }

    public CategoryEntityMatcher hasId(final long id) {
        return hasId(equalTo(id));
    }

    public CategoryEntityMatcher hasId(final Matcher<? super Long> idMatcher) {
        this.idMatcher.setMatcher(idMatcher);
        return this;
    }

    public CategoryEntityMatcher hasLastUpdated(final LocalDateTime lastUpdated) {
        return hasLastUpdated(equalTo(lastUpdated));
    }

    public CategoryEntityMatcher hasLastUpdated(final Matcher<? super LocalDateTime> lastUpdatedMatcher) {
        this.lastUpdatedMatcher.setMatcher(lastUpdatedMatcher);
        return this;
    }

    public CategoryEntityMatcher hasName(final String name) {
        return hasName(equalTo(name));
    }

    public CategoryEntityMatcher hasName(final Matcher<? super String> nameMatcher) {
        this.nameMatcher.setMatcher(nameMatcher);
        return this;
    }

    public CategoryEntityMatcher hasVersion(final long version) {
        return hasVersion(equalTo(version));
    }

    public CategoryEntityMatcher hasVersion(final Matcher<? super Long> versionMatcher) {
        this.versionMatcher.setMatcher(versionMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final CategoryEntity item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
