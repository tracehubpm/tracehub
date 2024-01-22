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
package git.tracehub.validation;

import java.util.List;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Collected}.
 *
 * @since 0.0.0
 * @todo #15:60min Enable test when Remote sheets will be available.
 *  For now tests do not work. We should wait for stable implementation
 *  of Remote sheets. After it will be released, enable the tests.
 */
final class CollectedTest {

    @Test
    @Disabled
    void collectsAllSheets() throws Exception {
        final List<String> all = new Collected().value();
        MatcherAssert.assertThat(
            "Sheets %s do not match with expected format"
                .formatted(all),
            all,
            new IsEqual<>(
                new ListOf<>(
                    "src/main/resources/git/tracehub/validation/arc.xsl",
                    "src/main/resources/git/tracehub/validation/struct.xsl",
                    "src/main/resources/git/tracehub/validation/errors.xsl",
                    "src/main/resources/git/tracehub/validation/dev.xsl"
                )
            )
        );
    }

    @Test
    @Disabled
    void excludesProvidedSheets() throws Exception {
        final List<String> sheets = new Collected(
            () -> new ListOf<>(
                "src/main/resources/git/tracehub/validation/dev.xsl"
            )
        ).value();
        MatcherAssert.assertThat(
            "Sheets %s do not match with expected"
                .formatted(sheets),
            sheets,
            new IsEqual<>(
                new ListOf<>(
                    "src/main/resources/git/tracehub/validation/arc.xsl",
                    "src/main/resources/git/tracehub/validation/struct.xsl",
                    "src/main/resources/git/tracehub/validation/errors.xsl"
                )
            )
        );
    }
}
