package org.liquigraph.ogm.entities;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class SomeEntity {

    @Id
    private Long id;

    @Property
    private String name;

    @Property(name = "name2")
    private String someOtherName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSomeOtherName() {
        return someOtherName;
    }

    public void setSomeOtherName(String someOtherName) {
        this.someOtherName = someOtherName;
    }
}
