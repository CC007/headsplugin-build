package com.github.cc007.headsplugin.integration.database.entities;

import com.github.cc007.headsplugin.integration.database.converters.LocalDateTimeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories",
        indexes = {
                @Index(name = "categories_name_index", columnList = "name")
        }
)
@Getter
@Setter
@ToString()
@NoArgsConstructor
public class CategoryEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "lastUpdated", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastUpdated;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "categorized_heads",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<HeadEntity> heads = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "categories"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<DatabaseEntity> databases = new HashSet<>();

    public Set<HeadEntity> getHeads() {
        return Collections.unmodifiableSet(heads);
    }

    public Set<DatabaseEntity> getDatabases() {
        return Collections.unmodifiableSet(databases);
    }

    public void addhead(HeadEntity head) {
        heads.add(head);
    }
}
