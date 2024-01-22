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

import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import com.yegor256.WeAreOnline;
import git.tracehub.validation.Remote;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Integration test case for {@link Remote}.
 *
 * @since 0.0.0
 */
final class RemoteITCase {

    @Test
    @ExtendWith(WeAreOnline.class)
    void readsRemoteXsl() throws Exception {
        final XSL sheet = new Remote("master", "struct").value();
        final XSL expected = new XSLDocument(
            new ResourceOf("it/sheets/struct.xsl").stream()
        );
        MatcherAssert.assertThat(
            "Fetched XSL sheet %s does not match with expected %s"
                .formatted(sheet, expected),
            sheet,
            new IsEqual<>(expected)
        );
    }

    @Test
    @ExtendWith(WeAreOnline.class)
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void throwsOnUnexistedSheet() {
        final String name = "does-not-exist";
        new Assertion<>(
            "Remote does not throw exception on fetching sheet that does not exists",
            () -> new Remote("master", name)
                .value(),
            new Throws<>(
                "XSL sheet '%s' is not found in https://raw.githubusercontent.com/tracehubpm/vsheets/master/xsl/%s.xsl"
                    .formatted(name, name),
                IOException.class
            )
        ).affirm();
    }
}
