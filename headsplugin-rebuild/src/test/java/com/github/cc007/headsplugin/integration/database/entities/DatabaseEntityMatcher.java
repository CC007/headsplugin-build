package com.github.cc007.headsplugin.integration.database.entities;

import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.util.Set;

@Matches(DatabaseEntity.class)
public final class DatabaseEntityMatcher extends CompositePropertyMatcher<DatabaseEntity> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a DatabaseEntity";
    private final PropertyMatcher<Set<CategoryEntity>> categoriesMatcher = new ReflectingPropertyMatcher<>("categories", this);
    private final PropertyMatcher<Set<HeadEntity>> headsMatcher = new ReflectingPropertyMatcher<>("heads", this);
    private final PropertyMatcher<Long> idMatcher = new ReflectingPropertyMatcher<>("id", this);
    private final PropertyMatcher<String> nameMatcher = new ReflectingPropertyMatcher<>("name", this);
    private final PropertyMatcher<Set<TagEntity>> tagsMatcher = new ReflectingPropertyMatcher<>("tags", this);
    private final PropertyMatcher<Long> versionMatcher = new ReflectingPropertyMatcher<>("version", this);

    private DatabaseEntityMatcher(final String matchedObjectDescription, final DatabaseEntity template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasCategories(template.getCategories());
            hasHeads(template.getHeads());
            hasId(template.getId());
            hasName(template.getName());
            hasTags(template.getTags());
            hasVersion(template.getVersion());
        }
    }

    public static DatabaseEntityMatcher aDatabaseEntityThat() {
        return new DatabaseEntityMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static DatabaseEntityMatcher aDatabaseEntityLike(final DatabaseEntity template) {
        return new DatabaseEntityMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public DatabaseEntityMatcher hasCategories(final Set<CategoryEntity> categories) {
        return hasCategories(equalTo(categories));
    }

    public DatabaseEntityMatcher hasCategories(final Matcher<? super Set<CategoryEntity>> categoriesMatcher) {
        this.categoriesMatcher.setMatcher(categoriesMatcher);
        return this;
    }

    public DatabaseEntityMatcher hasHeads(final Set<HeadEntity> heads) {
        return hasHeads(equalTo(heads));
    }

    public DatabaseEntityMatcher hasHeads(final Matcher<? super Set<HeadEntity>> headsMatcher) {
        this.headsMatcher.setMatcher(headsMatcher);
        return this;
    }

    public DatabaseEntityMatcher hasId(final long id) {
        return hasId(equalTo(id));
    }

    public DatabaseEntityMatcher hasId(final Matcher<? super Long> idMatcher) {
        this.idMatcher.setMatcher(idMatcher);
        return this;
    }

    public DatabaseEntityMatcher hasName(final String name) {
        return hasName(equalTo(name));
    }

    public DatabaseEntityMatcher hasName(final Matcher<? super String> nameMatcher) {
        this.nameMatcher.setMatcher(nameMatcher);
        return this;
    }

    public DatabaseEntityMatcher hasTags(final Set<TagEntity> tags) {
        return hasTags(equalTo(tags));
    }

    public DatabaseEntityMatcher hasTags(final Matcher<? super Set<TagEntity>> tagsMatcher) {
        this.tagsMatcher.setMatcher(tagsMatcher);
        return this;
    }

    public DatabaseEntityMatcher hasVersion(final long version) {
        return hasVersion(equalTo(version));
    }

    public DatabaseEntityMatcher hasVersion(final Matcher<? super Long> versionMatcher) {
        this.versionMatcher.setMatcher(versionMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final DatabaseEntity item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
