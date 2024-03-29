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
import com.jcabi.log.Logger;
import git.tracehub.Job;
import git.tracehub.Performer;
import git.tracehub.Project;
import git.tracehub.facts.Architect;
import git.tracehub.facts.HasRole;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;

/**
 * Assign performer on issue in GitHub.
 *
 * @since 0.0.0
 * @todo #41:60min How to assign a performer to an issue?
 *  We should decide how to assign performer, for now its
 *  just a random. That should be replaced with something
 *  more reasonable.
 *  Don't forget to remove this puzzle.
 */
@RequiredArgsConstructor
public final class AssignOnIssue implements Scalar<Issue> {

    /**
     * Project.
     */
    private final Project project;

    /**
     * Job.
     */
    private final Job job;

    /**
     * Issue.
     */
    private final Scalar<Issue> before;

    @Override
    public Issue value() throws Exception {
        final Issue issue = this.before.value();
        final String role = this.job.role();
        final List<Performer> performers = this.project.performers();
        final List<Performer> candidates = performers.stream()
            .filter(
                performer ->
                    new HasRole(role).test(performer)
            ).toList();
        Logger.info(
            this,
            "Found %d candidates for issue %s",
            candidates.size(),
            issue.number()
        );
        new Labeled(() -> issue, new ListOf<>(role)).value();
        if (candidates.isEmpty()) {
            issue.comments().post(
                "@%s, I'm failed to find any eligible candidates for this issue."
                    .formatted(new Architect(performers).name())
            );
        } else {
            final String assignee = candidates.get(
                new Random().nextInt(candidates.size())
            ).name();
            new Issue.Smart(issue).assign(assignee);
            new Commented(
                issue,
                new TextOf(
                    "@%s, take a look, please"
                        .formatted(assignee)
                )
            ).value();
        }
        return issue;
    }
}
