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
package it;

import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Issue;
import git.tracehub.agents.github.issues.OnAttachedLabel;
import git.tracehub.identity.GhIdentity;
import io.github.eocqrs.eokson.Jocument;
import io.github.eocqrs.eokson.JsonOf;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Integration test case for {@link OnAttachedLabel}.
 *
 * @since 0.0.0
 */
final class OnAttachedLabelITCase {

    @Test
    @Tag("simulation")
    void respondsWithNewPull() throws Exception {
        final Issue commented = new OnAttachedLabel(
            new RqFake(
                "POST",
                "",
                new Jocument(
                    new JsonOf(
                        new ResourceOf("it/github/bug-123.json")
                            .stream()
                    )
                ).pretty()
            ),
            new GhIdentity().value()
                .repos().get(new Coordinates.Simple("h1alexbel/test"))
        ).value();
        final String text = new Comment.Smart(
            new ListOf<>(
                commented.comments()
                    .iterate(new Date(Instant.now().toEpochMilli()))
            ).get(0)
        ).body();
        final Pattern pattern = Pattern.compile(
            "@h1alexbel,\\sI've created #\\d+ for adding this issue into work queue\\."
        );
        MatcherAssert.assertThat(
            "Comment '%s' on issue '%s' does not match with %s regex"
                .formatted(
                    text,
                    new Issue.Smart(commented).title(),
                    pattern
                ),
            pattern.matcher(text).matches(),
            new IsEqual<>(true)
        );
    }
}
