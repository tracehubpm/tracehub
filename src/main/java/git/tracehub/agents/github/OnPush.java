package git.tracehub.agents.github;

import git.tracehub.Project;
import git.tracehub.facts.Order;
import lombok.RequiredArgsConstructor;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class OnPush implements Order {

    private final Order origin;

    @Override
    public void exec(final Project project) throws Exception {
        this.origin.exec(project);
    }
}
