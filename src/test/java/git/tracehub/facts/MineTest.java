package git.tracehub.facts;

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Mine}.
 *
 * @since 0.0.0
 */
final class MineTest {

    @Test
    void checksMine() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue created = repo.issues().create("Some issue", "");
        MatcherAssert.assertThat(
            "Issue %s does not belong to %s, but should be"
                .formatted(
                    new Issue.Smart(created).title(),
                    repo.github().users().self().login()
                ),
            new Mine(created).value(),
            new IsEqual<>(true)
        );
    }

    @Test
    void checksNotMe() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.randomRepo();
        final Issue issue = repo.issues().create("Some issue", "");
        final Issue before = github.relogin("john").repos()
            .get(repo.coordinates())
            .issues()
            .get(issue.number());
        MatcherAssert.assertThat(
            "Issue %s belongs to %s, but should not"
                .formatted(
                    new Issue.Smart(before).title(),
                    github.users().self().login()
                ),
            new Mine(before).value(),
            new IsEqual<>(false)
        );
    }
}
