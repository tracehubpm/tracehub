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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import git.tracehub.tk.TkGitHub;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;

/**
 * Integration test case for {@link git.tracehub.tk.TkGitHub}.
 *
 * @since 0.0.0
 * @todo #22:60min Create test with push webhook JSON payload.
 *  We should create an integration test for push GitHub webhook with it payload.
 *  Probably we need to configure Takes so that it will start accepting JSON payloads
 *  on /github/hook.
 *  Don't forget to remove this puzzle.
 */
final class TkGitHubITCase {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void returnsResponseOnHook() throws Exception {
        new FtRemote(new TkGitHub()).exec(
            home -> new JdkRequest(home)
                .fetch()
                .as(RestResponse.class)
                .assertStatus(200)
                .assertBody(Matchers.equalTo("GitHub webhook"))
        );
    }
}
