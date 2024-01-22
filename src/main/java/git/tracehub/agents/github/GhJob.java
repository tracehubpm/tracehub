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
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import git.tracehub.Job;
import git.tracehub.validation.DocTransformed;
import org.cactoos.Text;

/**
 * Job in GitHub.
 *
 * @since 0.0.0
 */
public final class GhJob implements Job {

    /**
     * YAML input.
     */
    private final YamlMapping yaml;

    /**
     * Job template.
     */
    private final Text template;

    /**
     * Ctor.
     *
     * @param repo Repo
     * @param name Name
     * @param tmplt Job template
     * @throws Exception if something went wrong
     */
    public GhJob(
        final Repo repo,
        final String name,
        final Text tmplt
    )
        throws Exception {
        this(
            Yaml.createYamlInput(
                new GhContent(
                    repo,
                    name
                ).asString()
            ).readYamlMapping(),
            tmplt
        );
    }

    /**
     * Ctor.
     *
     * @param yml YAML input
     * @param tmplt Job template
     */
    public GhJob(final YamlMapping yml, final Text tmplt) {
        this.yaml = yml;
        this.template = tmplt;
    }

    @Override
    public String label() {
        return this.yaml.string("label");
    }

    @Override
    public String role() {
        String role = this.yaml.string("role");
        if (role == null) {
            role = "DEV";
        }
        return role;
    }

    @Override
    public String asString() throws Exception {
        return this.template.asString()
            .formatted(
                String.join(
                    " ",
                    this.yaml.literalBlockScalar("description")
                ),
                this.yaml.string("cost")
            );
    }

    @Override
    public XML asXml() throws Exception {
        return new DocTransformed(this.yaml).value();
    }
}
