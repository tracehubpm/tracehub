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

import com.jcabi.github.Content;
import com.jcabi.github.Repo;
import javax.json.Json;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * Contribution to Mock GitHub.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
@SuppressWarnings(
    {
        "JTCOP.RuleAllTestsHaveProductionClass",
        "JTCOP.RuleCorrectTestName"
    }
)
public final class MkContribution implements Scalar<Content> {

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * YAML Path.
     */
    private final String path;

    /**
     * YAML Content.
     */
    private final String yml;

    @Override
    public Content value() throws Exception {
        return this.repo.contents().create(
            Json.createObjectBuilder()
                .add("path", this.path)
                .add("message", "contributed")
                .add("content", this.yml)
                .build()
        );
    }
}
