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
import com.amihaiemil.eoyaml.YamlInput;
import com.jcabi.github.Repo;
import git.tracehub.Job;

import java.io.IOException;

/**
 * Job in GitHub.
 *
 * @since 0.0.0
 *
 * @todo #10:25min Return total minutes in GhJob#cost().
 *  We should return total minutes instead of String value.
 *  For instance, 20 mins will be equal to 20, and
 *  1h 20 mins will be equal to 60 + 20 => 80 minutes.
 *  In this case we also should rename method to GhJob#minutes().
 * @todo #10:60min YAML Job document validation.
 *  We should validate all incoming job documents.
 *  probably we should have some sort of schema.
 *  Alternatively we can transform YAML into XML
 *  with attached XSD schema to it.
 *  Don't forget to remove this puzzle.
 */
public final class GhJob implements Job {

    /**
     * YAML input.
     */
    private final YamlInput yaml;

    /**
     * Ctor.
     *
     * @param repo repo
     * @param name name
     * @throws Exception if something went wrong
     */
    public GhJob(final Repo repo, final String name) throws Exception {
        this(
            Yaml.createYamlInput(
                new GhContent(
                    repo,
                    ".trace/jobs/%s".formatted(name)
                ).asString()
            )
        );
    }

    /**
     * Ctor.
     *
     * @param yml YAML input
     */
    public GhJob(final YamlInput yml) {
        this.yaml = yml;
    }

    @Override
    public String label() throws IOException {
        return this.yaml.readYamlMapping().string("label");
    }

    @Override
    public String verbose() throws IOException {
        return String.join(
            " ",
            this.yaml.readYamlMapping().literalBlockScalar("description")
        );
    }

    @Override
    public String cost() throws IOException {
        return this.yaml.readYamlMapping().string("cost");
    }
}
