package org.ilaborie.cat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An in-memory database.
 */
@Singleton
public class DB {

  private Cache<String, Cat> db = CacheBuilder.newBuilder()
      .maximumSize(100)
      .build();

  public List<Cat> findAll() {
    return db.asMap().values().stream()
        .collect(Collectors.toList());
  }

  public Cat find(String id) {
    return db.getIfPresent(id);
  }

  public void save(Cat cat) {
    db.put(cat.getId(), cat);
  }

  public void delete(String id) {
    db.invalidate(id);
  }

}
