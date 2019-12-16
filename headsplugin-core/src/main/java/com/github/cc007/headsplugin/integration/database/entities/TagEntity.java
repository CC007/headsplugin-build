package com.github.cc007.headsplugin.integration.database.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class TagEntity {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column
    private long version;

    @Column(unique = true)
    private String name;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tagged_heads",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<HeadEntity> heads;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "tags"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<DatabaseEntity> databases;

    public void addhead(HeadEntity head) {
        heads.add(head);
    }

    public void removeHead(HeadEntity head) {
        heads.remove(head);
    }

//    public void addDatabase(DatabaseEntity database){
//        databases.add(database);
//    }
//
//    public void removeDatabase(DatabaseEntity database){
//        databases.remove(database);
//    }
}
