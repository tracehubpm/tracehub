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
import com.jcabi.github.Repo;
import git.tracehub.facts.Mine;
import java.util.List;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * On new opened issue.
 *
 * @since 0.0.0
 * @todo #125:45min Create an integration test for OnNew.java.
 *  We should create a few integration tests that will check
 *  both cases: labeled issue, and issue ignored. It can be done
 *  right after we will implement <a href="https://github.com/tracehubpm/tracehub/issues/105">this</a>,
 *  include it in {@link git.tracehub.tk.TkGitHub}, so it will be testable.
 */
@RequiredArgsConstructor
public final class OnNew implements Scalar<Issue> {

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * Request.
     */
    private final JsonObject request;

    /**
     * Labels.
     */
    private final List<String> labels;

    @Override
    public Issue value() throws Exception {
        final Issue.Smart submitted = new Issue.Smart(
            this.repo.issues().get(
                this.request.getJsonObject("issue").getInt("number")
            )
        );
        if (!new Mine(submitted).value()) {
            submitted.labels().add(this.labels);
        }
        return submitted;
    }
}
