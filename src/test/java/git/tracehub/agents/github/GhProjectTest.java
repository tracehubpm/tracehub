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
import java.util.List;
import java.util.Map;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.cactoos.text.TextOf;
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
        final Project project = this.provide(
            "github/projects/deps.yml"
        );
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
        final Project project = this.provide(
            "github/projects/with-performer.yml"
        );
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
        final Project project = this.provide(
            "github/projects/with-performer.yml"
        );
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
        final Project project = this.provide(
            "github/projects/with-performer.yml"
        );
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
        final Project project = this.provide(
            "github/projects/with-more-performers.yml"
        );
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
        final Project project = this.provide(
            "github/projects/with-more-performers.yml"
        );
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
            this.provide("github/projects/--no-id.yml")::identity,
            new Throws<>(
                "ID can't be NULL, please fix your YAML file",
                IllegalStateException.class
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void ignoresAbsenceOfDependencies() throws Exception {
        final Project project = this.provide(
            "github/projects/no-dependencies.yml"
        );
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
            this.provide("github/projects/no-dependencies.yml")::dependencies,
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
        "many-performers"
    })
    void transformsProjectsToXml(final String name) throws Exception {
        final Project project = this.provide(
            "github/projects/%s.yml".formatted(name)
        );
        final XML xml = project.asXml();
        final XML expected = new XMLDocument(
            new ResourceOf("github/projects/xml/%s.xml".formatted(name)).stream()
        );
        MatcherAssert.assertThat(
            "XML %s for project %s does not match with expected %s"
                .formatted(xml, project.identity(), expected),
            xml,
            new IsEqual<>(expected)
        );
    }

    /**
     * Provide a project from path.
     * @param path Path to YAML document
     * @return Project
     * @throws Exception if something went wrong
     * @todo #38:25min Resolve code duplication with Project providing.
     *  We should resolve a code duplication for a project providing
     *  in tests. Possible some JUnit extension/Argument provider
     *  that will provide us a projects by list of path we feed to it.
     *  Don't forget to remove this puzzle.
     */
    private Project provide(final String path) throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(path)
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        return new GhProject(repo);
    }
}
