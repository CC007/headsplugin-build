package com.github.cc007.headsplugin.integration.database.entities;

import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;


//TODO regenerate with smog (to check for copy-paste errors
@Matches(SearchEntity.class)
public final class SearchEntityMatcher extends CompositePropertyMatcher<SearchEntity> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a SearchEntity";
    private final PropertyMatcher<Set<HeadEntity>> headsMatcher = new ReflectingPropertyMatcher<>("heads", this);
    private final PropertyMatcher<Long> idMatcher = new ReflectingPropertyMatcher<>("id", this);
    private final PropertyMatcher<LocalDateTime> lastUpdatedMatcher = new ReflectingPropertyMatcher<>("lastUpdated", this);
    private final PropertyMatcher<String> searchTermMatcher = new ReflectingPropertyMatcher<>("searchTerm", this);
    private final PropertyMatcher<Long> searchCountMatcher = new ReflectingPropertyMatcher<>("searchCount", this);
    private final PropertyMatcher<Long> versionMatcher = new ReflectingPropertyMatcher<>("version", this);

    private SearchEntityMatcher(final String matchedObjectDescription, final SearchEntity template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasHeads(template.getHeads());
            hasId(template.getId());
            hasLastUpdated(template.getLastUpdated());
            hasSearchTerm(template.getSearchTerm());
            hasSearchCount(template.getSearchCount());
            hasVersion(template.getVersion());
        }
    }

    public static SearchEntityMatcher aSearchEntityThat() {
        return new SearchEntityMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static SearchEntityMatcher aSearchEntityLike(final SearchEntity template) {
        return new SearchEntityMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public SearchEntityMatcher hasHeads(final Set<HeadEntity> heads) {
        return hasHeads(equalTo(heads));
    }

    public SearchEntityMatcher hasHeads(final Matcher<? super Set<HeadEntity>> headsMatcher) {
        this.headsMatcher.setMatcher(headsMatcher);
        return this;
    }

    public SearchEntityMatcher hasId(final long id) {
        return hasId(equalTo(id));
    }

    public SearchEntityMatcher hasId(final Matcher<? super Long> idMatcher) {
        this.idMatcher.setMatcher(idMatcher);
        return this;
    }

    public SearchEntityMatcher hasLastUpdated(final LocalDateTime lastUpdated) {
        return hasLastUpdated(equalTo(lastUpdated));
    }

    public SearchEntityMatcher hasLastUpdated(final Matcher<? super LocalDateTime> lastUpdatedMatcher) {
        this.lastUpdatedMatcher.setMatcher(lastUpdatedMatcher);
        return this;
    }

    public SearchEntityMatcher hasSearchTerm(final String searchTerm) {
        return hasSearchTerm(equalTo(searchTerm));
    }

    public SearchEntityMatcher hasSearchTerm(final Matcher<? super String> searchTermMatcher) {
        this.searchTermMatcher.setMatcher(searchTermMatcher);
        return this;
    }

    public SearchEntityMatcher hasSearchCount(final long searchCount) {
        return hasSearchCount(equalTo(searchCount));
    }

    public SearchEntityMatcher hasSearchCount(final Matcher<? super Long> searchCountMatcher) {
        this.searchCountMatcher.setMatcher(searchCountMatcher);
        return this;
    }

    public SearchEntityMatcher hasVersion(final long version) {
        return hasVersion(equalTo(version));
    }

    public SearchEntityMatcher hasVersion(final Matcher<? super Long> versionMatcher) {
        this.versionMatcher.setMatcher(versionMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final SearchEntity item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
