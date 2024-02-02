package git.tracehub.agents.github;

import com.jcabi.github.Repo;
import git.tracehub.Project;
import git.tracehub.facts.ExecOn;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class OnPush implements Scalar<Response> {

    private final Repo repo;
    private final Project project;
    private final Request req;
    private final Scalar<StringBuilder> out;

    @Override
    public Response value() throws Exception {
        final Commit commit = new TraceLogged(
            new TraceOnly(new Composed(new GhCommits(this.req)))
        );
        new ExecOn(
            "GitHub".equals(this.project.backlog().where()),
            new GhOrder(commit, this.repo, this.out)
        ).exec(this.project);
        return new RsText(
            "%s commits was processed, thanks %s"
                .formatted(
                    commit,
                    this.repo.coordinates().toString()
                )
        );
    }
}
