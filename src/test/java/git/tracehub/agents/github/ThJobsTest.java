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
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ThJobs}.
 *
 * @since 0.0.0
 */
final class ThJobsTest {

    @Test
    void returnsJobsOnly() throws Exception {
        final Commit commit =
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                Json.createReader(
                                    new ResourceOf(
                                        "github/hooks/push/more-duplicates.json"
                                    ).stream()
                                ).readObject()
                            )
                        )
                    )
                )
            );
        final List<String> created = commit.created();
        MatcherAssert.assertThat(
            "Created files %s are not empty, but should be"
                .formatted(created),
            created.isEmpty(),
            new IsEqual<>(true)
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
            "Deleted files %s do not match with expected"
                .formatted(deleted),
            deleted,
            new IsEqual<>(
                new ListOf<>(
                    ".trace/jobs/job.yml"
                )
            )
        );
    }

    @Test
    void returnsRepo() throws Exception {
        final Commit commit =
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                Json.createReader(
                                    new ResourceOf(
                                        "github/hooks/push/more-duplicates.json"
                                    ).stream()
                                ).readObject()
                            )
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
