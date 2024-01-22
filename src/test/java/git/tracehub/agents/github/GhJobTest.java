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

import com.amihaiemil.eoyaml.Yaml;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import git.tracehub.Job;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link GhJob}.
 *
 * @since 0.0.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class GhJobTest {

    @Test
    void returnsFormattedJob() throws Exception {
        final String fmt = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/fix-me.yml").stream()
            ).readYamlMapping(),
            new TextOf(
                new ResourceOf(
                    "git/tracehub/agents/github/Issue.md"
                )
            )
        ).asString();
        MatcherAssert.assertThat(
            "Job %s does not match expected format"
                .formatted(fmt),
            fmt,
            // @checkstyle StringLiteralsConcatenationCheck (3 lines)
            new IsEqual<>(
                "Lets update a copyright year in our License to 2024. Its very important task.\n\n"
                + "Estimation here is 20 minutes."
            )
        );
    }

    @Test
    void returnsFormattedJobOnelineDescription() throws Exception {
        final String fmt = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/oneline-description.yml").stream()
            ).readYamlMapping(),
            new TextOf(
                new ResourceOf(
                    "git/tracehub/agents/github/Issue.md"
                )
            )
        ).asString();
        MatcherAssert.assertThat(
            "Job %s does not match expected format".formatted(fmt),
            fmt,
            // @checkstyle StringLiteralsConcatenationCheck (3 lines)
            new IsEqual<>(
                "Lets update a copyright year in our License to 2024\n\n"
                + "Estimation here is 20 minutes."
            )
        );
    }

    /**
     * Test case for invalid YAML input.
     *
     * @todo #22:25min Throw custom validation exception after applying validations.
     *  This puzzle can be resolved probably after GhJob will be empowered with
     *  XSD schema/validation decorator.
     *  Don't forget to remove this puzzle.
     */
    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void throwsOnInvalidYaml() {
        new Assertion<>(
            "Exception is not thrown or invalid",
            () -> new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf("github/jobs/--invalid.yml").stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf(
                        "git/tracehub/agents/github/Issue.md"
                    )
                )
            ).asString(),
            new Throws<>(NullPointerException.class)
        ).affirm();
    }

    @Test
    void returnsFormattedJobAndItsLabel() throws Exception {
        final Job job = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/fix-me.yml").stream()
            ).readYamlMapping(),
            new TextOf(
                new ResourceOf(
                    "git/tracehub/agents/github/Issue.md"
                )
            )
        );
        final String fmt = job.asString();
        MatcherAssert.assertThat(
            "Job %s does not match expected format"
                .formatted(fmt),
            fmt,
            // @checkstyle StringLiteralsConcatenationCheck (3 lines)
            new IsEqual<>(
                "Lets update a copyright year in our License to 2024. Its very important task.\n\n"
                + "Estimation here is 20 minutes."
            )
        );
        final String label = job.label();
        final String expected = "Update License year to 2024";
        MatcherAssert.assertThat(
            "Job label %s does not match expected %s"
                .formatted(label, expected),
            label,
            new IsEqual<>(
                expected
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "all-attributes",
        "no-role",
        "description-escape"
    })
    void transformsJobToXml(final String name) throws Exception {
        final Job job = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf(
                    "github/jobs/%s.yml".formatted(name)
                ).stream()
            ).readYamlMapping(),
            new TextOf(
                new ResourceOf(
                    "git/tracehub/agents/github/Issue.md"
                )
            )
        );
        final XML xml = job.asXml();
        final XML expected = new XMLDocument(
            new ResourceOf("github/jobs/xml/%s.xml".formatted(name)).stream()
        );
        MatcherAssert.assertThat(
            "XML %s for job %s does not match with expected %s"
                .formatted(xml, job.label(), expected),
            xml,
            new IsEqual<>(expected)
        );
    }
}
