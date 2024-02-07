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

import com.jcabi.xml.XSL;
import git.tracehub.Job;
import git.tracehub.Project;
import java.io.IOException;
import java.util.Map;
import lombok.SneakyThrows;
import org.cactoos.Scalar;

/**
 * Job validation.
 *
 * @since 0.0.0
 */
public final class JobValidation extends ValidationEnvelope {
//
//    private final Job job;
//    private final Project project;
//    private final Scalar<Map<String, XSL>> sheets;

    public JobValidation(
        final Job validate,
        final Project project,
        final Scalar<Map<String, XSL>> shts
    ) throws IOException {
        super(
            validate.label(),
            new RulesBound(
                validate,
                project
            ),
            shts
        );
    }

//    @Override
//    public String asString() throws Exception {
//        Logger.info(
//            this,
//            "Starting validation of job labeled '%s'",
//            this.job.label()
//        );
//        return new JoinedErrors(
//            new XsErrors(
//                new XsApplied(
//                    new RulesBound(
//                        this.job,
//                        this.project
//                    ),
//                    this.sheets
//                )
//            )
//        ).asString();
//    }
}
