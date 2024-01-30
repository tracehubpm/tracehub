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

import com.jcabi.github.Github;
import com.jcabi.github.RtPagination;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Proc;

/**
 * Accept invites.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class AcceptInvites implements Proc<Boolean> {

    /**
     * GitHub.
     */
    private final Github github;

    @Override
    public void exec(final Boolean input) throws IOException {
        final Request entry = this.github.entry().reset("Accept").header(
            "Accept", "application/vnd.github.swamp-thing-preview+json"
        );
        final Iterable<JsonObject> all = new RtPagination<>(
            entry.uri().path("/user/repository_invitations").back(),
            RtPagination.COPYING
        );
        for (final JsonObject json : all) {
            final String repo = json.getJsonObject("repository").getString("full_name");
            final int invitation = json.getInt("id");
            try {
                entry.uri().path("/user/repository_invitations/")
                    .path(Integer.toString(invitation)).back()
                    .method(Request.PATCH)
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
                Logger.info(
                    this, "Invitation #%d to %s accepted",
                    invitation, repo
                );
            } catch (final AssertionError ex) {
                Logger.info(
                    this, "Failed to accept invitation #%d in %s: %s",
                    invitation, repo, ex.getLocalizedMessage()
                );
            }
        }
    }
}
