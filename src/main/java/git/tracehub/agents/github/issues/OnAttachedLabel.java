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
import git.tracehub.agents.github.CreatePull;
import git.tracehub.agents.github.GhProject;
import git.tracehub.facts.Architect;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * On attached label to an issue in GitHub.
 * Webhook payload is issues:labeled, read
 * <a href="https://docs.github.com/en/webhooks/webhook-events-and-payloads?actionType=labeled#issues">this</a>
 * for more info.
 * @since 0.0.0
 * @todo #126:45min Introduce unit tests for OnAttachedLabel.java
 *  We should introduce unit tests for this class. It one depends on
 *  <a href="https://github.com/tracehubpm/tracehub/issues/130">this</a> issue.
 */
@RequiredArgsConstructor
public final class OnAttachedLabel implements Scalar<Issue> {

    /**
     * Request.
     */
    private final JsonObject request;

    /**
     * Repo.
     */
    private final Repo repo;

    @Override
    public Issue value() throws Exception {
        final String label = this.request.getJsonObject("label").getString("name");
        final Issue issue = new RqIssue(this.request, this.repo).value();
        if ("bug".equals(label)) {
            issue.comments().post(
                "@%s, I've created #%s for adding this issue into work queue."
                    .formatted(
                        new Architect(new GhProject(this.repo)).name(),
                        new CreatePull(issue, this.repo, "master").value()
                            .number()
                    )
            );
        }
        return issue;
    }
}
