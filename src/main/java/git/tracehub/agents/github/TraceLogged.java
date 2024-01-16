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

import com.jcabi.log.Logger;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Logged commit.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class TraceLogged implements Commit {

    /**
     * Origin.
     */
    private final Commit origin;

    @Override
    public List<String> created() {
        final List<String> created = this.origin.created();
        if (!created.isEmpty()) {
            Logger.info(
                this,
                "found %s new .trace sources: %s",
                created.size(),
                created
            );
        }
        return created;
    }

    @Override
    public List<String> updated() {
        final List<String> updated = this.origin.updated();
        if (!updated.isEmpty()) {
            Logger.info(
                this,
                "found %s .trace updated sources: %s",
                updated.size(),
                updated
            );
        }
        return updated;
    }

    @Override
    public List<String> deleted() {
        final List<String> deleted = this.origin.deleted();
        if (!deleted.isEmpty()) {
            Logger.info(
                this,
                "found %s .trace sources for removal: %s",
                deleted.size(),
                deleted
            );
        }
        return deleted;
    }
}
