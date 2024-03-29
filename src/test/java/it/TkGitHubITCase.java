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

import com.jcabi.github.Github;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.yegor256.WeAreOnline;
import git.tracehub.identity.GhIdentity;
import git.tracehub.tk.TkGitHub;
import io.github.h1alexbel.ghquota.Quota;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.takes.http.FtRemote;

/**
 * Integration test case for {@link TkGitHub}.
 *
 * @since 0.0.0
 * @todo #84:45min Create assertions after issue was created in #returnsResponseOnHook.
 *  Right now we are just checking the response and the status of it.
 *  Its not enough for this integration test. We should make an assertions that checks
 *  whether issue is created/commented/assigned or not.
 *  Depends on this <a href="https://github.com/tracehubpm/tracehub/issues/55">issue</a>.
 * @todo #64:30min Test case for invalid job.
 *  We should test case when invalid job was submitted (so validation
 *  failed). Don't forget to remove this puzzle.
 */
final class TkGitHubITCase {

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void returnsResponseOnPush() throws Exception {
        final Github github = new GhIdentity().value();
        new FtRemote(new TkGitHub(github, "master")).exec(
            home -> new JdkRequest(home)
                .method("POST")
                .header("Accept", "application/json")
                .body()
                .set(
                    new TextOf(
                        new ResourceOf("it/github/job-hook.json")
                    ).asString()
                )
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(200)
                .assertBody(
                    Matchers.equalTo(
                        "Thanks for webhook, h1alexbel/test"
                    )
                )
        );
    }

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void returnsResponseOnOpened() throws Exception {
        final Github github = new GhIdentity().value();
        new FtRemote(new TkGitHub(github, "master")).exec(
            home -> new JdkRequest(home)
                .method("POST")
                .header("Accept", "application/json")
                .body()
                .set(
                    new TextOf(
                        new ResourceOf("it/github/new-issue.json")
                    ).asString()
                )
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(200)
                .assertBody(
                    Matchers.equalTo(
                        "Thanks for webhook, h1alexbel/test"
                    )
                )
        );
    }

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void returnsResponseOnLabeledBug() throws Exception {
        final Github github = new GhIdentity().value();
        new FtRemote(new TkGitHub(github, "master")).exec(
            home -> new JdkRequest(home)
                .method("POST")
                .header("Accept", "application/json")
                .body()
                .set(
                    new TextOf(
                        new ResourceOf("it/github/bug-123.json")
                    ).asString()
                )
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(200)
                .assertBody(
                    Matchers.equalTo(
                        "Thanks for webhook, h1alexbel/test"
                    )
                )
        );
    }

    @Test
    @Tag("simulation")
    @ExtendWith({WeAreOnline.class, Quota.class})
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    // @checkstyle StringLiteralsConcatenationCheck (25 lines)
    void foundsErrors() throws Exception {
        final Github github = new GhIdentity().value();
        new FtRemote(new TkGitHub(github, "master")).exec(
            home -> new JdkRequest(home)
                .method("POST")
                .header("Accept", "application/json")
                .body()
                .set(
                    new TextOf(
                        new ResourceOf("it/github/project-with-errors.json")
                    ).asString()
                )
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(400)
                .assertBody(
                    Matchers.equalTo(
                        "`project.yml` contains errors:\n"
                        + "Project must have exactly one Architect.\n"
                        + "At least one performer must have the DEV role."
                    )
                )
        );
    }
}
