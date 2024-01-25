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

import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.Response;

/**
 * Test case for {@link ErrorsCase}.
 *
 * @since 0.0.0
 */
final class ErrorsCaseTest {

    @Test
    void returnsOkResponseOnEmpty() throws Exception {
        final String expected = "All things clean!";
        final Response response = new ErrorsCase(
            new TextOf(""),
            () -> "Errors occurred!",
            out -> {
                out.append(expected);
            }
        ).value();
        final String body = new TextOf(response.body()).asString();
        MatcherAssert.assertThat(
            "Response body %s does not match with expected %s"
                .formatted(body, expected),
            body,
            new IsEqual<>(expected)
        );
        final Iterable<String> headers = response.head();
        MatcherAssert.assertThat(
            "Headers %s does not contain OK HTTP 1.1 status but should be"
                .formatted(headers),
            new ListOf<>(headers).contains("HTTP/1.1 200 OK"),
            new IsEqual<>(true)
        );
    }

    @Test
    void returnsBadRequestOnValidationErrors() throws Exception {
        final String expected = "Errors occurred:\nBoom!";
        final Response response = new ErrorsCase(
            new TextOf("Boom!"),
            () -> "Errors occurred:",
            out -> {
                out.append(expected);
            }
        ).value();
        final String body = new TextOf(response.body()).asString();
        MatcherAssert.assertThat(
            "Response body %s does not match with expected %s"
                .formatted(body, expected),
            body,
            new IsEqual<>(expected)
        );
        final Iterable<String> headers = response.head();
        MatcherAssert.assertThat(
            "Headers %s does not contain Bad Request HTTP 1.1 status but should be"
                .formatted(headers),
            new ListOf<>(headers).contains("HTTP/1.1 400 Bad Request"),
            new IsEqual<>(true)
        );
    }
}
