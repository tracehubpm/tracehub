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
package git.tracehub.agents.github.issues;

import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import javax.json.Json;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link OnNew}.
 *
 * @since 0.0.0
 */
final class OnNewTest {

    @Test
    void attachesLabelOnNotMineIssue() throws Exception {
        final MkGithub github = new MkGithub("h1alexbel");
        final Repo repo = github.randomRepo();
        repo.issues().create(
            "This is a new issue", "Lets implement the following.."
        );
        final String label = "new";
        final Issue labeled = new OnNew(
            Json.createReader(
                new ResourceOf(
                    "github/hooks/opened/mock-new-issue.json"
                ).stream()
            ).readObject(),
            github.relogin("tracehubgit").repos().get(repo.coordinates()),
            new ListOf<>(label)
        ).value();
        MatcherAssert.assertThat(
            "Issue '%s' does not have label '%s', but should be"
                .formatted(
                    new Issue.Smart(labeled).title(),
                    label
                ),
            new IssueLabels.Smart(labeled.labels())
                .contains(label),
            new IsEqual<>(true)
        );
    }

    @Test
    void doesNotAttachIfMine() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.issues().create("Some mock issue", "Lets to this: ..");
        final String label = "new";
        final Issue labeled = new OnNew(
            Json.createReader(
                new ResourceOf(
                    "github/hooks/opened/mock-new-issue.json"
                ).stream()
            ).readObject(),
            repo,
            new ListOf<>(label)
        ).value();
        MatcherAssert.assertThat(
            "Issue '%s' has label '%s', but should not be"
                .formatted(
                    new Issue.Smart(labeled).title(),
                    label
                ),
            new IssueLabels.Smart(labeled.labels())
                .contains(label),
            new IsEqual<>(false)
        );
    }
}
