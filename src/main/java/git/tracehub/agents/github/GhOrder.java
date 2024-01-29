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
import git.tracehub.Project;
import git.tracehub.agents.github.issues.GhNew;
import git.tracehub.facts.Order;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cactoos.Scalar;

/**
 * GitHub order execution.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class GhOrder implements Order {

    /**
     * Commit.
     */
    private final Commit commit;

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * Output.
     */
    private final Scalar<StringBuilder> out;

    @SneakyThrows
    @Override
    public void exec(final Project project) {
        new GhNew(
            project,
            new ThJobs(this.commit),
            this.repo
        ).value();
        this.out.value().append(
            "Thanks %s for GitHub webhook".formatted(
                new Repo.Smart(this.repo).coordinates()
            )
        );
    }
}
