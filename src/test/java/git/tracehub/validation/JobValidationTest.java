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

import com.amihaiemil.eoyaml.Yaml;
import com.jcabi.xml.XSL;
import git.tracehub.agents.github.GhJob;
import git.tracehub.agents.github.GhProject;
import git.tracehub.extensions.ValidationPipeline;
import git.tracehub.extensions.SheetsIn;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link JobValidation}.
 *
 * @since 0.0.0
 * @todo #133:30min Validate job using `min-words` rules.
 *  We should validate input job using one more rule `min-words`.
 *  Respectively, we need unit tests for this logic.
 *  Don't forget to remove this puzzle.
 */
final class JobValidationTest {

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "estimate"
    })
    @ExtendWith(ValidationPipeline.class)
    void appendsErrorOnUnderestimate(final Map<String, XSL> sheets) throws Exception {
        final String result = new JobValidation(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf("git/tracehub/validation/under-estimate.yml")
                        .stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf("git/tracehub/agents/github/Issue.md")
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/with-suppressions.yml").stream()
                ).readYamlMapping()
            ),
            () -> sheets
        ).asString();
        final String expected = "Specified estimate (20) is not in allowed range: 25"
                                + " (`issues:backlog:rules:min-estimate`) and 90"
                                + " (`issues:backlog:rules:max-estimate)`.";
        MatcherAssert.assertThat(
            "Job validation result '%s' does not match with expected %s"
                .formatted(result, expected),
            result,
            new IsEqual<>(expected)
        );
    }

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "estimate"
    })
    @ExtendWith(ValidationPipeline.class)
    void appendsErrorOnOverestimate(final Map<String, XSL> sheets)
        throws Exception {
        final String result = new JobValidation(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf("git/tracehub/validation/over-estimate.yml")
                        .stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf("git/tracehub/agents/github/Issue.md")
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/with-suppressions.yml").stream()
                ).readYamlMapping()
            ),
            () -> sheets
        ).asString();
        final String expected = "Specified estimate (100) is not in allowed range: 25"
                                + " (`issues:backlog:rules:min-estimate`) and 90"
                                + " (`issues:backlog:rules:max-estimate)`.";
        MatcherAssert.assertThat(
            "Job validation result '%s' does not match with expected %s"
                .formatted(result, expected),
            result,
            new IsEqual<>(expected)
        );
    }

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "estimate"
    })
    @ExtendWith(ValidationPipeline.class)
    void validatesClean(final Map<String, XSL> sheets) throws Exception {
        final String result = new JobValidation(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf("git/tracehub/validation/clean-job.yml")
                        .stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf("git/tracehub/agents/github/Issue.md")
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/with-suppressions.yml").stream()
                ).readYamlMapping()
            ),
            () -> sheets
        ).asString();
        MatcherAssert.assertThat(
            "Job validation result '%s' contains errors, but should not"
                .formatted(result),
            result.isEmpty(),
            new IsEqual<>(true)
        );
    }

    @Test
    @SheetsIn({
        "struct",
        "errors",
        "estimate"
    })
    @ExtendWith(ValidationPipeline.class)
    void validatesCleanOnTopEstimate(final Map<String, XSL> sheets) throws Exception {
        final String result = new JobValidation(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf("git/tracehub/validation/top.yml")
                        .stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf("git/tracehub/agents/github/Issue.md")
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/with-suppressions.yml").stream()
                ).readYamlMapping()
            ),
            () -> sheets
        ).asString();
        MatcherAssert.assertThat(
            "Job validation result '%s' contains errors, but should not"
                .formatted(result),
            result.isEmpty(),
            new IsEqual<>(true)
        );
    }
}
