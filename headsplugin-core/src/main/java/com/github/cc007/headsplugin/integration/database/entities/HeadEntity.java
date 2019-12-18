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
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private long id;

    @Version
    @Column
    private long version;

    @Column(unique = true)
    private String headOwner;

    @Column
    private String name;

    @Column
    private String value;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<DatabaseEntity> databases;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<TagEntity> tags;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<CategoryEntity> categories;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "heads"
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<SearchEntity> searches;

//    public void addDatabase(DatabaseEntity database){
//        databases.add(database);
//    }
//
//    public void removeDatabase(DatabaseEntity database){
//        databases.remove(database);
//    }
//
//    public void addTag(TagEntity tag){
//        tags.add(tag);
//    }
//
//    public void removeTag(TagEntity tag){
//        tags.remove(tag);
//    }
//
//    public void addCategory(CategoryEntity category){
//        categories.add(category);
//    }
//
//    public void removeCategory(CategoryEntity category){
//        categories.remove(category);
//    }
//
//    public void addSearch(SearchEntity search){
//        searches.add(search);
//    }
//
//    public void removeSearch(SearchEntity search){
//        searches.remove(search);
//    }

}
