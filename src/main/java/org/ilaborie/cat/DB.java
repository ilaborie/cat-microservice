package org.ilaborie.cat;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An in-memory database.
 */
@Singleton
public class DB {

    private Map<String, Cat> db = new HashMap<>();

    public List<Cat> findAll() {
        return db.values().stream()
                .collect(Collectors.toList());
    }

    public Optional<Cat> find(String id) {
        return Optional.ofNullable(db.get(id));
    }

    public void save(Cat cat) {
        db.put(cat.getId(), cat);
    }

    public void delete(String id) {
        db.remove(id);
    }

}
