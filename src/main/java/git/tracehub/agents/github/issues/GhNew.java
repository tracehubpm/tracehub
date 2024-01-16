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
import com.jcabi.github.Repo;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.GhJob;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;

/**
 * New GitHub issues.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class GhNew implements Scalar<List<Issue>> {

    /**
     * Commit.
     */
    private final Commit commit;

    /**
     * Repo.
     */
    private final Repo repo;

    @Override
    public List<Issue> value() throws Exception {
        return this.commit.created().stream()
            .map(
                new Function<String, Issue>() {
                    @SneakyThrows
                    @Override
                    public Issue apply(final String name) {
                        return new CreateIssue(
                            new GhJob(
                                GhNew.this.repo,
                                name,
                                new TextOf(
                                    new ResourceOf(
                                        "git/tracehub/agents/github/Issue.md"
                                    )
                                )
                            ),
                            GhNew.this.repo.issues()
                        ).value();
                    }
                })
            .toList();
    }
}
