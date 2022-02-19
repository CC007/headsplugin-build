package com.github.cc007.headsplugin.integration.database.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "heads",
        indexes = {
                @Index(name = "heads_headowner_index", columnList = "headOwner"),
                @Index(name = "heads_name_index", columnList = "name")
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HeadEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column(name = "version")
    private long version;

    @Column(name = "headOwner", unique = true)
    private String headOwner;

    @Column(name = "name")
    private String name;

    @Column(name = "value", length = 1023)
    private String value;

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<DatabaseEntity> databases = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<TagEntity> tags = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<CategoryEntity> categories = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<SearchEntity> searches = new HashSet<>();

    public Set<DatabaseEntity> getDatabases() {
        return Collections.unmodifiableSet(databases);
    }

    public Set<TagEntity> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Set<CategoryEntity> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public Set<SearchEntity> getSearches() {
        return Collections.unmodifiableSet(searches);
    }
}
