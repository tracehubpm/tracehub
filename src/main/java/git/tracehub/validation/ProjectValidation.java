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
import com.jcabi.xml.XSL;
import git.tracehub.Project;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.text.Joined;

/**
 * Project (project.yml) validation.
 *
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class ProjectValidation implements Text {

    /**
     * Project.
     */
    private final Project project;

    /**
     * Sheets to apply.
     */
    private final Scalar<Map<String, XSL>> sheets;

    @Override
    public String asString() throws Exception {
        Logger.info(
            this,
            "Starting validation of project.yml in %s",
            this.project.identity()
        );
        return new Joined(
            "\n",
            new XsErrors(
                new XsApplied(
                    this.project.asXml(),
                    this.sheets
                )
            ).value()
        ).asString();
    }
}
