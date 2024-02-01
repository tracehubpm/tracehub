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
import com.jcabi.github.mock.MkGithub;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import javax.json.Json;
import javax.json.JsonObject;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TreeRequest}.
 *
 * @since 0.0.0
 */
final class TreeRequestTest {

    @Test
    void readsJson() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue created = repo.issues().create(
            "Tree request test", "TreeRequest.java needs to be tested"
        );
        final JsonObject json = new TreeRequest(
            Json.createObjectBuilder()
                .add("tree", Json.createObjectBuilder().add("sha", "123"))
                .build(),
            new Issue.Smart(created)
        ).value();
        final String expected = new Jocument(
            new JsonOf(
                new ResourceOf("github/requests/tree.json").stream()
            )
        ).pretty();
        final String pretty = new Jocument(new JsonOf(json.toString())).pretty();
        MatcherAssert.assertThat(
            "JSON %s does not match with expected %s"
                .formatted(pretty, expected),
            pretty,
            new IsEqual<>(expected)
        );
    }
}
