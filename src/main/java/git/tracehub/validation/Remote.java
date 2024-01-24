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
package git.tracehub.validation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.io.InputWithFallback;

/**
 * Remote sheet, located in tracehubpm/vsheets.
 *
 * @since 0.0.0
 * @todo #65:45min Implement CdRemote decorator.
 *  Lets cache remote requests to raw.githubusercontent.com.
 *  To prevent latency and some response failure problems.
 *  Its not urgent, but crucial in general.
 */
@RequiredArgsConstructor
public final class Remote implements Scalar<Map<String, XSL>> {

    /**
     * URL.
     */
    private final URL url;

    /**
     * Sheets to fetch.
     */
    private final Scalar<List<String>> names;

    /**
     * Ctor.
     *
     * @param sht Sheets  to fetch
     * @throws MalformedURLException if URL is malformed
     * @throws URISyntaxException    if URL syntax is wrong
     */
    public Remote(final Scalar<List<String>> sht)
        throws MalformedURLException, URISyntaxException {
        this("master", sht);
    }

    /**
     * Ctor.
     *
     * @param tag Repo tag
     * @param sht Sheets to fetch
     * @throws MalformedURLException if URL is malformed
     * @throws URISyntaxException    if URL syntax is wrong
     */
    public Remote(final String tag, final Scalar<List<String>> sht)
        throws MalformedURLException, URISyntaxException {
        this(
            new URI(
                "https://raw.githubusercontent.com/tracehubpm/vsheets/%s/xsl/"
                    .formatted(tag)
            ).toURL(),
            sht
        );
    }

    // @checkstyle AnonInnerLengthCheck (30 lines)
    @Override
    public Map<String, XSL> value() throws Exception {
        final Map<String, XSL> sheets = new LinkedHashMap<>(16);
        this.names.value().forEach(
            new Consumer<>() {
                @SneakyThrows
                @Override
                public void accept(final String name) {
                    final String named = "%s%s.%s".formatted(
                        Remote.this.url,
                        name,
                        "xsl"
                    );
                    Logger.debug(
                        this, "The sheet '%s' will be pulled from %s...",
                        name, named
                    );
                    sheets.put(
                        name,
                        new XSLDocument(
                            new InputWithFallback(
                                new InputOf(new URI(named)),
                                input -> {
                                    throw new IOException(
                                        "XSL sheet '%s' is not found in %s"
                                            .formatted(name, named),
                                        input
                                    );
                                }
                            ).stream()
                        )
                    );
                }
            });
        return sheets;
    }
}
