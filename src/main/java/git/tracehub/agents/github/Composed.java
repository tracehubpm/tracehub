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

import java.util.List;
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
    @SneakyThrows
    public List<String> created() {
        final List<String> created = new ListOf<>();
        this.chain.forEach(commit -> created.addAll(commit.created()));
        return created;
    }

    @Override
    @SneakyThrows
    public List<String> updated() {
        final List<String> updated = new ListOf<>();
        this.chain.forEach(commit -> updated.addAll(commit.updated()));
        return updated;
    }

    @Override
    @SneakyThrows
    public List<String> deleted() {
        final List<String> deleted = new ListOf<>();
        this.chain.forEach(commit -> deleted.addAll(commit.deleted()));
        return deleted;
    }
}
