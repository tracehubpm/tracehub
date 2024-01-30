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

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.mock.MkGithub;
import git.tracehub.extensions.MkContribution;
import io.github.h1alexbel.ghquota.Quota;
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
        new MkContribution(
            repo,
            path,
            expected
        ).value();
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
}
