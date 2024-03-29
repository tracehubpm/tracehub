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
package git.tracehub;

import com.amihaiemil.eoyaml.Yaml;
import java.io.IOException;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link YmlBacklog}.
 *
 * @since 0.0.0
 */
final class YmlBacklogTest {

    @Test
    void readsType() throws IOException {
        final String expected = "GitHub";
        final Backlog backlog = new YmlBacklog(
            Yaml.createYamlInput(
                "type: GitHub"
            ).readYamlMapping()
        );
        final String platform = backlog.where();
        MatcherAssert.assertThat(
            "Backlogs platform %s does not match with expected %s"
                .formatted(platform, expected),
            platform,
            new IsEqual<>(expected)
        );
    }

    @Test
    void readsRules() throws Exception {
        final Rules rules = new YmlBacklog(
            Yaml.createYamlInput(
                new ResourceOf("yml/projects/with-rules.yml").stream()
            ).readYamlMapping().value("backlog")
        ).rules();
        final int size = rules.value().size();
        final int expected = 3;
        MatcherAssert.assertThat(
            "Rules size %s does not match with expected %s"
                .formatted(size, expected),
            size,
            new IsEqual<>(expected)
        );
    }
}
