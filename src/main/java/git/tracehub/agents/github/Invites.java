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
import git.tracehub.agents.Act;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;

/**
 * GitHub Invites.
 *
 * @since 0.0.0
 * @todo #1:45min Schedule Invites#exec in the background.
 *   We should schedule GitHub invitations accepting to run it in the background
 *   while application is running.
 *   Don't forget to remove this puzzle.
 */
@RequiredArgsConstructor
public final class Invites implements Act {

    /**
     * GitHub.
     */
    private final Github github;

    @Override
    public void exec() throws IOException {
        final Request entry = this.github.entry().reset("Accept").header(
            "Accept", "application/vnd.github.swamp-thing-preview+json"
        );
        final Iterable<JsonObject> all = new RtPagination<>(
            entry.uri().path("/user/repository_invitations").back(),
            RtPagination.COPYING
        );
        for (final JsonObject json : all) {
            this.accept(
                entry, json.getInt("id"),
                json.getJsonObject("repository").getString("full_name")
            );
        }
    }

    /**
     * Accept one invite.
     *
     * @param entry Entry
     * @param invitation Invitation
     * @param repo Repo
     * @throws IOException if I/O fails
     * @todo #1:25min Resolve private method with invite accepting.
     *  Let's create a new class instead of this #accept() private method.
     *  Don't forget to remove this puzzle.
     */
    private void accept(
        final Request entry,
        final int invitation,
        final String repo
    ) throws IOException {
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
