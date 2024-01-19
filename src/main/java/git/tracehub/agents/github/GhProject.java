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
import com.amihaiemil.eoyaml.YamlMapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import git.tracehub.Performer;
import git.tracehub.Project;
import java.util.List;
import org.cactoos.list.ListOf;

/**
 * Project in GitHub.
 *
 * @since 0.0.0
 */
public final class GhProject implements Project {

    /**
     * YAML.
     */
    private final YamlMapping yaml;

    /**
     * Ctor.
     *
     * @param repo Repo
     * @throws Exception if something went wrong
     */
    public GhProject(final Repo repo) throws Exception {
        this(
            Yaml.createYamlInput(
                new GhContent(repo, ".trace/project.yml").asString()
            ).readYamlMapping()
        );
    }

    /**
     * Ctor.
     *
     * @param yml YAML
     */
    public GhProject(final YamlMapping yml) {
        this.yaml = yml;
    }

    @Override
    public String identity() {
        final String id = this.yaml.string("id");
        if (id == null) {
            throw new IllegalStateException(
                "ID can't be NULL, please fix your YAML file"
            );
        }
        return id;
    }

    @Override
    public List<Performer> performers() {
        final List<Performer> performers = new ListOf<>();
        this.yaml.yamlSequence("performers")
            .forEach(node -> performers.add(new Performer.Simple(node)));
        return performers;
    }

    @Override
    public List<String> dependencies() {
        final List<String> found = new ListOf<>();
        if (this.yaml.yamlSequence("dependencies") != null) {
            this.yaml.yamlSequence("dependencies")
                .forEach(node -> found.add(node.asScalar().value()));
        }
        return found;
    }

    @Override
    public XML asXml() throws Exception {
        return new XMLDocument(
            new XmlMapper().writeValueAsString(
                new ObjectMapper(new YAMLFactory()).readValue(
                    this.yaml.toString(),
                    new TypeReference<>() {
                    }
                )
            )
        );
    }
}
