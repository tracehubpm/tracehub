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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLChain;
import com.jcabi.xml.XSLDocument;
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/deps.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/with-performer.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/with-performer.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/with-performer.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/with-more-performers.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/with-more-performers.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/--no-id.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
        new Assertion<>(
            "Project does not throw an exception, but should be, id is NULL",
            project::identity,
            new Throws<>(
                "ID can't be NULL, please fix your YAML file",
                IllegalStateException.class
            )
        ).affirm();
    }

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void ignoresAbsenceOfDependencies() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/no-dependencies.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
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
        final Repo repo = new MkGithub().randomRepo();
        repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(
                            "github/projects/no-dependencies.yml"
                        )
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        final Project project = new GhProject(repo);
        new Assertion<>(
            "Project is not ignores the absence of dependencies, but it should be",
            project::dependencies,
            new IsNot<>(
                new Throws<>(Exception.class)
            )
        ).affirm();
    }

    /**
     * Runs a PoC that transforms YAML document and validates it.
     *
     * @throws Exception if something went wrong
     * @todo #11:45min Extract this PoC to a validating objects.
     *  We should extract this proof of concept, where we are
     *  getting YAML document, converting it into XML, applying XSL sheets,
     *  and checking the result of validation.
     */
    @Test
    void readsArcDevErrors() throws Exception {
        final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
        final Map<String, Object> map = yaml.readValue(
            new ResourceOf("github/projects/--to-xml.yml").stream(),
            new TypeReference<>() {
            }
        );
        final XML transform =
            new XSLChain(
                new XSLDocument(
                    new ResourceOf("github/validation/src.xsl").stream()
                ),
                new XSLDocument(
                    new ResourceOf("github/validation/errors.xsl").stream()
                ),
                new XSLDocument(
                    new ResourceOf("github/validation/arc.xsl").stream()
                ),
                new XSLDocument(
                    new ResourceOf("github/validation/dev.xsl").stream()
                )
            ).transform(
                new XMLDocument(
                    new XmlMapper().writeValueAsString(map)
                )
            );
        final List<String> errors = transform.xpath("//errors/error/text()");
        final List<String> expected = new ListOf<>(
            "Only one performer can be an Architect in the team.",
            "At least one performer should have the DEV role."
        );
        MatcherAssert.assertThat(
            "Errors %s do not match with expected %s"
                .formatted(errors, expected),
            errors,
            new IsEqual<>(expected)
        );
    }
}
