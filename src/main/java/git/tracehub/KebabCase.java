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

import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.cactoos.Text;

/**
 * Kebab cased text.
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class KebabCase implements Text {

    /**
     * Kebab pattern.
     */
    private static final Pattern KEBAB = Pattern.compile("[^a-zA-Z0-9 .]");

    /**
     * Spaces and dots.
     */
    private static final Pattern SPACES_DOT = Pattern.compile("[ .]");

    /**
     * Original string.
     */
    private final String original;

    @Override
    public String asString() throws Exception {
        return KebabCase.SPACES_DOT.matcher(
            KebabCase.KEBAB.matcher(this.original).replaceAll("")
        ).replaceAll("-").toLowerCase(Locale.ROOT);
    }
}
