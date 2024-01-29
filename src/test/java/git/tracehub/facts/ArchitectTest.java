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
import git.tracehub.Performer;
import git.tracehub.agents.github.GhProject;
import java.util.List;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Architect}.
 *
 * @since 0.0.0
 */
final class ArchitectTest {

    @Test
    void findsArchitectInCrew() throws Exception {
        final Performer architect = new Architect(
            new GhProject(
                Yaml.createYamlInput(
                    new ResourceOf("github/projects/backed.yml")
                        .stream()
                ).readYamlMapping()
            ).performers()
        );
        final String name = architect.name();
        final String nexpected = "h1alexbel";
        MatcherAssert.assertThat(
            "Architect's name %s does not match with expected %s"
                .formatted(name, nexpected),
            name,
            new IsEqual<>(nexpected)
        );
        final List<String> roles = architect.roles();
        final List<String> rexpected = new ListOf<>(
            "DEV",
            "ARC"
        );
        MatcherAssert.assertThat(
            "Architect's roles %s does not match with expected %s"
                .formatted(roles, rexpected),
            roles,
            new IsEqual<>(rexpected)
        );
    }
}
