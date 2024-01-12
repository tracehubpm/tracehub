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

import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.IsBlank;

/**
 * Test case for {@link GhYamlProject}.
 *
 * @since 0.0.0
 */
final class GhYamlProjectTest {

    @Test
    void returnsYaml() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add("content", "name: test")
                .add("message", "project created")
                .build()
        );
        MatcherAssert.assertThat(
            "project.yml can't be read from repo, but should be",
            new GhYamlProject(repo).asString(),
            new AllOf<>(
                new IsNot<>(new IsBlank()),
                new IsNot<>(new IsNull<>())
            )
        );
    }
}
