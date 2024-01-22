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
import lombok.RequiredArgsConstructor;
import org.cactoos.io.InputOf;
import org.cactoos.io.InputWithFallback;

/**
 * Remote sheet, located in tracehubpm/vsheets.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class Remote implements Sheets {

    /**
     * URL.
     */
    private final URL url;

    /**
     * Sheet name.
     */
    private final String sheet;

    /**
     * Ctor.
     * @param tag Repo tag
     * @param sht Sheet name
     * @throws MalformedURLException if URL is malformed
     * @throws URISyntaxException if URL syntax is wrong
     */
    public Remote(final String tag, final String sht)
        throws MalformedURLException, URISyntaxException {
        this(
            new URI(
                "https://raw.githubusercontent.com/tracehubpm/vsheets/%s/xsl/%s.xsl"
                    .formatted(tag, sht)
            ).toURL(),
            sht
        );
    }

    @Override
    public XSL value() throws Exception {
        Logger.debug(
            this, "The sheet '%s' will be pulled from %s...",
            this.sheet, this.url
        );
        return new XSLDocument(
            new InputWithFallback(
                new InputOf(this.url),
                input -> {
                    throw new IOException(
                        "XSL sheet '%s' is not found in %s"
                            .formatted(this.sheet, this.url),
                        input
                    );
                }
            ).stream()
        );
    }
}
