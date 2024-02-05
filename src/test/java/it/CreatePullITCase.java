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
package it;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import git.tracehub.agents.github.CreatePull;
import git.tracehub.agents.github.issues.RqIssue;
import git.tracehub.identity.GhIdentity;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Integration test case for {@link CreatePull}.
 *
 * @since 0.0.0
 */
final class CreatePullITCase {

    @Test
    @Tag("simulation")
    void createsPull() throws Exception {
        final Repo repo = new GhIdentity().value().repos()
            .get(new Coordinates.Simple("h1alexbel/test"));
        final Pull.Smart created = new Pull.Smart(
            new CreatePull(
                new RqIssue(
                    Json.createReader(
                        new ResourceOf("it/github/new-issue.json")
                            .stream()
                    ).readObject(),
                    repo
                ).value(),
                repo,
                "master"
            ).value()
        );
        final String title = created.title();
        final String texpected = "sync(#142)";
        MatcherAssert.assertThat(
            "Pull Request title %s does not match with expected %s"
                .formatted(title, texpected),
            title,
            new IsEqual<>(texpected)
        );
        final String login = created.author().login();
        final String lexpected = "tracehubgit";
        MatcherAssert.assertThat(
            "Pull Request author %s does not match with expected %s"
                .formatted(login, lexpected),
            login,
            new IsEqual<>(lexpected)
        );
        final int commits = new ListOf<>(created.commits()).size();
        final int expected = 1;
        MatcherAssert.assertThat(
            "Pull Request size in commits %s does not match to expected %s"
                .formatted(commits, expected),
            commits,
            new IsEqual<>(expected)
        );
        final int files = new ListOf<>(created.files()).size();
        MatcherAssert.assertThat(
            "Files size %s does not match with expected %s"
                .formatted(files, expected),
            files,
            new IsEqual<>(expected)
        );
    }
}
