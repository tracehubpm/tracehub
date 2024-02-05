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

import com.jcabi.github.Repo;
import git.tracehub.agents.github.issues.OnAttachedLabel;
import git.tracehub.agents.github.issues.OnNew;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;

/**
 * Hook map.
 * @since 0.0.0
 * @todo #142:60min Implement dynamic object addition into a map.
 *  We should implement dynamic object addition into map with
 *  event codes and related objects. Probably we will need to read
 *  mapping sheet in some data format (JSON, XML, YAML, etc.),
 *  and dynamically fill the objects for each event.
 */
@RequiredArgsConstructor
public final class HookMap implements Scalar<Map<String, Scalar<?>>> {

    /**
     * Repo.
     */
    private final Repo repo;

    /**
     * Request.
     */
    private final JsonObject request;

    @Override
    public Map<String, Scalar<?>> value() throws Exception {
        final Map<String, Scalar<?>> mapped = new HashMap<>(4);
        mapped.put(
            "opened",
            new OnNew(this.request, this.repo, new ListOf<>("new"))
        );
        mapped.put(
            "labeled",
            new OnAttachedLabel(this.request, this.repo)
        );
        return mapped;
    }
}
