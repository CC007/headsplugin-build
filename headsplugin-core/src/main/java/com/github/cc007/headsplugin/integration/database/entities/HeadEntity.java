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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Set;

@Entity
@Table(name = "heads")
@Getter
@Setter
@NoArgsConstructor
public class HeadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column
    private long version;

    @Column
    private String headOwner;

    @Column
    private String name;

    @Column
    private String value;

    @ManyToMany(mappedBy = "heads")
    private Set<DatabaseEntity> databases;

    @ManyToMany(mappedBy = "heads")
    private Set<TagEntity> tags;

    @ManyToMany(mappedBy = "heads")
    private Set<TagEntity> searches;
}
