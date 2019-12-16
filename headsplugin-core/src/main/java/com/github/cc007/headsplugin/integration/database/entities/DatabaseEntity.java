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
@Table(name = "databases")
@Getter
@Setter
@NoArgsConstructor
public class DatabaseEntity {

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
            name = "database_heads",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "head_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<HeadEntity> heads;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "database_tags",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<TagEntity> tags;
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "database_categories",
            joinColumns = @JoinColumn(name = "database_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<CategoryEntity> categories;

    public void addhead(HeadEntity head) {
        heads.add(head);
    }

    public void removeHead(HeadEntity head) {
        heads.remove(head);
    }

    public void addTag(TagEntity tag) {
        tags.add(tag);
    }

    public void removeTag(TagEntity tag) {
        tags.remove(tag);
    }

    public void addCategory(CategoryEntity category) {
        categories.add(category);
    }

    public void removeCategory(CategoryEntity category) {
        categories.remove(category);
    }
}
