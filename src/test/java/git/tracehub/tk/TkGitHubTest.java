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
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkGitHub}.
 *
 * @since 0.0.0
 * @checkstyle StringLiteralsConcatenationCheck (50 lines)
 */
final class TkGitHubTest {

    @Test
    void returnsOkOnRequest() throws Exception {
        final String expected = "Thanks h1alexbel/test for GitHub webhook";
        final MkGithub github = new MkGithub();
        final Repo repo = github.users().add("h1alexbel")
            .github()
            .repos()
            .create(new Repos.RepoCreate("test", false));
        repo.contents().create(
            TkGitHubTest.content(
                ".trace/jobs/fix-me.yml",
                "mocked fix me",
                "label: Update License year to 2024\ndescription:"
                + " test description\ncost: 20 minutes\nrole: DEV"
            ).build()
        );
        new LocalGhProject("github/projects/single-backed.yml", repo).value();
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
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }

    private static JsonObjectBuilder content(
        final String path, final String message, final String content
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add("content", content);
    }
}
