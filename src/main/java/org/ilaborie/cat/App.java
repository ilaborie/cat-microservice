package org.ilaborie.cat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ilaborie.cat.Cat.CatRace;
import org.jooby.Err;
import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.json.Jackson;
import org.jooby.swagger.SwaggerUI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NON_PRIVATE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.SETTER;
import static org.jooby.Status.NOT_FOUND;

public class App extends Jooby {

  {
    // JSON mapping
    ObjectMapper mapper = new ObjectMapper()
        .setVisibility(GETTER, NON_PRIVATE)
        .setVisibility(SETTER, NON_PRIVATE);
    use(new Jackson(mapper));

    /**
     *
     * Everything about your Cats.
     */
    use("/api/cats")
        /**
         *
         * List cats ordered by id.
         *
         * @return Cats ordered by name.
         */
        .get(req -> {
          DB db = req.require(DB.class);
          return db.findAll();
        })
        /**
         *
         * Find cat by ID
         *
         * @param id Cat ID.
         * @return Returns <code>200</code> with a single cat or <code>404</code>
         */
        .get("/:id", req -> {
          String id = req.param("id").value();
          DB db = req.require(DB.class);
          Cat cat = db.find(id);
          if (cat == null) {
            throw new Err(NOT_FOUND);
          }
          return cat;
        })
        /**
         *
         * Add a new cat to the store.
         *
         * @param body Cat object that needs to be added to the store.
         * @return Returns a saved cat.
         */
        .post(req -> {
          Cat cat = req.body(Cat.class);
          DB db = req.require(DB.class);
          db.save(cat);
          return cat;
        })
        /**
         *
         * Update an existing cat.
         *
         * @param body Cat object that needs to be updated.
         * @return Returns a saved cat.
         */
        .put(req -> {
          Cat cat = req.body(Cat.class);
          DB db = req.require(DB.class);
          db.save(cat);
          return cat;
        })
        /**
         *
         * Deletes a cat by ID.
         *
         * @param id Cat ID.
         * @return A <code>204</code>
         */
        .delete("/:id", req -> {
          String id = req.param("id").value();
          DB db = req.require(DB.class);
          db.delete(id);
          return Results.noContent();
        })
        .produces("json")
        .consumes("json");

    // Provide Swagger
    new SwaggerUI()
        .tag(route -> "cats")
        .install(this);

    // Create felines herd
    onStart(registry -> {
      DB db = registry.require(DB.class);
      List<CatRace> races = Arrays.asList(CatRace.values());
      Supplier<CatRace> randomRace = () -> {
        Collections.shuffle(races);
        return races.get(0);
      };
      // every decent cat have an Egyptian God name
      Stream.of("Isis", "Nut", "Anubis", "Osiris", "Thoth")
          .map(name -> new Cat(name, randomRace.get()))
          .forEach(db::save);
    });
  }

  public static void main(final String[] args) throws Exception {
    new App().start(args);
  }

}
