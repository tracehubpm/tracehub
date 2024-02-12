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
import com.jcabi.xml.XSL;
import git.tracehub.Project;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.Composed;
import git.tracehub.agents.github.GhCommits;
import git.tracehub.agents.github.ThJobs;
import git.tracehub.agents.github.TraceLogged;
import git.tracehub.agents.github.TraceOnly;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.MkContribution;
import git.tracehub.extensions.SheetsIn;
import git.tracehub.extensions.ValidationPipeline;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link GhNew}.
 *
 * @since 0.0.0
 * @todo #64:35min: Check the job creation if there is no backlog rules.
 *  We should validate job even there is no backlog:rules in project.yml.
 *  Depends on this <a href="https://github.com/tracehubpm/tracehub/issues/116">one</a>.
 * @checkstyle StringLiteralsConcatenationCheck (150 lines)
 */
final class GhNewTest {

    @Test
    @ExtendWith(ValidationPipeline.class)
    @SheetsIn(
        {
            "struct",
            "errors",
            "estimate",
            "words"
        }
    )
    void createsNewIssues(final Map<String, XSL> sheets) throws Exception {
        final MkGithub github = new MkGithub();
        github.users().add("h1alexbel");
        github.users().add("hizmailovich");
        final Repo repo = github.randomRepo();
        new MkContribution(
            repo,
            ".trace/jobs/job1.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description that passes validation\ncost: 25\nrole: DEV"
        ).value();
        new MkContribution(
            repo,
            ".trace/jobs/job2.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description that passes validation\ncost: 25\nrole: ARC"
        ).value();
        final Commit commit =
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                Json.createReader(
                                    new ResourceOf(
                                        "github/hooks/push/created-jobs.json"
                                    ).stream()
                                ).readObject()
                            )
                        )
                    )
                )
            );
        final Project project = new LocalGhProject(
            "yml/projects/with-suppressions.yml", repo
        ).value();
        final List<Issue> created = new GhNew(
            project,
            commit,
            repo,
            () -> sheets
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

    @Test
    @ExtendWith(ValidationPipeline.class)
    @SheetsIn(
        {
            "struct",
            "errors",
            "estimate",
            "words"
        }
    )
    void createsAndLabelsOnlyValidJobs(final Map<String, XSL> sheets) throws Exception {
        final MkGithub github = new MkGithub();
        github.users().add("h1alexbel");
        github.users().add("hizmailovich");
        final Repo repo = github.randomRepo();
        new MkContribution(
            repo,
            ".trace/jobs/job1.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description blah blah blah\ncost: 25\nrole: DEV"
        ).value();
        new MkContribution(
            repo,
            ".trace/jobs/job2.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20\nrole: ARC"
        ).value();
        final Commit commit =
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                Json.createReader(
                                    new ResourceOf(
                                        "github/hooks/push/created-jobs.json"
                                    ).stream()
                                ).readObject()
                            )
                        )
                    )
                )
            );
        final Project project = new LocalGhProject(
            "yml/projects/with-suppressions.yml", repo
        ).value();
        final List<Issue> created = new GhNew(
            project,
            commit,
            repo,
            () -> sheets
        ).value();
        final int size = created.size();
        final int expected = 1;
        MatcherAssert.assertThat(
            "Created issues size %s does not match with expected %s"
                .formatted(size, expected),
            size,
            new IsEqual<>(expected)
        );
    }
}
