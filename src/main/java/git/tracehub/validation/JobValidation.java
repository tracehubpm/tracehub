package git.tracehub.validation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XSL;
import git.tracehub.Job;
import git.tracehub.Project;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class JobValidation implements Text {

    private final Job job;
    private final Project project;
    private final Scalar<Map<String, XSL>> sheets;

    @Override
    public String asString() throws Exception {
        Logger.info(
            this,
            "Starting validation of job labeled '%s'",
            this.job.label()
        );
        return new JoinedErrors(
            new XsErrors(
                new XsApplied(
                    new RulesBound(
                        this.job,
                        this.project
                    ),
                    this.sheets
                )
            )
        ).asString();
    }
}
