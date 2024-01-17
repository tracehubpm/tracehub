/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2024 Tracehub.git
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package git.tracehub.agents.github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;

/**
 * Composed Commit.
 *
 * @since 0.0.0
 */
public final class Composed implements Commit {

    /**
     * Chain of commits.
     */
    private final List<Commit> chain;

    /**
     * Ctor.
     * @param scalar Scalar
     * @throws Exception if something went wrong
     */
    public Composed(final Scalar<List<Commit>> scalar) throws Exception {
        this.chain = scalar.value();
    }

    @Override
    public String repo() throws Exception {
        return this.chain.get(0).repo();
    }

    @Override
    @SneakyThrows
    public List<String> created() {
        return this.merge().get("created");
    }

    @Override
    @SneakyThrows
    public List<String> updated() {
        return this.merge().get("updated");
    }

    @Override
    @SneakyThrows
    public List<String> deleted() {
        return this.merge().get("deleted");
    }

    /**
     * Merge.
     *
     * @return Map of merged commits.
     * @todo #17:45min Resolve code duplication and extract method parts.
     *  We should introduce more manageable pieces out of this method.
     *  Don't forget to remove this puzzle.
     */
    private Map<String, List<String>> merge() {
        final Map<String, String> state = new HashMap<>(16);
        for (final Commit commit : this.chain) {
            for (final String name : commit.created()) {
                state.put(name, "created");
            }
            for (final String name : commit.updated()) {
                state.put(name, "updated");
            }
            for (final String name : commit.deleted()) {
                state.put(name, "deleted");
            }
        }
        final List<String> created = new ListOf<>();
        final List<String> updated = new ListOf<>();
        final List<String> deleted = new ListOf<>();
        for (final Map.Entry<String, String> entry : state.entrySet()) {
            switch (entry.getValue()) {
                case "created":
                    created.add(entry.getKey());
                    break;
                case "updated":
                    updated.add(entry.getKey());
                    break;
                case "deleted":
                    deleted.add(entry.getKey());
                    break;
                default:
                    throw new IllegalStateException(
                        "Unexpected value: %s".formatted(entry.getValue())
                    );
            }
        }
        final Map<String, List<String>> map = new HashMap<>(16);
        map.put("created", created);
        map.put("updated", updated);
        map.put("deleted", deleted);
        return map;
    }
}
