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

import com.jcabi.github.Commit;
import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Reference;
import com.jcabi.github.Repo;
import java.time.Instant;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.takes.Request;

/**
 * Create GitHub pull request.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class CreatePull implements Scalar<Pull> {

    /**
     * Request.
     */
    private final Request request;

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * Target branch.
     */
    private final String target;

    @Override
    public Pull value() throws Exception {
        final Issue.Smart submitted = new Issue.Smart(
            this.repo.issues().get(
                Json.createReader(this.request.body())
                    .readObject()
                    .getJsonObject("issue")
                    .getInt("number")
            )
        );
        final JsonObject head = this.repo.branches()
            .find(this.target)
            .commit().json();
        final Commit commit = new CreateCommit(
            this.repo,
            new CommitBody(
                new CreateTree(
                    this.repo,
                    new TreeBody(head, submitted)
                ),
                head,
                submitted
            )
        ).value();
        Reference reference;
        final long millis = Instant.now().toEpochMilli();
        final String rfmt = "refs/heads/sync-%s".formatted(millis);
        try {
            this.repo.git().references().get(rfmt).json();
            reference = this.repo.git().references().get(rfmt);
        } catch (final AssertionError err) {
            reference = this.repo.git().references().create(
                rfmt,
                commit.sha()
            );
        }
        return this.repo.pulls().create(
            "sync(#%s)".formatted(submitted.number()),
            reference.ref(),
            this.target
        );
    }
}
