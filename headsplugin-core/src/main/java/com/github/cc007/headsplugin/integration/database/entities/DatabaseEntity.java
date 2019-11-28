package com.github.cc007.headsplugin.integration.database.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Set;

@Entity
@Table(name = "databases")
@Getter
@Setter
@NoArgsConstructor
public class DatabaseEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    private long version;

    @Column
    private String name;

    @ManyToMany
    @JoinTable(
            name = "database_heads",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    private Set<HeadEntity> heads;

    @ManyToMany
    @JoinTable(
            name = "database_tags",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags;
    @ManyToMany
    @JoinTable(
            name = "database_categories",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<TagEntity> categories;
}
