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
package git.tracehub;

import com.amihaiemil.eoyaml.Yaml;
import java.io.IOException;
import java.util.List;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Performer.Simple}.
 *
 * @since 0.0.0
 */
final class PerformerTest {

    @Test
    void returnsName() throws IOException {
        final String expected = "h1alexbel";
        final String name = new Performer.Simple(
            Yaml.createYamlInput("name: %s".formatted(expected))
                .readYamlMapping()
        ).name();
        MatcherAssert.assertThat(
            "Performer %s name does not match expected %s"
                .formatted(name, expected),
            name,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsRoles() throws Exception {
        final List<String> expected = new ListOf<>("PO", "ARC");
        final List<String> roles = new Performer.Simple(
            Yaml.createYamlInput(
                new ResourceOf("yml/projects/one-performer.yml").stream()
            ).readYamlMapping()
        ).roles();
        MatcherAssert.assertThat(
            "Performer roles %s does not match expected %s"
                .formatted(roles, expected),
            roles,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsSingleRole() throws Exception {
        final List<String> expected = new ListOf<>("DEV");
        final List<String> roles = new Performer.Simple(
            Yaml.createYamlInput(
                new ResourceOf("yml/projects/single-role.yml").stream()
            ).readYamlMapping()
        ).roles();
        MatcherAssert.assertThat(
            "Performer roles %s does not match expected %s"
                .formatted(roles, expected),
            roles,
            new IsEqual<>(expected)
        );
    }
}
