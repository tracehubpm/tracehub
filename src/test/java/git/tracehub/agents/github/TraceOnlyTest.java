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

import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import java.util.List;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TraceOnly}.
 *
 * @since 0.0.0
 */
final class TraceOnlyTest {

    @Test
    void returnsOnlyTraceFiles() throws Exception {
        final Commit commit = new TraceOnly(
            new Composed(
                new GhCommits(
                    new RqFake(
                        "POST",
                        "",
                        new Jocument(
                            new JsonOf(
                                new ResourceOf(
                                    "github/hooks/many-commits.json"
                                ).stream()
                            )
                        ).pretty()
                    )
                )
            )
        );
        final List<String> created = commit.created();
        MatcherAssert.assertThat(
            "Created files %s do not match with expected format"
                .formatted(created),
            created,
            new IsEqual<>(
                new ListOf<>(
                    ".trace/project.yml"
                )
            )
        );
        final List<String> updated = commit.updated();
        MatcherAssert.assertThat(
            "Updated files %s are not empty, but should be"
                .formatted(updated),
            updated.isEmpty(),
            new IsEqual<>(true)
        );
        final List<String> deleted = commit.deleted();
        MatcherAssert.assertThat(
            "Deleted files %s are not empty, but should be"
                .formatted(deleted),
            deleted.isEmpty(),
            new IsEqual<>(true)
        );
    }

    @Test
    void returnsRepo() throws Exception {
        final Commit commit = new TraceOnly(
            new Composed(
                new GhCommits(
                    new RqFake(
                        "POST",
                        "",
                        new Jocument(
                            new JsonOf(
                                new ResourceOf(
                                    "github/hooks/many-commits.json"
                                ).stream()
                            )
                        ).pretty()
                    )
                )
            )
        );
        final String repo = commit.repo();
        final String expected = "tracehubpm/tracehub";
        MatcherAssert.assertThat(
            "Repo name %s does not match with expected %s"
                .formatted(repo, expected),
            repo,
            new IsEqual<>(expected)
        );
    }
}
