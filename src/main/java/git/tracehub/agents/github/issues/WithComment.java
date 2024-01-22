package git.tracehub.agents.github.issues;

import com.jcabi.github.Issue;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * GitHub Issue with a comment.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class WithComment implements Scalar<Issue> {

    private final Scalar<Issue> before;

    @Override
    public Issue value() throws Exception {
        final Issue issue = this.before.value();
        issue.comments().post(
          ""
        );
        return issue;
    }
}
