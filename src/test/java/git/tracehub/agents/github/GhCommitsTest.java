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
import nl.altindag.log.LogCaptor;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link GhCommits}.
 *
 * @since 0.0.0
 */
final class GhCommitsTest {

    @Test
    void returnsSingleCommit() throws Exception {
        final List<Commit> commits = new GhCommits(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("github/hooks/push/single-commit.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final int expected = 1;
        MatcherAssert.assertThat(
            "Commits %s size does not match with expected %s"
                .formatted(commits, expected),
            commits.size(),
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsManyCommits() throws Exception {
        final List<Commit> commits = new GhCommits(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("github/hooks/push/many-commits.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final int expected = 3;
        MatcherAssert.assertThat(
            "Commits %s size does not match with expected %s"
                .formatted(commits, expected),
            commits.size(),
            new IsEqual<>(expected)
        );
    }

    @Test
    void logsFoundCommits() throws Exception {
        final LogCaptor capt = LogCaptor.forClass(GhCommits.class);
        final List<Commit> commits = new GhCommits(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("github/hooks/push/many-commits.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final List<String> infos = capt.getInfoLogs();
        MatcherAssert.assertThat(
            "Logs %s for commits %s do not match with expected"
                .formatted(infos, commits),
            infos,
            new IsEqual<>(
                new ListOf<>(
                    "found 3 commit(s) in tracehubpm/tracehub"
                )
            )
        );
    }
}
