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
import java.util.List;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Labeled}.
 *
 * @since 0.0.0
 */
final class LabeledTest {

    @Test
    void attachesLabels() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final List<String> expected = new ListOf<>("synced", "routine");
        final Issue labeled = new Labeled(
            () -> repo.issues().create(
                "Test issue", "this needs to be fixed..."
            ),
            expected
        ).value();
        final IssueLabels.Smart labels = new IssueLabels.Smart(
            labeled.labels()
        );
        final List<String> actual = new ListOf<>();
        labels.iterate().forEach(label -> actual.add(label.name()));
        MatcherAssert.assertThat(
            "Labels %s do not match with expected %s"
                .formatted(labels.toString(), expected),
            actual,
            new IsEqual<>(expected)
        );
    }
}
