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
package git.tracehub.identity;

import com.yegor256.WeAreOnline;
import io.github.h1alexbel.ghquota.Quota;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link GhIdentity}.
 *
 * @since 0.0.0
 */
final class GhIdentityTest {

    @Test
    @Tag("mock")
    void loginsAsJeff() throws Exception {
        final String self = new GhIdentity().value().users().self().login();
        final String expected = "jeff";
        MatcherAssert.assertThat(
            "GitHub login %s does not match with expected one %s"
                .formatted(self, expected),
            self,
            new IsEqual<>(expected)
        );
    }

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    void loginsAsBot() throws Exception {
        final String self = new GhIdentity().value().users().self().login();
        final String expected = "tracehubgit";
        MatcherAssert.assertThat(
            "GitHub login %s does not match with expected one %s"
                .formatted(self, expected),
            self,
            new IsEqual<>(expected)
        );
    }
}
