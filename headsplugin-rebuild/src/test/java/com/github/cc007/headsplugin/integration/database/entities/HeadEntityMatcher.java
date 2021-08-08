package com.github.cc007.headsplugin.integration.database.entities;

import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.util.Set;

@Matches(HeadEntity.class)
public final class HeadEntityMatcher extends CompositePropertyMatcher<HeadEntity> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a HeadEntity";
    private final PropertyMatcher<Set<CategoryEntity>> categoriesMatcher = new ReflectingPropertyMatcher<>("categories", this);
    private final PropertyMatcher<Set<DatabaseEntity>> databasesMatcher = new ReflectingPropertyMatcher<>("databases", this);
    private final PropertyMatcher<String> headOwnerMatcher = new ReflectingPropertyMatcher<>("headOwner", this);
    private final PropertyMatcher<Long> idMatcher = new ReflectingPropertyMatcher<>("id", this);
    private final PropertyMatcher<String> nameMatcher = new ReflectingPropertyMatcher<>("name", this);
    private final PropertyMatcher<Set<SearchEntity>> searchesMatcher = new ReflectingPropertyMatcher<>("searches", this);
    private final PropertyMatcher<Set<TagEntity>> tagsMatcher = new ReflectingPropertyMatcher<>("tags", this);
    private final PropertyMatcher<String> valueMatcher = new ReflectingPropertyMatcher<>("value", this);
    private final PropertyMatcher<Long> versionMatcher = new ReflectingPropertyMatcher<>("version", this);

    private HeadEntityMatcher(final String matchedObjectDescription, final HeadEntity template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasCategories(template.getCategories());
            hasDatabases(template.getDatabases());
            hasHeadOwner(template.getHeadOwner());
            hasId(template.getId());
            hasName(template.getName());
            hasSearches(template.getSearches());
            hasTags(template.getTags());
            hasValue(template.getValue());
            hasVersion(template.getVersion());
        }
    }

    public static HeadEntityMatcher aHeadEntityThat() {
        return new HeadEntityMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static HeadEntityMatcher aHeadEntityLike(final HeadEntity template) {
        return new HeadEntityMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public HeadEntityMatcher hasCategories(final Set<CategoryEntity> categories) {
        return hasCategories(equalTo(categories));
    }

    public HeadEntityMatcher hasCategories(final Matcher<? super Set<CategoryEntity>> categoriesMatcher) {
        this.categoriesMatcher.setMatcher(categoriesMatcher);
        return this;
    }

    public HeadEntityMatcher hasDatabases(final Set<DatabaseEntity> databases) {
        return hasDatabases(equalTo(databases));
    }

    public HeadEntityMatcher hasDatabases(final Matcher<? super Set<DatabaseEntity>> databasesMatcher) {
        this.databasesMatcher.setMatcher(databasesMatcher);
        return this;
    }

    public HeadEntityMatcher hasHeadOwner(final String headOwner) {
        return hasHeadOwner(equalTo(headOwner));
    }

    public HeadEntityMatcher hasHeadOwner(final Matcher<? super String> headOwnerMatcher) {
        this.headOwnerMatcher.setMatcher(headOwnerMatcher);
        return this;
    }

    public HeadEntityMatcher hasId(final long id) {
        return hasId(equalTo(id));
    }

    public HeadEntityMatcher hasId(final Matcher<? super Long> idMatcher) {
        this.idMatcher.setMatcher(idMatcher);
        return this;
    }

    public HeadEntityMatcher hasName(final String name) {
        return hasName(equalTo(name));
    }

    public HeadEntityMatcher hasName(final Matcher<? super String> nameMatcher) {
        this.nameMatcher.setMatcher(nameMatcher);
        return this;
    }

    public HeadEntityMatcher hasSearches(final Set<SearchEntity> searches) {
        return hasSearches(equalTo(searches));
    }

    public HeadEntityMatcher hasSearches(final Matcher<? super Set<SearchEntity>> searchesMatcher) {
        this.searchesMatcher.setMatcher(searchesMatcher);
        return this;
    }

    public HeadEntityMatcher hasTags(final Set<TagEntity> tags) {
        return hasTags(equalTo(tags));
    }

    public HeadEntityMatcher hasTags(final Matcher<? super Set<TagEntity>> tagsMatcher) {
        this.tagsMatcher.setMatcher(tagsMatcher);
        return this;
    }

    public HeadEntityMatcher hasValue(final String value) {
        return hasValue(equalTo(value));
    }

    public HeadEntityMatcher hasValue(final Matcher<? super String> valueMatcher) {
        this.valueMatcher.setMatcher(valueMatcher);
        return this;
    }

    public HeadEntityMatcher hasVersion(final long version) {
        return hasVersion(equalTo(version));
    }

    public HeadEntityMatcher hasVersion(final Matcher<? super Long> versionMatcher) {
        this.versionMatcher.setMatcher(versionMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final HeadEntity item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
