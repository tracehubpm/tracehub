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
package git.tracehub.tk;

import com.jcabi.github.Github;
import java.net.HttpURLConnection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.cactoos.Scalar;
import org.cactoos.bytes.BytesOf;
import org.cactoos.text.TextOf;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.fork.TkMethods;
import org.takes.http.Front;
import org.takes.http.FtBasic;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsVelocity;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkSlf4j;

/**
 * Front Application.
 *
 * @since 0.0.0
 * @todo #25:45min Test error.html.vm template.
 *  We should test that error page can be rendered and
 *  shown to the end-users with a exception stacktrace.
 * @todo #25:45min Test fallbacks.
 *  We should test all fallbacks we have in both:
 *  unit and integration tests.
 *  Don't forget to remove this puzzle.
 */
@RequiredArgsConstructor
public final class FtApp implements Scalar<Front> {

    /**
     * GitHub.
     */
    private final Github github;

    @Override
    public Front value() throws Exception {
        return new FtBasic(
            new TkSlf4j(
                new TkFallback(
                    new TkFork(
                        new FkRegex(
                            "/github/hook",
                            new TkMethods(
                                new TkGitHub(this.github, ""),
                                "POST"
                            )
                        ),
                        new FkRegex("/gitlab/hook", new TkGitLab()),
                        new FkRegex("/bitbucket/hook", new TkBitbucket()),
                        new FkRegex("/jira/hook", new TkJira()),
                        new FkRegex("/confluence/hook", new TkConfluence())
                    ),
                    new FbChain(
                        new FbStatus(
                            404,
                            new RsText(
                                "Sorry, this page is absent, probably link is broken"
                            )
                        ),
                        new FbStatus(
                            405,
                            new RsText(
                                "This method is not allowed here"
                            )
                        ),
                        req -> new Opt.Single<>(
                            new RsWithStatus(
                                new RsVelocity(
                                    FtApp.class.getResource("error.html.vm"),
                                    new RsVelocity.Pair(
                                        "error",
                                        StringEscapeUtils.escapeHtml4(
                                            new TextOf(
                                                new BytesOf(
                                                    req.throwable()
                                                )
                                            ).asString()
                                        )
                                    )
                                ),
                                HttpURLConnection.HTTP_INTERNAL_ERROR
                            )
                        )
                    )
                )
            ),
            8080
        );
    }
}
