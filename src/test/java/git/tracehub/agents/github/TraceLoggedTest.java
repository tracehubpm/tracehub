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

import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import java.util.List;
import nl.altindag.log.LogCaptor;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TraceLogged}.
 *
 * @since 0.0.0
 */
final class TraceLoggedTest {

    @Test
    void logsInConsole() throws Exception {
        final LogCaptor capt = LogCaptor.forClass(TraceLogged.class);
        final Commit commit = new TraceLogged(
            new TraceOnly(
                new Composed(
                    new GhCommits(
                        new RqFake(
                            "POST",
                            "",
                            new Jocument(
                                new JsonOf(
                                    new ResourceOf(
                                        "github/hooks/more-duplicates.json"
                                    ).stream()
                                )
                            ).pretty()
                        )
                    )
                )
            )
        );
        final List<String> created = commit.created();
        commit.updated();
        final List<String> deleted = commit.deleted();
        final List<String> infos = capt.getInfoLogs();
        MatcherAssert.assertThat(
            "Logs %s for %s commit do not match with expected"
                .formatted(infos, commit),
            infos,
            new IsEqual<>(
                new ListOf<>(
                    "found 1 new .trace sources: %s".formatted(created),
                    "found 1 .trace sources for removal: %s".formatted(deleted)
                )
            )
        );
    }
}
