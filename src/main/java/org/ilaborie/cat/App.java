package org.ilaborie.cat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ilaborie.cat.Cat.CatRace;
import org.jooby.*;
import org.jooby.json.Jackson;

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

        use("/api/cats")
                .get(this::findAll)
                .get("/:id", this::findById)
                .post(this::create)
                .put(this::update)
                .delete("/:id", req -> this.delete(req))
                .produces("json")
                .consumes("json");

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

    private List<Cat> findAll(Request req) {
        DB db = req.require(DB.class);
        return db.findAll();
    }

    private Cat findById(Request req) {
        String id = req.param("id").value();
        DB db = req.require(DB.class);
        Cat cat = db.find(id);
        if (cat == null) {
            throw new Err(NOT_FOUND);
        }
        return cat;
    }

    private Cat create(Request req) throws Exception {
        Cat cat = req.body(Cat.class);
        DB db = req.require(DB.class);
        db.save(cat);
        return cat;
    }

    private Cat update(Request req) throws Exception {
        Cat cat = req.body(Cat.class);
        DB db = req.require(DB.class);
        db.save(cat);
        return cat;
    }

    private Result delete(Request req) {
        String id = req.param("id").value();
        DB db = req.require(DB.class);
        db.delete(id);
        return Results.noContent();
    }

}
