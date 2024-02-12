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

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.xml.XSL;
import git.tracehub.extensions.LocalGhProject;
import git.tracehub.extensions.MkContribution;
import git.tracehub.extensions.SheetsIn;
import git.tracehub.extensions.ValidationPipeline;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link GhOrder}.
 *
 * @since 0.0.0
 * @checkstyle StringLiteralsConcatenationCheck (30 lines)
 */
final class GhOrderTest {

    @Test
    @ExtendWith(ValidationPipeline.class)
    @SheetsIn(
        {
            "struct",
            "errors",
            "estimate",
            "words"
        }
    )
    void executesOrder(final Map<String, XSL> sheets) throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        new MkContribution(
            repo,
            ".trace/jobs/job1.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description that passes validation\ncost: 25\nrole: DEV"
        ).value();
        new MkContribution(
            repo,
            ".trace/jobs/job2.yml",
            "label: Update License year to 2024\ndescription:"
            + " test description that passes validation\ncost: 25\nrole: ARC"
        ).value();
        new GhOrder(
            new ThJobs(
                new TraceLogged(
                    new TraceOnly(
                        new Composed(
                            new GhCommits(
                                Json.createReader(
                                    new ResourceOf(
                                        "github/hooks/push/created-jobs.json"
                                    ).stream()
                                ).readObject()
                            )
                        )
                    )
                )
            ),
            repo,
            () -> sheets
        ).exec(
            new LocalGhProject(
                "yml/projects/with-rules.yml",
                repo
            ).value()
        );
        final List<Issue> collected = new ListOf<>();
        repo.issues().iterate(new HashMap<>(4)).forEach(collected::add);
        final int expected = 2;
        MatcherAssert.assertThat(
            "Issues size %s does not match with expected %s"
                .formatted(collected.size(), expected),
            collected.size(),
            new IsEqual<>(expected)
        );
    }
}
