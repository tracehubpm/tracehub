package git.tracehub.agents.github;

import com.jcabi.github.Branch;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import com.jcabi.github.Reference;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.github.Tree;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.cactoos.io.ResourceOf;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ScopedMock;

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
        // words... -> word-word2-word3

        final Branch master = repo.branches().find("master");
        final JsonObject json = master.commit().json();
        final String sha = json.getString("sha");
        final String tsha = json.getJsonObject("tree").getString("sha");

        final Tree tree = repo.git().trees().create(
            Json.createObjectBuilder()
                .add("base_tree", tsha)
                .add(
                    "tree",
                    Json.createArrayBuilder()
                        .add(
                            Json.createObjectBuilder()
                                .add(
                                    "path",
                                    ".trace/jobs/%s.yml".formatted(submitted.title())
                                )
                                .add("mode", "100644")
                                .add("type", "blob")
                                .add("content", "")
                                .build()
                        )
                        .build()
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
//                .add("branch", "test")
                .build()
        );
        System.out.println(commit.json());
        final String cmsha = commit.sha();
        Reference reference;
        if (repo.git().references().get("sync-3") == null) {
            reference = repo.git().references().create(
                "refs/heads/sync-3",
                cmsha
            );
        } else {
            reference = repo.git().references().get("sync-3");
        }
        repo.pulls().create(
            "sync",
            reference.ref(),
            "master"
        );
//        System.out.println(reference.ref());

//        final Reference reference = repo.git().references().create("sync-", tsha);


//        repo.contents().create(
//            Json.createObjectBuilder()
//                .add("path", ".trace/jobs/%s.yml".formatted(submitted.title()))
//                .add("message", "sync(#%s)".formatted(submitted.number()))
//                .add("content", "")
//                .build()
//        ).path();
    }
}
