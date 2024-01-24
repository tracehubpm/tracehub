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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.util.LinkedHashMap;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link XsApplied}.
 *
 * @since 0.0.0
 */
final class XsAppliedTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "valid-project",
        "no-arc",
        "no-arc-no-dev"
    })
    void appliesXsls(final String name) throws Exception {
        final Map<String, XSL> pipeline = new LinkedHashMap<>(4);
        pipeline.put(
            "struct.xsl",
            new XSLDocument(
                new ResourceOf(
                    "git/tracehub/validation/struct.xsl"
                ).stream()
            )
        );
        pipeline.put(
            "errors.xsl",
            new XSLDocument(
                new ResourceOf(
                    "git/tracehub/validation/errors.xsl"
                ).stream()
            )
        );
        pipeline.put(
            "arc.xsl",
            new XSLDocument(
                new ResourceOf(
                    "git/tracehub/validation/arc.xsl"
                ).stream()
            )
        );
        pipeline.put(
            "dev.xsl",
            new XSLDocument(
                new ResourceOf(
                    "git/tracehub/validation/dev.xsl"
                ).stream()
            )
        );
        final XML out = new XsApplied(
            new XMLDocument(
                new ResourceOf(
                    "git/tracehub/validation/%s-in.xml"
                        .formatted(name)
                ).stream()
            ),
            () -> pipeline
        ).value();
        final XML expected = new XMLDocument(
            new ResourceOf(
                "git/tracehub/validation/%s-out.xml"
                    .formatted(name)
            ).stream()
        );
        MatcherAssert.assertThat(
            "Output XML %s does not match with expected %s"
                .formatted(out, expected),
            out,
            new IsEqual<>(expected)
        );
    }
}
