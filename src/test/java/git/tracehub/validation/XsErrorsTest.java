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

import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import git.tracehub.extensions.ValidationPipeline;
import git.tracehub.extensions.SheetsIn;
import java.util.List;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link XsErrors}.
 *
 * @since 0.0.0
 */
final class XsErrorsTest {

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "arc",
        "dev"
    })
    @ExtendWith(ValidationPipeline.class)
    void returnsErrors(final Map<String, XSL> pipeline) throws Exception {
        final List<String> errors = new XsErrors(
            new XsApplied(
                new XMLDocument(
                    new ResourceOf(
                        "git/tracehub/validation/no-arc-no-dev-in.xml"
                    ).stream()
                ),
                () -> pipeline
            )
        ).value();
        final List<String> expected = new ListOf<>(
            "Project must have exactly one Architect.",
            "At least one performer must have the DEV role."
        );
        MatcherAssert.assertThat(
            "Errors %s do not match with expected %s"
                .formatted(errors, expected),
            errors,
            new IsEqual<>(expected)
        );
    }

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "arc",
        "dev"
    })
    @ExtendWith(ValidationPipeline.class)
    void returnsNoErrors(final Map<String, XSL> pipeline) throws Exception {
        final List<String> errors = new XsErrors(
            new XsApplied(
                new XMLDocument(
                    new ResourceOf(
                        "git/tracehub/validation/valid-project-in.xml"
                    ).stream()
                ),
                () -> pipeline
            )
        ).value();
        MatcherAssert.assertThat(
            "Errors %s are not empty, but should be"
                .formatted(errors),
            errors.isEmpty(),
            new IsEqual<>(true)
        );
    }
}
