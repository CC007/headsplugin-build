package com.github.cc007.headsplugin.integration.database.entities;

import com.github.cc007.headsplugin.integration.database.converters.LocalDateTimeConverter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "searches")
@Getter
@Setter
@NoArgsConstructor
public class SearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    private long version;

    @Column
    private String searchTerm;

    @Column
    private int searchCount;

    @Column
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastSearched;

    @ManyToMany
    @JoinTable(
            name = "searched_heads",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    private Set<HeadEntity> heads;
}
