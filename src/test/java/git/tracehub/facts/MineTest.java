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

import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Mine}.
 *
 * @since 0.0.0
 */
final class MineTest {

    @Test
    void checksMine() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Issue created = repo.issues().create("Some issue", "");
        MatcherAssert.assertThat(
            "Issue %s does not belong to %s, but should be"
                .formatted(
                    new Issue.Smart(created).title(),
                    repo.github().users().self().login()
                ),
            new Mine(created).value(),
            new IsEqual<>(true)
        );
    }

    @Test
    void checksNotMe() throws Exception {
        final MkGithub github = new MkGithub();
        final Repo repo = github.randomRepo();
        final Issue issue = repo.issues().create("Some issue", "");
        final Issue before = github.relogin("john").repos()
            .get(repo.coordinates())
            .issues()
            .get(issue.number());
        MatcherAssert.assertThat(
            "Issue %s belongs to %s, but should not"
                .formatted(
                    new Issue.Smart(before).title(),
                    github.users().self().login()
                ),
            new Mine(before).value(),
            new IsEqual<>(false)
        );
    }
}
