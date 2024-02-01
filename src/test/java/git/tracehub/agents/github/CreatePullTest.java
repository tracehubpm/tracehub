package git.tracehub.agents.github;

import com.jcabi.github.Branch;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Reference;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.Tree;
import git.tracehub.KebabCase;
import java.time.Instant;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CreatePull}.
 *
 * @since 0.0.0
 */
final class CreatePullTest {

    @Test
    @Tag("simulation")
    void createsPull() throws Exception {
        final Repo repo = new RtGithub(
            "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"
        ).repos().get(new Coordinates.Simple("h1alexbel/test"));
        final Issue.Smart submitted = new Issue.Smart(repo.issues().get(84));

        final Branch master = repo.branches().find("master");
        final JsonObject json = master.commit().json();
        final String sha = json.getString("sha");
        final String tsha = json.getJsonObject("tree").getString("sha");

        final String formatted = ".trace/jobs/%s.yml".formatted(
            new KebabCase(submitted.title()).asString()
        );
        System.out.println(formatted);
        final Tree tree = repo.git().trees().create(
            Json.createObjectBuilder()
                .add("base_tree", tsha)
                .add(
                    "tree",
                    Json.createArrayBuilder()
                        .add(
                            Json.createObjectBuilder()
                                .add("path", formatted)
                                .add("mode", "100644")
                                .add("type", "blob")
                                .add("content", "")
                                .build()
                        ).build()
                )
                .build()
        );
        final String csha = tree.sha();
        final Commit commit = repo.git().commits().create(
            Json.createObjectBuilder()
                .add("message", "sync(#%s)".formatted(submitted.number()))
                .add("tree", csha)
                .add(
                    "parents", Json.createArrayBuilder()
                        .add(sha)
                        .build()
                )
                .build()
        );
        System.out.println(commit.json());
        final String cmsha = commit.sha();
        Reference reference;
        final long millis = Instant.now().toEpochMilli();
        try {
            repo.git().references().get("refs/heads/sync-%s".formatted(millis)).json();
            reference = repo.git().references().get("refs/heads/sync-%s".formatted(millis));
        } catch (final AssertionError er) {
            reference = repo.git().references().create(
                "refs/heads/sync-%s".formatted(millis),
                cmsha
            );
        }
        repo.pulls().create(
            "sync(#%s)".formatted(submitted.number()),
            reference.ref(),
            "master"
        );
    }
}
