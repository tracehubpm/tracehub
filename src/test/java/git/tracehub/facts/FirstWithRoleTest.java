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
package git.tracehub.facts;

import com.amihaiemil.eoyaml.Yaml;
import git.tracehub.agents.github.GhProject;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link FirstWithRole}.
 *
 * @since 0.0.0
 */
final class FirstWithRoleTest {

    @Test
    void findsArc() throws Exception {
        final String expected = "h1alexbel";
        final String name = new FirstWithRole(
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("github/projects/backed.yml").stream()
                ).readYamlMapping()
            ).performers(),
            "ARC"
        ).value().name();
        MatcherAssert.assertThat(
            "Found performer's name %s does not match with expected %s"
                .formatted(name, expected),
            name,
            new IsEqual<>(expected)
        );
    }

    @Test
    void throwsException() {
        final String role = "DEV";
        new Assertion<>(
            "Exception was not thrown, but should be",
            () -> new FirstWithRole(
                new ListOf<>(),
                role
            ).value(),
            new Throws<>(
                "Can not find any performers with %s role".formatted(role),
                IllegalStateException.class
            )
        ).affirm();
    }
}
