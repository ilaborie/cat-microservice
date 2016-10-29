package org.ilaborie.cat;

import java.util.Objects;
import java.util.UUID;

class Cat {

    private String id;
    private String name;
    private CatRace race;

    Cat() {
        super();
        this.id = UUID.randomUUID().toString();
    }

    Cat(String name, CatRace race) {
        this();
        this.race = race;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Cat{id='%s', name='%s', race=%s}", id, name, race);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cat cat = (Cat) o;
        return Objects.equals(id, cat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    CatRace getRace() {
        return race;
    }

    void setRace(CatRace race) {
        this.race = race;
    }

    public enum CatRace {
        Angora, Bengal, Cyprus, Persian, Ragdoll, Siamese, Sphynx;
    }
}
