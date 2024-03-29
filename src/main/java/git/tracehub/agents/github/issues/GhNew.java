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
import com.jcabi.xml.XSL;
import git.tracehub.Job;
import git.tracehub.Project;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.GhJob;
import git.tracehub.validation.JobValidation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;

/**
 * New GitHub issues.
 *
 * @since 0.0.0
 * @todo #28:60min Submit created tickets to a PMO.
 *  We should submit it directly to a PMO on each issue we created.
 *  Probably an input will be an issue number, repo, and full file path.
 *  For now can be postponed. We should prepare PMO for that firstly.
 *  Don't forget to remove this puzzle.
 * @todo #28:60min Implement GhClose.java.
 *  We should implement a logic for closing a GitHub issue
 *  for each deleted file in ThJobs#deleted().
 *  As described above we need to reach out to PMO.
 *  PMO will provide us the issue number by the full path of deleted job.
 *  We should get the issue and close it in GitHub.
 *  Don't forget to remove this puzzle.
 * @todo #28:60min Implement GhUpdate.java.
 *  We should implement a logic for updating for each updated files in ThJobs#updated().
 *  We provide a full path of updated job to PMO, PMO gets us an issue number.
 *  From there, probably we should post a comment to that issue that things gets
 *  an update.
 * @todo #64:45min Post found job validation errors somewhere.
 *  We should post found errors in job by validation pipeline.
 *  Ideally the destination should be GitHub commit, or maybe pull request,
 *  where it was discovered.
 */
@RequiredArgsConstructor
public final class GhNew implements Scalar<List<Issue>> {

    /**
     * Project.
     */
    private final Project project;

    /**
     * Commit.
     */
    private final Commit commit;

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * Sheets.
     */
    private final Scalar<Map<String, XSL>> sheets;

    // @checkstyle AnonInnerLengthCheck (20 lines)
    @Override
    public List<Issue> value() throws Exception {
        return this.commit.created().stream()
            .map(
                new Function<String, Job>() {
                    @SneakyThrows
                    @Override
                    public Job apply(final String name) {
                        return new GhJob(
                            GhNew.this.repo,
                            name,
                            new TextOf(
                                new ResourceOf(
                                    "git/tracehub/agents/github/Issue.md"
                                )
                            )
                        );
                    }
                })
            .filter(
                new Predicate<Job>() {
                    @SneakyThrows
                    @Override
                    public boolean test(final Job job) {
                        return new JobValidation(
                            job,
                            GhNew.this.project,
                            GhNew.this.sheets
                        ).asString().isEmpty();
                    }
                })
            .map(
                new Function<Job, Issue>() {
                    @SneakyThrows
                    @Override
                    public Issue apply(final Job job) {
                        return new Labeled(
                            new AssignOnIssue(
                                GhNew.this.project,
                                job,
                                new CreateIssue(
                                    job,
                                    GhNew.this.repo.issues()
                                )
                            ),
                            new ListOf<>("synced")
                        ).value();
                    }
                }
            ).toList();
    }
}
