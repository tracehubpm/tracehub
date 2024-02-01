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
package git.tracehub.agents.github;

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Tree;
import com.jcabi.github.mock.MkGithub;
import git.tracehub.KebabCase;
import git.tracehub.agents.github.issues.YmlIssue;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import javax.json.Json;
import javax.json.JsonObject;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CreateCommit}.
 *
 * @since 0.0.0
 */
final class CreateCommitTest {

    /**
     * Test case for creating commit in a repo.
     *
     * @throws Exception if something went wrong
     * @todo #122:45min Test #createsCommit does not work with MkGithub.
     *  For now we can't test {@link CreateCommit} with {@link MkGithub},
     *  since {@link com.jcabi.github.mock.MkCommit} cannot be identified
     *  with sha we providing in tests.
     */
    @Test
    @Disabled
    void createsCommit() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final JsonObject head = Json.createObjectBuilder()
            .add(
                "tree",
                Json.createObjectBuilder().add("sha", "123").build()
            ).add("sha", "123")
            .build();
        final Issue.Smart from = new Issue.Smart(
            repo.issues().create(
                "Create a test commit", "We need to test our commit creation"
            )
        );
        final JsonObject req = Json.createObjectBuilder()
            .add("base_tree", head.getJsonObject("tree").getString("sha"))
            .add(
                "tree",
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add(
                                "path",
                                ".trace/jobs/%s.yml".formatted(
                                    new KebabCase(from.title()).asString()
                                )
                            )
                            .add("mode", "100644")
                            .add("type", "blob")
                            .add("sha", "678")
                            .add(
                                "content",
                                new YmlIssue(from).value().toString()
                            )
                    ).build()
            ).build();
        final Tree tree = repo.git().trees().create(req);
        final String json = new Jocument(
            new JsonOf(
                new CreateCommit(
                    repo,
                    () -> Json.createObjectBuilder()
                        .add("sha", "678")
                        .add("message", "sync(#%s)".formatted(from.number()))
                        .add("tree", tree.sha())
                        .add(
                            "parents",
                            Json.createArrayBuilder().add(head.getString("sha")).build()
                        ).build()
                ).value().json().toString()
            )
        ).pretty();
        final String expected = new Jocument(
            new JsonOf(
                new ResourceOf("github/commit.json").stream()
            )
        ).pretty();
        MatcherAssert.assertThat(
            "Commit JSON %s does not match with expected %s"
                .formatted(json, expected),
            json,
            new IsEqual<>(expected)
        );
    }
}
