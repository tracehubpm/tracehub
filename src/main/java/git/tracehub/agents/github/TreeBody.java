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

import com.jcabi.github.Issue;
import git.tracehub.KebabCase;
import git.tracehub.agents.github.issues.YmlIssue;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * GitHub Tree request body.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class TreeBody implements Scalar<JsonObject> {

    /**
     * Head.
     */
    private final JsonObject head;

    /**
     * Issue.
     */
    private final Issue.Smart from;

    @Override
    public JsonObject value() throws Exception {
        return Json.createObjectBuilder()
            .add("base_tree", this.head.getJsonObject("tree").getString("sha"))
            .add(
                "tree",
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add(
                                "path",
                                ".trace/jobs/%s.yml".formatted(
                                    new KebabCase(this.from.title()).asString()
                                )
                            )
                            .add("mode", "100644")
                            .add("type", "blob")
                            .add(
                                "content",
                                new YmlIssue(this.from).value().toString()
                            )
                    )
            ).build();
    }
}
