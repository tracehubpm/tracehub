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
 * Test case for {@link Commit.Smart}.
 *
 * @since 0.0.0
 */
final class CommitTest {

    @Test
    void returnsSingleCommitFiles() throws Exception {
        final List<Commit> commits = new GhCommits(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("github/hooks/single-commit.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final Commit first = commits.get(0);
        MatcherAssert.assertThat(
            "Created files from 1st commit %s do not match with expected format"
                .formatted(commits.get(0)),
            first.created(),
            new IsEqual<>(
                new ListOf<>(
                    "src/main/java/git/tracehub/agents/Act.java",
                    "src/main/java/git/tracehub/agents/Job.java",
                    "src/main/java/git/tracehub/agents/Project.java",
                    "src/main/java/git/tracehub/agents/github/GhContent.java",
                    "src/main/java/git/tracehub/agents/github/GhYamlJob.java",
                    "src/main/java/git/tracehub/agents/github/GhYamlProject.java",
                    "src/main/java/git/tracehub/identity/GhIdentity.java",
                    "src/main/resources/META-INF/MANIFEST.MF",
                    "src/test/java/git/tracehub/agents/github/GhYamlProjectTest.java",
                    "src/test/java/git/tracehub/identity/GhIdentityTest.java"
                )
            )
        );
        MatcherAssert.assertThat(
            "Updated files from 1st commit %s do not match with expected format"
                .formatted(first),
            first.updated(),
            new IsEqual<>(
                new ListOf<>(
                    "pom.xml"
                )
            )
        );
        MatcherAssert.assertThat(
            "Deleted files from 1st commit %s are not empty but should be"
                .formatted(first),
            first.deleted(),
            new IsEqual<>(
                new ListOf<>()
            )
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
                        new ResourceOf("github/hooks/many-commits.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final List<String> first = commits.get(0).created();
        MatcherAssert.assertThat(
            "Created files from 1st commit %s do not match with expected format"
                .formatted(first),
            first,
            new IsEqual<>(
                new ListOf<>(
                    "src/main/java/git/tracehub/agents/Act.java"
                )
            )
        );
        final List<String> second = commits.get(1).created();
        MatcherAssert.assertThat(
            "Created files from 2nd commit %s do not match with expected format"
                .formatted(second),
            second,
            new IsEqual<>(
                new ListOf<>(
                    "src/main/java/git/tracehub/agents/github/Invites.java"
                )
            )
        );
        final List<String> third = commits.get(2).created();
        MatcherAssert.assertThat(
            "Created files from 3rd commit %s do not match with expected format"
                .formatted(third),
            third,
            new IsEqual<>(
                new ListOf<>(
                    "src/main/java/git/tracehub/agents/github/package-info.java",
                    ".trace/project.yml"
                )
            )
        );
    }

    @Test
    void returnsUpdatedFromSecondCommit() throws Exception {
        final List<Commit> commits = new GhCommits(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("github/hooks/many-commits.json").stream()
                    )
                ).pretty()
            )
        ).value();
        final List<String> updated = commits.get(1).updated();
        MatcherAssert.assertThat(
            "Updated files from 2nd commit %s are not empty, but should be"
                .formatted(updated),
            updated,
            new IsEqual<>(
                new ListOf<>()
            )
        );
    }
}
