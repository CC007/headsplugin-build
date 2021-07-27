package com.github.cc007.headsplugin.integration.database.entities;

import com.github.cc007.headsplugin.integration.database.converters.LocalDateTimeConverter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(name = "searches",
        indexes = {
                @Index(name = "searches_searchTerm_index", columnList = "searchTerm")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SearchEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column(name = "version")
    private long version;

    @Column(name = "searchTerm", unique = true)
    private String searchTerm;

    @Column(name = "searchCount")
    @Setter(AccessLevel.NONE)
    private int searchCount;

    @Column(name = "lastUpdated")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastUpdated;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "searched_heads",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<HeadEntity> heads = new HashSet<>();

    public Set<HeadEntity> getHeads() {
        return Collections.unmodifiableSet(heads);
    }

    public void addhead(HeadEntity head) {
        heads.add(head);
    }

    public void removeHead(HeadEntity head) {
        heads.remove(head);
    }

    public void resetSearchCount() {
        searchCount = 0;
    }

    public void incrementSearchCount() {
        searchCount++;
    }
}
