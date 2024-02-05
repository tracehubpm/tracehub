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

import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HookAction}.
 *
 * @since 0.0.0
 */
final class HookActionTest {

    @Test
    void returnsPushOnDefault() throws Exception {
        final String action = new HookAction(
            Json.createReader(
                new ResourceOf(
                    "github/hooks/push/mock.json"
                ).stream()
            ).readObject()
        ).asString();
        final String expected = "push";
        MatcherAssert.assertThat(
            "Action name %s does not match with expected %s"
                .formatted(action, expected),
            action,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsActionOnExplicit() throws Exception {
        final String action = new HookAction(
            Json.createReader(
                new ResourceOf(
                    "github/hooks/opened/mock-new-issue.json"
                ).stream()
            ).readObject()
        ).asString();
        final String expected = "opened";
        MatcherAssert.assertThat(
            "Action name %s does not match with expected %s"
                .formatted(action, expected),
            action,
            new IsEqual<>(expected)
        );
    }
}
