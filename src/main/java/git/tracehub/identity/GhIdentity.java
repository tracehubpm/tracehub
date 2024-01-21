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

import com.jcabi.aspects.Cacheable;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.log.Logger;
import org.cactoos.Scalar;

/**
 * GitHub Identity.
 *
 * @since 0.0.0
 * @todo #25:20min verify compatibility with Docker environment variables.
 *  After replacing System#getEnv() to System#getProperty() for setting
 *  properties during integration build with github, we probably
 *  can break ENV settings during docker run. We should double check
 *  that. Don't forget to remove this puzzle.
 */
public final class GhIdentity implements Scalar<Github> {

    @Override
    @Cacheable(forever = true)
    public Github value() throws Exception {
        Logger.info(this, "Connecting to GitHub...");
        final String token = System.getProperty("Tracehub-GitHubToken");
        final Github github;
        if (token == null) {
            github = new MkGithub();
        } else {
            github = new RtGithub(
                new RtGithub(token).entry()
            );
        }
        Logger.info(this, "GitHub object instantiated");
        Logger.info(
            this, "GitHub connected as @%s",
            github.users().self().login()
        );
        return github;
    }
}
