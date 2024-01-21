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
package git.tracehub.tk;

import com.jcabi.github.mock.MkGithub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkGitHub}.
 *
 * @since 0.0.0
 */
final class TkGitHubTest {

    /**
     * OK test case.
     *
     * @throws Exception if something went wrong
     * @todo #25:60min Introduce unit test for {@link TkGitHub}.
     *  We should create a simple, maintainable unit test.
     *  For now it can't be done, since {@link MkGithub} does not
     *  have configuration from JSON webhook we accepting.
     */
    @Test
    @Disabled
    void returnsOkOnRequest() throws Exception {
        final String expected = "GitHub webhook";
        final String response = new RsPrint(
            new TkGitHub(new MkGithub()).act(new RqFake("GET", "/"))
        ).printBody();
        MatcherAssert.assertThat(
            "Response %s does not match with expected format %s"
                .formatted(response, expected),
            response,
            new IsEqual<>(
                expected
            )
        );
    }
}
