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

import com.amihaiemil.eoyaml.Yaml;
import com.jcabi.github.Comment;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import git.tracehub.agents.github.GhJob;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.RepoWithCollaborator;
import git.tracehub.extensions.RepoWithCollaborators;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link AssignOnIssue}.
 *
 * @since 0.0.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class AssignOnIssueTest {

    @Test
    @ExtendWith(RepoWithCollaborator.class)
    void assignsOnIssue(final Repo repo) throws Exception {
        final String expected = "h1alexbel";
        final Issue.Smart issue = new Issue.Smart(
            new AssignOnIssue(
                new LocalGhProject("github/projects/single-dev-arc.yml", repo).value(),
                new GhJob(
                    Yaml.createYamlInput(
                        new ResourceOf("github/jobs/for-arc.yml").stream()
                    ).readYamlMapping(),
                    new TextOf(
                        new ResourceOf(
                            "git/tracehub/agents/github/Issue.md"
                        )
                    )
                ),
                () -> repo.issues().create(
                    "Test issue", "this needs to be fixed:..."
                )
            ).value()
        );
        final String who = issue.assignee().login();
        MatcherAssert.assertThat(
            "Issue assignee %s does not match with expected %s"
                .formatted(who, expected),
            who,
            new IsEqual<>(expected)
        );
        MatcherAssert.assertThat(
            "ARC label does not present, but should be",
            new IssueLabels.Smart(issue.labels()).contains("ARC"),
            new IsEqual<>(true)
        );
    }

    @Test
    @ExtendWith(RepoWithCollaborator.class)
    void assignsDevOnUnspecifiedRole(final Repo repo) throws Exception {
        final String expected = "h1alexbel";
        final Issue.Smart issue = new Issue.Smart(
            new AssignOnIssue(
                new LocalGhProject("github/projects/single-dev-arc.yml", repo).value(),
                new GhJob(
                    Yaml.createYamlInput(
                        new ResourceOf("github/jobs/no-role.yml").stream()
                    ).readYamlMapping(),
                    new TextOf(
                        new ResourceOf(
                            "git/tracehub/agents/github/Issue.md"
                        )
                    )
                ),
                () -> repo.issues().create(
                    "Test issue", "this needs to be fixed:..."
                )
            ).value()
        );
        final String who = issue.assignee().login();
        MatcherAssert.assertThat(
            "Issue assignee %s does not match with expected %s"
                .formatted(who, expected),
            who,
            new IsEqual<>(expected)
        );
        MatcherAssert.assertThat(
            "DEV label does not present, but should be",
            new IssueLabels.Smart(issue.labels()).contains("DEV"),
            new IsEqual<>(true)
        );
    }

    @Test
    @ExtendWith(RepoWithCollaborators.class)
    void choosesWhomToAssign(final Repo repo) throws Exception {
        MatcherAssert.assertThat(
            "Issue was not assigned, but it should be",
            new Issue.Smart(
                new AssignOnIssue(
                    new LocalGhProject("github/projects/many-performers.yml", repo).value(),
                    new GhJob(
                        Yaml.createYamlInput(
                            new ResourceOf("github/jobs/no-role.yml").stream()
                        ).readYamlMapping(),
                        new TextOf(
                            new ResourceOf(
                                "git/tracehub/agents/github/Issue.md"
                            )
                        )
                    ),
                    () -> repo.issues().create(
                        "Test issue", "this needs to be fixed:..."
                    )
                ).value()
            ).hasAssignee(),
            new IsEqual<>(true)
        );
    }

    @Test
    @ExtendWith(RepoWithCollaborator.class)
    void commentsAfterAssigning(final Repo repo) throws Exception {
        final String expected = "@h1alexbel, take a look, please";
        final String text = new Comment.Smart(
            new Issue.Smart(
                new AssignOnIssue(
                    new LocalGhProject("github/projects/single-dev-arc.yml", repo).value(),
                    new GhJob(
                        Yaml.createYamlInput(
                            new ResourceOf("github/jobs/for-arc.yml").stream()
                        ).readYamlMapping(),
                        new TextOf(
                            new ResourceOf(
                                "git/tracehub/agents/github/Issue.md"
                            )
                        )
                    ),
                    () -> repo.issues().create(
                        "Test issue", "this needs to be fixed:..."
                    )
                ).value()
            ).comments().get(1)
        ).body();
        MatcherAssert.assertThat(
            "Posted comment %s does not match with expected %s"
                .formatted(text, expected),
            text,
            new IsEqual<>(expected)
        );
    }

    @Test
    void postsCommentWhenNoCandidates() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.collaborators().add("h1alexbel");
        final String text = new Comment.Smart(
            new Issue.Smart(
                new AssignOnIssue(
                    new LocalGhProject("github/projects/single-dev-arc.yml", repo).value(),
                    new GhJob(
                        Yaml.createYamlInput(
                            new ResourceOf("github/jobs/testing.yml").stream()
                        ).readYamlMapping(),
                        new TextOf(
                            new ResourceOf(
                                "git/tracehub/agents/github/Issue.md"
                            )
                        )
                    ),
                    () -> repo.issues().create(
                        "Testing", "lets test this one.."
                    )
                ).value()
            ).comments().get(1)
        ).body();
        final String expected =
            "@h1alexbel, I'm failed to find any eligible candidates for this issue.";
        MatcherAssert.assertThat(
            "Posted comment %s does not match with expected %s"
                .formatted(text, expected),
            text,
            new IsEqual<>(expected)
        );
    }
}
