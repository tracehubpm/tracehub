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

import com.jcabi.http.request.BaseRequest;
import com.yegor256.WeAreOnline;
import git.tracehub.agents.github.GhRun;
import git.tracehub.identity.GhIdentity;
import io.github.h1alexbel.ghquota.Quota;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import nl.altindag.log.LogCaptor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test case for {@link GhRun}.
 *
 * @since 0.0.0
 */
final class GhRunITCase {

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    void runs() throws Exception {
        final LogCaptor capt = LogCaptor.forClass(BaseRequest.class);
        new GhRun(new GhIdentity().value()).start();
        Thread.currentThread().join(Duration.ofSeconds(65L).toMillis());
        final List<String> infos = capt.getInfoLogs();
        final String invite = infos.get(1);
        final Pattern pattern = Pattern.compile(
            "#fetch\\(GET api\\.github\\.com /user/repository_invitations\\): \\[200 OK] in \\d+ms"
        );
        MatcherAssert.assertThat(
            "Invite logs %s do not match with regex %s"
                .formatted(invite, pattern),
            pattern.matcher(invite).matches(),
            new IsEqual<>(true)
        );
    }
}
