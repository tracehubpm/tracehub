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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.xml.XSL;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.ValidationPipeline;
import git.tracehub.extensions.SheetsIn;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link ProjectValidation}.
 *
 * @since 0.0.0
 */
final class ProjectValidationTest {

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "arc",
        "dev"
    })
    @ExtendWith(ValidationPipeline.class)
    void validatesCleanProject(final Map<String, XSL> sheets) throws Exception {
        final String message = new ProjectValidation(
            new LocalGhProject(
                "yml/projects/good.yml",
                new MkGithub().randomRepo()
            ).value(),
            () -> sheets
        ).asString();
        MatcherAssert.assertThat(
            "Error message %s is not empty, but should be"
                .formatted(message),
            message.isEmpty(),
            new IsEqual<>(true)
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
    void foundsErrors(final Map<String, XSL> sheets) throws Exception {
        final String message = new ProjectValidation(
            new LocalGhProject(
                "yml/projects/no-arc.yml",
                new MkGithub().randomRepo()
            ).value(),
            () -> sheets
        ).asString();
        final String expected = "Project must have exactly one Architect.";
        MatcherAssert.assertThat(
            "Error message %s does not match with expected %s"
                .formatted(message, expected),
            message,
            new IsEqual<>(expected)
        );
    }
}
