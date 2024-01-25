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

import java.net.HttpURLConnection;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.takes.Response;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Errors case fork.
 *
 * @since 0.0.0
 * @todo #66:45min Compose validations together and
 *  format all the problems together. We should compose
 *  all validation errors together after checking project, jobs,
 *  docs and so on.
 */
@RequiredArgsConstructor
public final class ErrorsCase implements Scalar<Response> {

    /**
     * Validation.
     */
    private final Text validation;

    /**
     * Fallback.
     */
    private final Scalar<String> fallback;

    /**
     * When everything is clean.
     */
    private final Consumer<StringBuilder> clean;

    @Override
    public Response value() throws Exception {
        final StringBuilder out = new StringBuilder();
        int status = HttpURLConnection.HTTP_ACCEPTED;
        final String errors = this.validation.asString();
        if (!errors.isEmpty()) {
            status = HttpURLConnection.HTTP_BAD_REQUEST;
            out.append(this.fallback.value());
            out.append('\n');
            out.append(errors);
        }
        if (errors.isEmpty()) {
            this.clean.accept(out);
            status = HttpURLConnection.HTTP_OK;
        }
        return new RsWithStatus(
            new RsJson(
                new RsText(out)
            ),
            status
        );
    }
}
