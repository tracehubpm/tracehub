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

import com.jcabi.github.Content;
import com.jcabi.github.Contents;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.mock.MkGithub;
import io.github.h1alexbel.ghquota.Quota;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test cases {@link GhContent}.
 *
 * @since 0.0.0
 * @checkstyle StringLiteralsConcatenationCheck (50 lines)
 */
final class GhContentTest {

    @Test
    void readsContent() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final String path = ".trace/test.yml";
        final String expected = "test: true";
        this.contribute(
            repo,
            path,
            expected
        );
        MatcherAssert.assertThat(
            "%s does not match with expected format %s"
                .formatted(path, expected),
            new GhContent(repo, path).asString(),
            new IsEqual<>(expected)
        );
    }

    @Test
    @ExtendWith(Quota.class)
    void readsContentInRealGitHub() throws Exception {
        final String path = "README.md";
        final String expected =
            "Empty GitHub repository for my integration tests,"
            + " Do not delete it!\n";
        MatcherAssert.assertThat(
            "%s does not match with expected format %s"
                .formatted(path, expected),
            new GhContent(
                new RtGithub().repos().get(
                    new Coordinates.Simple("h1alexbel/empty")
                ),
                path
            ).asString(),
            new IsEqual<>(expected)
        );
    }

    /**
     * Contribute to repository.
     *
     * @param repo Repo
     * @param path Path
     * @param yml YML
     * @return Content
     * @throws Exception if something went wrong
     * @todo #26:30min Resolve code duplication in tests.
     *  We should create a reusable code structures for contribution
     *  inside GitHub repositories (both mock and real).
     *  \@Before code block in JUnit is not an option.
     *  Code duplication should be resolved in GhNewTest.java and GhOrder.java also.
     *  Don't forget to remove this puzzle.
     */
    private Content contribute(
        final Repo repo, final String path, final String yml
    ) throws Exception {
        final Contents contents = repo.contents();
        final JsonObject json = GhContentTest
            .content(path, "theCreateMessage", yml)
            .add("committer", GhContentTest.committer())
            .build();
        return contents.create(json);
    }

    private static JsonObjectBuilder content(
        final String path, final String message, final String content
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add("content", content);
    }

    private static JsonObjectBuilder committer() {
        return Json.createObjectBuilder()
            .add("name", "joe")
            .add("email", "joe@contents.com");
    }
}
