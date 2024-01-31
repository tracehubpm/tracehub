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

import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import git.tracehub.Performer;
import git.tracehub.Project;
import git.tracehub.extensions.LocalGhProject;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link GhProject}.
 *
 * @since 0.0.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
final class GhProjectTest {

    @Test
    void returnsId() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final String expected = "test";
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content", "id: %s"
                        .formatted(expected)
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
        final String pid = project.identity();
        MatcherAssert.assertThat(
            "project.yml id %s does not match with expected %s"
                .formatted(pid, expected),
            pid,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsDependencies() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/deps.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<String> deps = project.dependencies();
        final List<String> expected = new ListOf<>(
            "github.com/h1alexbel/cdit@master",
            "github.com/h1alexbel/ghquota@master"
        );
        MatcherAssert.assertThat(
            "project.yml dependencies %s do not match with expected %s"
                .formatted(deps, expected),
            deps,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsPerformerName() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-performer.yml",
            new MkGithub().randomRepo()
        ).value();
        final Performer performer = project.performers().get(0);
        final String expected = "h1alexbel";
        MatcherAssert.assertThat(
            "Performer %s does not match with expected %s"
                .formatted(performer.name(), expected),
            performer.name(),
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsPerformerRoles() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-performer.yml",
            new MkGithub().randomRepo()
        ).value();
        final Performer performer = project.performers().get(0);
        final List<String> roles = performer.roles();
        final List<String> expected = new ListOf<>("PO", "ARC");
        MatcherAssert.assertThat(
            "Performer %s roles %s do not match with expected %s"
                .formatted(performer.name(), roles, expected),
            roles,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsRightPerformersSize() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-performer.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<Performer> performers = project.performers();
        final int esize = 1;
        MatcherAssert.assertThat(
            "Performers size %s does not match with expected %s"
                .formatted(performers.size(), esize),
            performers.size(),
            new IsEqual<>(esize)
        );
    }

    @Test
    void returnsRightPerformersSizeWhenMorePerformers() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-more-performers.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<Performer> performers = project.performers();
        final int esize = 2;
        MatcherAssert.assertThat(
            "Performers size %s does not match with expected %s"
                .formatted(performers.size(), esize),
            performers.size(),
            new IsEqual<>(esize)
        );
    }

    @Test
    void returnsRolesWhenMorePerformers() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-more-performers.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<Performer> performers = project.performers();
        final Map<String, List<String>> accum = new MapOf<>();
        performers.forEach(
            performer ->
                accum.put(performer.name(), performer.roles())
        );
        final String first = "h1alexbel";
        MatcherAssert.assertThat(
            "Performer's name %s does not match to expected %s"
                .formatted(accum.get(first), first),
            accum.get(first),
            new IsEqual<>(new ListOf<>("DEV", "ARC"))
        );
        final String second = "hizmailovich";
        MatcherAssert.assertThat(
            "Performer's name %s does not match to expected %s"
                .formatted(accum.get(second), second),
            accum.get(second),
            new IsEqual<>(new ListOf<>("DEV"))
        );
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void throwsOnNoId() throws Exception {
        new Assertion<>(
            "Project does not throw an exception, but should be, id is NULL",
            new LocalGhProject(
                "yml/projects/--no-id.yml",
                new MkGithub().randomRepo()
            ).value()::identity,
            new Throws<>(
                "ID can't be NULL, please fix your YAML file",
                IllegalStateException.class
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void ignoresAbsenceOfDependencies() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/no-dependencies.yml",
            new MkGithub().randomRepo()
        ).value();
        new Assertion<>(
            "Project is not ignores the absence of dependencies, but it should be",
            project::dependencies,
            new IsNot<>(
                new Throws<>(Exception.class)
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void throwsOnNoPerformers() throws Exception {
        new Assertion<>(
            "Project is not ignores the absence of dependencies, but it should be",
            new LocalGhProject(
                "yml/projects/no-dependencies.yml",
                new MkGithub().randomRepo()
            ).value()::dependencies,
            new IsNot<>(
                new Throws<>(Exception.class)
            )
        ).affirm();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "good",
        "full",
        "just-id",
        "name-and-performer",
        "many-performers",
        "backed"
    })
    void transformsProjectsToXml(final String name) throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/%s.yml".formatted(name),
            new MkGithub().randomRepo()
        ).value();
        final XML xml = project.asXml();
        final XML expected = new XMLDocument(
            new ResourceOf("yml/projects/xml/%s.xml".formatted(name)).stream()
        );
        MatcherAssert.assertThat(
            "XML %s for project %s does not match with expected %s"
                .formatted(xml, project.identity(), expected),
            xml,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsBacklog() throws Exception {
        final String expected = "JIRA";
        final Project project = new LocalGhProject(
            "yml/projects/backed.yml",
            new MkGithub().randomRepo()
        ).value();
        final String type = project.backlog().where();
        MatcherAssert.assertThat(
            "Project %s backlog type %s does not match with expected %s"
                .formatted(
                    project.asXml(),
                    type,
                    expected
                ),
            type,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsBacklogWithRules() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-rules.yml",
            new MkGithub().randomRepo()
        ).value();
        final String words = project.backlog().rules().value().get("min-words");
        final String expected = "20";
        MatcherAssert.assertThat(
            "Rule %s does not match with expected %s"
                .formatted(words, expected),
            words,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsSuppressions() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/with-suppressions.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<String> suppressed = project.suppressed();
        final List<String> expected = new ListOf<>("dev", "arc");
        MatcherAssert.assertThat(
            "Suppressed values %s do not match with expected %s"
                .formatted(suppressed, expected),
            suppressed,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsEmptyListIfNoSuppressed() throws Exception {
        final Project project = new LocalGhProject(
            "yml/projects/full.yml",
            new MkGithub().randomRepo()
        ).value();
        final List<String> suppressed = project.suppressed();
        MatcherAssert.assertThat(
            "Suppressed values %s are not empty, but should be"
                .formatted(suppressed),
            suppressed.isEmpty(),
            new IsEqual<>(true)
        );
    }
}
