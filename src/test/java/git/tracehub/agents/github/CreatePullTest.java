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

import com.jcabi.github.mock.MkGithub;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link CreatePull}.
 *
 * @since 0.0.0
 */
final class CreatePullTest {

    /**
     * Test case for creating pull request in MkGithub.
     *
     * @throws Exception if something went wrong
     * @todo #122:90min Enable this test when {@link com.jcabi.github.mock.MkBranches}
     *  will implement #find(name) method. For now it can't be tested, since
     *  mock branch implementation does not work as expected, actually
     *  for now it throws the following exception:
     *  java.lang.UnsupportedOperationException: find(name) not implemented.
     */
    @Test
    @Disabled
    void createsPull() throws Exception {
        final String json = new Jocument(
            new JsonOf(
                new CreatePull(
                    new RqFake(
                        "POST",
                        "",
                        new Jocument(
                            new JsonOf(
                                new ResourceOf(
                                    "github/hooks/opened/new-issue.json"
                                ).stream()
                            )
                        ).pretty()
                    ),
                    new MkGithub().randomRepo(),
                    "master"
                ).value().json().toString()
            )
        ).pretty();
        final String expected = new Jocument(
            new JsonOf(new ResourceOf("github/pulls/test-pull.json").stream())
        ).pretty();
        MatcherAssert.assertThat(
            "Pull JSON %s does not match with expected %s"
                .formatted(json, expected),
            json,
            new IsEqual<>(expected)
        );
    }
}
