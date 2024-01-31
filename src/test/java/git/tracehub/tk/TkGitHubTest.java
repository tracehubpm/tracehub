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
package git.tracehub.tk;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.mock.MkGithub;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.MkContribution;
import git.tracehub.validation.XsErrors;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkGitHub}.
 *
 * @since 0.0.0
 * @todo #82:30min Resolve code duplication in preparing project.
 *  We should resolve code duplication inside blocks where we setup
 *  MkGithub, each time and the same. We should consider creating JUnit
 *  extension for this configuration and similar.
 * @checkstyle StringLiteralsConcatenationCheck (200 lines)
 */
final class TkGitHubTest {

    @Test
    void returnsOkOnRequest() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.users().add("h1alexbel")
            .github()
            .repos()
            .create(new Repos.RepoCreate("test", false));
        new MkContribution(
            repo,
            ".trace/jobs/fix-me.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: DEV"
        ).value();
        new LocalGhProject("yml/projects/single-backed.yml", repo).value();
        final String response = new RsPrint(
            new TkGitHub(github, "master").act(
                new RqFake(
                    "POST",
                    "/",
                    new Jocument(
                        new JsonOf(
                            new ResourceOf(
                                "github/hooks/mock.json"
                            ).stream()
                        )
                    ).pretty()
                )
            )
        ).printBody();
        final String expected = "Thanks h1alexbel/test for GitHub webhook";
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }

    /**
     * Test case for appending project error if there is no ID
     * in YAML document.
     *
     * @throws Exception if something went wrong.
     * @todo #82:25min Do not throw exception if Project ID is NULL.
     *  We should turn this into an error (inside {@link git.tracehub.validation.ProjectValidation})
     *  and add it after {@link XsErrors}. But not crash a program.
     *  For now {@link git.tracehub.agents.github.GhProject}
     *  terminates the execution since project.identity() is NULL.
     */
    @Test
    @Disabled
    void appendsErrorsIfThereIsNoId() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.users().add("h1alexbel")
            .github()
            .repos()
            .create(new Repos.RepoCreate("test", false));
        new MkContribution(
            repo,
            ".trace/jobs/fix-me.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: DEV"
        ).value();
        new LocalGhProject("yml/projects/--no-id.yml", repo).value();
        final String response = new RsPrint(
            new TkGitHub(github, "master").act(
                new RqFake(
                    "POST",
                    "/",
                    new Jocument(
                        new JsonOf(
                            new ResourceOf(
                                "github/hooks/mock.json"
                            ).stream()
                        )
                    ).pretty()
                )
            )
        ).printBody();
        final String expected = "project.yml does not contain ID of the project";
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }

    @Test
    void returnsBadRequest() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.users().add("h1alexbel")
            .github()
            .repos()
            .create(new Repos.RepoCreate("test", false));
        new MkContribution(
            repo,
            ".trace/jobs/fix-me.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: DEV"
        ).value();
        new LocalGhProject("yml/projects/--to-xml.yml", repo).value();
        final String response = new RsPrint(
            new TkGitHub(github, "master").act(
                new RqFake(
                    "POST",
                    "/",
                    new Jocument(
                        new JsonOf(
                            new ResourceOf(
                                "github/hooks/mock.json"
                            ).stream()
                        )
                    ).pretty()
                )
            )
        ).printBody();
        final String expected =
            "`project.yml` document contains errors:"
            + "\nProject must have exactly one Architect."
            + "\nAt least one performer must have the DEV role.";
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }

    /**
     * Test case for warning suppression.
     *
     * @throws Exception if something went wrong
     * @todo #82:30min Enable this test after warnings will be implemented.
     *  Take a look at <a href="https://github.com/tracehubpm/tracehub/issues/44">this</a>
     *  issue. Don't forget to remove this puzzle.
     */
    @Test
    @Disabled
    void returnsOkOnSuppressed() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.users().add("h1alexbel")
            .github()
            .repos()
            .create(new Repos.RepoCreate("test", false));
        new MkContribution(
            repo,
            ".trace/jobs/fix-me.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description\ncost: 20 minutes\nrole: DEV"
        ).value();
        new LocalGhProject("yml/projects/pm-suppressed.yml", repo).value();
        final String response = new RsPrint(
            new TkGitHub(github, "master").act(
                new RqFake(
                    "POST",
                    "/",
                    new Jocument(
                        new JsonOf(
                            new ResourceOf(
                                "github/hooks/mock.json"
                            ).stream()
                        )
                    ).pretty()
                )
            )
        ).printBody();
        final String expected =
            "`project.yml` document contains errors:"
            + "\nProject must have exactly one Architect."
            + "\nAt least one performer must have the DEV role.";
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }
}
