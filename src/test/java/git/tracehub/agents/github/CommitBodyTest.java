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
import git.tracehub.extensions.MockedTree;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link CommitBody}.
 *
 * @since 0.0.0
 */
final class CommitBodyTest {

    @Test
    @ExtendWith(MockedTree.class)
    void readsJson(final Tree mock) throws Exception {
        Mockito.mock(Tree.class);
        final Repo repo = new MkGithub().randomRepo();
        final String json = new Jocument(
            new JsonOf(
                new CommitBody(
                    () -> mock,
                    Json.createObjectBuilder()
                        .add("sha", "123")
                        .build(),
                    new Issue.Smart(
                        repo.issues().create(
                            "Test CommitBody.java",
                            "we need to test CommitBody.java"
                        )
                    )
                ).value().toString()
            )
        ).pretty();
        final String expected = new Jocument(
            new JsonOf(new ResourceOf("github/requests/commit.json").stream())
        ).pretty();
        MatcherAssert.assertThat(
            "JSON %s does not match with expected %s"
                .formatted(json, expected),
            json,
            new IsEqual<>(expected)
        );
    }
}
