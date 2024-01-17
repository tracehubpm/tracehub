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

import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;

/**
 * Tracehub jobs.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class ThJobs implements Commit {

    /**
     * Pattern to match.
     */
    private static final Pattern PATTERN = Pattern.compile("^\\.trace/jobs/.*$");

    /**
     * Origin.
     */
    private final Commit origin;

    @Override
    public String repo() throws Exception {
        return this.origin.repo();
    }

    @Override
    public List<String> created() {
        return this.origin.created().stream()
            .filter(name -> ThJobs.PATTERN.matcher(name).matches())
            .toList();
    }

    @Override
    public List<String> updated() {
        return this.origin.updated().stream()
            .filter(name -> ThJobs.PATTERN.matcher(name).matches())
            .toList();
    }

    @Override
    public List<String> deleted() {
        return this.origin.deleted().stream()
            .filter(name -> ThJobs.PATTERN.matcher(name).matches())
            .toList();
    }
}
