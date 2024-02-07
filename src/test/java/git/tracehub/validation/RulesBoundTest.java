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
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import git.tracehub.agents.github.GhJob;
import git.tracehub.agents.github.GhProject;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test case for {@link RulesBound}.
 *
 * @since 0.0.0
 */
final class RulesBoundTest {

    @ValueSource(strings = {
        "for-arc",
        "no-role",
        "description-escape"
    })
    @ParameterizedTest
    void returnsXmlWithRules(final String name) throws Exception {
        final XML out = new RulesBound(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf(
                        "yml/jobs/%s.yml"
                            .formatted(name)
                    ).stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf(
                        "git/tracehub/agents/github/Issue.md"
                    )
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/with-suppressions.yml").stream()
                ).readYamlMapping()
            )
        ).value();
        final XML expected = new XMLDocument(
            new ResourceOf(
                "git/tracehub/validation/out/%s.xml".formatted(name)
            ).stream()
        );
        MatcherAssert.assertThat(
            "Generated XML '%s' does not match with expected '%s'"
                .formatted(out, expected),
            out,
            new IsEqual<>(expected)
        );
    }

    /**
     * Test case for default rule binding into XML.
     *
     * @param name Input file name
     * @throws Exception if something went wrong
     * @todo #113:25min Check the default values for applied rules.
     *  We should check that rules was bound to defaults, check
     *  <a href="https://github.com/tracehubpm/tracehub/issues/116">this</a> issue before.
     *  Don't forget to remove this puzzle.
     */
    @Disabled
    @ValueSource(strings = {
        "for-arc",
        "no-role",
        "description-escape"
    })
    @ParameterizedTest
    void returnsXmlWithRulesOnNoRuleProject(final String name) throws Exception {
        final XML out = new RulesBound(
            new GhJob(
                Yaml.createYamlInput(
                    new ResourceOf(
                        "yml/jobs/%s.yml"
                            .formatted(name)
                    ).stream()
                ).readYamlMapping(),
                new TextOf(
                    new ResourceOf(
                        "git/tracehub/agents/github/Issue.md"
                    )
                )
            ),
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("yml/projects/good.yml").stream()
                ).readYamlMapping()
            )
        ).value();
        final XML expected = new XMLDocument(
            new ResourceOf(
                "git/tracehub/validation/out/%s.xml".formatted(name)
            ).stream()
        );
        MatcherAssert.assertThat(
            "Generated XML '%s' does not match with expected '%s'"
                .formatted(out, expected),
            out,
            new IsEqual<>(expected)
        );
    }
}
