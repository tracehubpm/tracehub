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

import com.jcabi.github.mock.MkGithub;
import git.tracehub.extensions.LocalGhProject;
import java.util.List;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ExecOn}.
 *
 * @since 0.0.0
 */
final class ExecOnTest {

    @Test
    void executesOnTrue() throws Exception {
        final List<String> ids = new ListOf<>();
        new ExecOn(true, project -> ids.add(project.identity())).exec(
            new LocalGhProject(
                "yml/projects/backed.yml",
                new MkGithub().randomRepo()
            ).value()
        );
        MatcherAssert.assertThat(
            "Order was not executed, but should be",
            ids.isEmpty(),
            new IsEqual<>(false)
        );
    }

    @Test
    void ignoresOnFalse() throws Exception {
        final List<String> ids = new ListOf<>();
        new ExecOn(false, p -> ids.add(p.identity())).exec(
            new LocalGhProject(
                "yml/projects/backed.yml",
                new MkGithub().randomRepo()
            ).value()
        );
        MatcherAssert.assertThat(
            "Order was executed, but should not be",
            ids.isEmpty(),
            new IsEqual<>(true)
        );
    }
}
