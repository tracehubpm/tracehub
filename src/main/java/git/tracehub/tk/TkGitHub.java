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
package git.tracehub.tk;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import git.tracehub.Project;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.Composed;
import git.tracehub.agents.github.GhCommits;
import git.tracehub.agents.github.GhOrder;
import git.tracehub.agents.github.GhProject;
import git.tracehub.agents.github.TraceLogged;
import git.tracehub.agents.github.TraceOnly;
import git.tracehub.facts.ExecOn;
import git.tracehub.validation.Excluded;
import git.tracehub.validation.ProjectValidation;
import git.tracehub.validation.Remote;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * GitHub Take.
 *
 * @since 0.0.0
 * @todo #25:45min Return the result of webhook.
 *  Instead of thanks for webhook, I believe we should
 *  return a result of sent webhook. A number of created issues,
 *  closed, and other info. Don't forget to create an integration
 *  test and remove this puzzle.
 * @todo #25:90min Implement branching after receiving commits.
 *  As we discussed <a href="https://github.com/tracehubpm/tracehub/issues/32">here</a>
 *  and <a href="https://github.com/tracehubpm/tracehub/issues/33">here</a>,
 *  we should branch our TraceLogged commits into ThJobs,
 *  ThJobs into created, updated and deleted. We should make it as much generic
 *  as possible, since we aim to process all kinds of GitHub webhooks using Takes.
 * @todo #15:45min Create a comment on the head commit with errors.
 *  Instead of sending errors as webhook result, we should create a comment
 *  on a head commit from hook we got.
 * @todo #96:45min Resolve code complexity with appending responses to StringBuilder.
 *  We should remove that complexity required to append strings to StringBuilder
 *  we pass between objects. Lets make it more simple.
 *  Don't forget to remove this puzzle.
 * @todo #96:60min Adopt a support for multiple webhook types in TkGitHub.
 *  We should adopt TkGitHub to handle multiple webhook types.
 *  For now lets start with push event (currently supported and processed),
 *  issue_comment_created, issue_created.
 * @todo #118:90min Implement OnNew.java.
 *  We should parse on opened issue event that comes from GitHub as webhook,
 *  check if the author of opened issue is not tracehubgit and add
 *  label `new` to opened issue.
 * @todo #122:90min Implement OnAttachedLabel.java.
 *  We should parse on attached label event that comes to us
 *  from GitHub webhook. If label was `bug` that we should
 *  launch {@link git.tracehub.agents.github.CreatePull} or other related integration.
 * @todo #122:30min Extract commits only on push event.
 *  We should implement OnPush.java that will handle that.
 *  Depends on <a href="https://github.com/tracehubpm/tracehub/issues/56">this</a> issue.
 *  Don't forget this remove this puzzle.
 * @todo #51:90min Parse and send incoming requests into distributed queue.
 *  We should parse incoming requests, transform them and send into queue
 *  for further processing. On queue consumer side, it will be handled after
 *  in a FIFO way.
 */
@RequiredArgsConstructor
public final class TkGitHub implements Take {

    /**
     * GitHub.
     */
    private final Github github;

    /**
     * Vsheets repo tag.
     */
    private final String tag;

    @Override
    public Response act(final Request req) throws Exception {
        final Commit commit = new TraceLogged(
            new TraceOnly(new Composed(new GhCommits(req)))
        );
        final Repo repo = this.github.repos().get(
            new Coordinates.Simple(commit.repo())
        );
        final Project project = new GhProject(repo);
        return new ErrorsCase(
            new ProjectValidation(
                project,
                new Remote(
                    this.tag,
                    new Excluded(
                        new ListOf<>(
                            "struct",
                            "errors",
                            "project/arc",
                            "project/dev"
                        ),
                        project.suppressed()
                    )
                )
            ),
            () -> "`project.yml` document contains errors:",
            new Consumer<StringBuilder>() {
                @SneakyThrows
                @Override
                public void accept(final StringBuilder out) {
                    new ExecOn(
                        "GitHub".equals(project.backlog().where()),
                        new GhOrder(commit, repo, () -> out)
                    ).exec(project);
                }
            }
        ).value();
    }
}
