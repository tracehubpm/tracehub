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
import com.jcabi.xml.XSL;
import git.tracehub.Project;
import git.tracehub.agents.github.Commit;
import git.tracehub.agents.github.Composed;
import git.tracehub.agents.github.GhCommits;
import git.tracehub.agents.github.GhProject;
import git.tracehub.agents.github.TraceLogged;
import git.tracehub.agents.github.TraceOnly;
import git.tracehub.agents.github.issues.GhNew;
import git.tracehub.validation.Remote;
import git.tracehub.validation.XsApplied;
import git.tracehub.validation.XsErrors;
import java.net.HttpURLConnection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

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
 * @todo #15:90min Fetch all XSL sheets from vsheets repo.
 *  We should fetch all XSL sheets into list with possibility to
 *  exclude some of them. Don't forget to remove this puzzle.
 * @todo #15:30min Clean up procedural code.
 *  We should clean up the code inside TkGitHub.
 *  Validation with XeErrors must be handled outside of this Take.
 *  The same with response construction.
 * @todo #15:45min Create a comment on the head commit with errors.
 *  Instead of sending errors as webhook result, we should create a comment
 *  on a head commit from hook we got.
 */
@RequiredArgsConstructor
@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public final class TkGitHub implements Take {

    /**
     * GitHub.
     */
    private final Github github;

    @Override
    public Response act(final Request req) throws Exception {
        final StringBuilder response = new StringBuilder();
        int status = HttpURLConnection.HTTP_ACCEPTED;
        final Commit commit = new TraceLogged(
            new TraceOnly(
                new Composed(
                    new GhCommits(
                        req
                    )
                )
            )
        );
        final Repo repo = this.github.repos().get(
            new Coordinates.Simple(commit.repo())
        );
        final Project project = new GhProject(repo);
        final List<String> err = new XsErrors(
            new XsApplied(
                project.asXml(),
                () -> {
                    final XSL struct = new Remote("master", "struct").value();
                    final XSL errors = new Remote("master", "errors").value();
                    final XSL arc = new Remote("master", "project/arc").value();
                    final XSL dev = new Remote("master", "project/dev").value();
                    return new ListOf<>(
                        struct,
                        errors,
                        arc,
                        dev
                    );
                }
            )
        ).value();
        if (!err.isEmpty()) {
            response.append("Project contains some errors:");
            err.forEach(
                e -> {
                    response.append(e);
                    response.append('\n');
                });
            status = HttpURLConnection.HTTP_BAD_REQUEST;
        }
        if (err.isEmpty()) {
            new GhNew(
                project,
                commit,
                repo
            ).value();
            response.append(
                "Thanks %s for GitHub webhook".formatted(
                    new Repo.Smart(repo).coordinates()
                )
            );
            status = HttpURLConnection.HTTP_OK;
        }
        return new RsWithStatus(
            new RsJson(
                new RsText(response)
            ),
            status
        );
    }
}
