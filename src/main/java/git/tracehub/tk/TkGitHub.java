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

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import git.tracehub.Project;
import git.tracehub.agents.github.Composed;
import git.tracehub.agents.github.GhCommits;
import git.tracehub.agents.github.GhOrder;
import git.tracehub.agents.github.GhProject;
import git.tracehub.agents.github.HookMap;
import git.tracehub.agents.github.RqRepo;
import git.tracehub.agents.github.TraceLogged;
import git.tracehub.agents.github.TraceOnly;
import git.tracehub.facts.ExecOn;
import git.tracehub.validation.Excluded;
import git.tracehub.validation.ProjectValidation;
import git.tracehub.validation.Remote;
import java.util.Collections;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonObject;
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
 * @checkstyle AnonInnerLengthCheck (110 lines)
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
 * @todo #122:30min Extract commits only on push event.
 *  We should implement OnPush.java that will handle that.
 *  Depends on <a href="https://github.com/tracehubpm/tracehub/issues/56">this</a> issue.
 *  Don't forget this remove this puzzle.
 * @todo #51:90min Parse and send incoming requests into distributed queue.
 *  We should parse incoming requests, transform them and send into queue
 *  for further processing. On queue consumer side, it will be handled after
 *  in a FIFO way.
 * @todo #126:45min Implement OnComment.java.
 *  We should implement logic that parses incoming comments
 *  and tries to pattern match them into one of the available categories.
 *  Can be postponed, its not an urgent one. Don't forget to remove
 *  this puzzle.
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

    // @checkstyle MethodBodyCommentsCheck (70 lines)
    @Override
    public Response act(final Request req) throws Exception {
        final JsonObject json = Json.createReader(req.body())
            .readObject();
        final Repo repo = new RqRepo(this.github, json).value();
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
            () -> "`project.yml` contains errors:",
            new Consumer<StringBuilder>() {
                @SneakyThrows
                @Override
                public void accept(final StringBuilder out) {
                    if (json.containsKey("action")) {
                        new HookMap(repo, json).value().get(
                            json.getString("action")
                        ).value();
                    } else {
                        new ExecOn(
                            "GitHub".equals(project.backlog().where()),
                            new GhOrder(
                                new TraceLogged(
                                    new TraceOnly(
                                        new Composed(
                                            new GhCommits(
                                                json
                                            )
                                        )
                                    )
                                ),
                                repo,
                                /**
                                 * Job validation sheets.
                                 * @todo #148:25min Fetch remote sheets for job validation.
                                 *  We should fetch remote sheets for job validation from
                                 *  vsheets repo. For now its depends on migration of this
                                 *  sheets there.
                                 *  See <a href="https://github.com/tracehubpm/tracehub/issues/148">this</a>
                                 *  and <a href="https://github.com/tracehubpm/tracehub/issues/149">this</a>.
                                 *  Don't forget to create tests and remove this puzzle.
                                 */
                                Collections::emptyMap
                            )
                        ).exec(project);
                    }
                    out.append(
                        "Thanks for webhook, %s".formatted(repo.coordinates())
                    );
                }
            }
        ).value();
    }
}
