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

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.mock.MkGithub;
import java.util.Map;
import javax.json.Json;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HookMap}.
 *
 * @since 0.0.0
 */
final class HookMapTest {

    @Test
    void returnsScalars() throws Exception {
        final Github github = new MkGithub("h1alexbel");
        final Repo repo = github.repos().create(new Repos.RepoCreate("cdit", false));
        final Map<String, Scalar<?>> hooks = new HookMap(
            repo,
            Json.createReader(
                new ResourceOf("github/hooks/opened/mock-new-issue.json")
                    .stream()
            ).readObject()
        ).value();
        MatcherAssert.assertThat(
            "Scalar for opened hook is NULL, but should not be",
            hooks.get("opened"),
            new IsNot<Scalar<?>>(new IsNull<>())
        );
        MatcherAssert.assertThat(
            "Scalar for labeled hook is NULL, but should not be",
            hooks.get("labeled"),
            new IsNot<Scalar<?>>(new IsNull<>())
        );
    }
}
