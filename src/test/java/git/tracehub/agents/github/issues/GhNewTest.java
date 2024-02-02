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
package git.tracehub.agents.github.issues;

import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import git.tracehub.Project;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.Composed;
import git.tracehub.agents.github.GhCommits;
import git.tracehub.agents.github.ThJobs;
import git.tracehub.agents.github.TraceLogged;
import git.tracehub.agents.github.TraceOnly;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.MkContribution;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import java.util.List;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link GhNew}.
 *
 * @since 0.0.0
 * @checkstyle StringLiteralsConcatenationCheck (100 lines)
 */
final class GhNewTest {

    @Test
    void createsNewIssues() throws Exception {
        final MkGithub github = new MkGithub();
        github.users().add("h1alexbel");
        github.users().add("hizmailovich");
        final Repo repo = github.randomRepo();
        new MkContribution(
            repo,
            ".trace/jobs/job1.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: DEV"
        ).value();
        new MkContribution(
            repo,
            ".trace/jobs/job2.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: ARC"
        ).value();
        final Commit commit =
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                new RqFake(
                                    "POST",
                                    "",
                                    new Jocument(
                                        new JsonOf(
                                            new ResourceOf(
                                                "github/hooks/push/created-jobs.json"
                                            ).stream()
                                        )
                                    ).pretty()
                                )
                            )
                        )
                    )
                )
            );
        final Project project = new LocalGhProject(
            "yml/projects/many-performers.yml", repo
        ).value();
        final List<Issue> created = new GhNew(
            project,
            commit,
            repo
        ).value();
        final int expected = 2;
        MatcherAssert.assertThat(
            "Created issues %s size does not match with expected %s"
                .formatted(created, expected),
            created.size(),
            new IsEqual<>(expected)
        );
        final Issue.Smart first = new Issue.Smart(created.get(0));
        MatcherAssert.assertThat(
            "1st issue %s is not opened, but it should be"
                .formatted(first),
            first.isOpen(),
            new IsEqual<>(true)
        );
        final Issue.Smart second = new Issue.Smart(created.get(1));
        MatcherAssert.assertThat(
            "2nd issue %s is not opened, but it should be"
                .formatted(second),
            second.isOpen(),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "1st issue %s is not assigned, but should be"
                .formatted(first),
            first.hasAssignee(),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "2nd issue %s is not assigned, but should be"
                .formatted(second),
            second.hasAssignee(),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "1st issue %s is not synced, but should be"
                .formatted(first),
            new IssueLabels.Smart(first.labels()).contains("synced"),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "2nd issue %s is not synced, but should be"
                .formatted(second),
            new IssueLabels.Smart(second.labels()).contains("synced"),
            new IsEqual<>(true)
        );
    }
}
