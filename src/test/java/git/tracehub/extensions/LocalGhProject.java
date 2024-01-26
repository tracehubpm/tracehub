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
package git.tracehub.extensions;

import com.jcabi.github.Repo;
import git.tracehub.Project;
import git.tracehub.agents.github.GhProject;
import javax.json.Json;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;

/**
 * Local GitHub project.
 *
 * @since 0.0.0
 * @todo #46:25min Make a JUnit extension out of LocalProject.
 *  Lets make a JUnit extension with local GitHub project.yml setup.
 *  Don't forget to remove this puzzle.
 */
@SuppressWarnings(
    {"JTCOP.RuleCorrectTestName", "JTCOP.RuleAllTestsHaveProductionClass"}
)
@RequiredArgsConstructor
public final class LocalGhProject implements Scalar<Project> {

    /**
     * Path.
     */
    private final String path;

    /**
     * Repo.
     */
    private final Repo repo;

    @Override
    public Project value() throws Exception {
        this.repo.contents().create(
            Json.createObjectBuilder()
                .add("path", ".trace/project.yml")
                .add(
                    "content",
                    new TextOf(
                        new ResourceOf(this.path)
                    ).asString()
                )
                .add("message", "project created")
                .build()
        );
        return new GhProject(this.repo);
    }
}
