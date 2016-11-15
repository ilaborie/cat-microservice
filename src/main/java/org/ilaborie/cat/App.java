package org.ilaborie.cat;

import org.ilaborie.cat.Cat.CatRace;
import org.jooby.*;
import org.jooby.json.Jackson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.jooby.Status.NOT_FOUND;

public class App extends Jooby {

    public static void main(final String[] args) throws Exception {
        new App().start(args);
    }

    public App() {
        // JSON mapping
        use(new Jackson());

        // FIXME Hello World

        use("/api/cats")
            .get(this::findAll)
            .get("/:id", this::findById)
            .post(this::create)
            .put(this::update)
            .delete("/:id", (Route.OneArgHandler) this::delete)
            .produces("json")
            .consumes("json");

        // Create felines herd
        onStart(registry -> {
            DB db = registry.require(DB.class);
            List<CatRace> races = Arrays.asList(CatRace.values());
            Supplier<CatRace> randomRace = () -> {
                Collections.shuffle(races); // XXX Evil side effect
                return races.get(0);
            };
            // every decent cat have an Egyptian God name
            Stream.of("Isis", "Nut", "Anubis", "Osiris", "Thoth")
                  .map(name -> new Cat(name, randomRace.get()))
                  .forEach(db::save);
        });
    }

    private List<Cat> findAll(Request req) {
        DB db = req.require(DB.class);
        return db.findAll();
    }

    private Cat findById(Request req) {
        String id = req.param("id").value();
        DB db = req.require(DB.class);
        return db.find(id).orElseThrow(() -> new Err(NOT_FOUND));
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
