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

import java.net.URI;
import java.util.List;
import javax.json.JsonValue;
import lombok.RequiredArgsConstructor;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;

/**
 * Commit on GitHub.
 *
 * @since 0.0.0
 */
public interface Commit {

    /**
     * Repository name.
     *
     * @return Repo name as a string
     * @throws Exception if something went wrong
     */
    String repo() throws Exception;

    /**
     * Created files.
     *
     * @return List of file names
     */
    List<String> created();

    /**
     * Updated files.
     *
     * @return List of file names
     */
    List<String> updated();

    /**
     * Deleted files.
     *
     * @return List of file names
     */
    List<String> deleted();

    /**
     * Smart commit.
     *
     * @since 0.0.0
     */
    @RequiredArgsConstructor
    final class Smart implements Commit {

        /**
         * Commit.
         */
        private final JsonValue commit;

        @Override
        public String repo() throws Exception {
            final String[] split = new URI(
                this.commit.asJsonObject().getString("url")
            ).getPath().split("/");
            return new Joined("/", new ListOf<>(split[1], split[2]))
                .asString();
        }

        @Override
        public List<String> created() {
            final List<String> added = new ListOf<>();
            this.commit.asJsonObject().getJsonArray("added")
                .forEach(name -> added.add(name.toString().replace("\"", "")));
            return added;
        }

        @Override
        public List<String> updated() {
            final List<String> updated = new ListOf<>();
            this.commit.asJsonObject().getJsonArray("modified")
                .forEach(name -> updated.add(name.toString().replace("\"", "")));
            return updated;
        }

        @Override
        public List<String> deleted() {
            final List<String> deleted = new ListOf<>();
            this.commit.asJsonObject().getJsonArray("removed")
                .forEach(name -> deleted.add(name.toString().replace("\"", "")));
            return deleted;
        }
    }
}
