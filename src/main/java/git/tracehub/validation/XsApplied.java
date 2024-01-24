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
package git.tracehub.validation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLChain;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;

/**
 * Validated XML document.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class XsApplied implements Scalar<XML> {

    /**
     * XML to validate.
     */
    private final XML origin;

    /**
     * Sheets.
     */
    private final Scalar<Map<String, XSL>> sheets;

    @Override
    public XML value() throws Exception {
        final Map<String, XSL> pipeline = this.sheets.value();
        Logger.info(
            this,
            "Applying validation, sheets activated: %s",
            pipeline.keySet()
        );
        return new XSLChain(pipeline.values()).transform(this.origin);
    }
}
