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

import com.amihaiemil.eoyaml.YamlNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cactoos.list.ListOf;

/**
 * Performer.
 *
 * @since 0.0.0
 */
public interface Performer {

    /**
     * Name.
     *
     * @return Name
     */
    String name();

    /**
     * Roles.
     *
     * @return Roles
     */
    List<String> roles();

    /**
     * Simple performer.
     *
     * @since 0.0.0
     */
    @RequiredArgsConstructor
    final class Simple implements Performer {

        /**
         * Node.
         */
        private final YamlNode yaml;

        @Override
        public String name() {
            return this.yaml.asMapping().string("name");
        }

        @Override
        public List<String> roles() {
            final List<String> roles = new ListOf<>();
            this.yaml.asMapping().yamlSequence("roles")
                .forEach(node -> roles.add(node.asScalar().value()));
            return roles;
        }
    }
}
