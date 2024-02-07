package git.tracehub.validation;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public abstract class ValidationEnvelope implements Text {

    private final String identity;
    private final Scalar<XML> candidate;
    private final Scalar<Map<String, XSL>> sheets;

    @Override
    public final String asString() throws Exception {
        Logger.info(
            this,
            "Starting validation of job labeled '%s'",
            this.identity
        );
        return new JoinedErrors(
            new XsErrors(
                new XsApplied(
                    this.candidate,
                    this.sheets
                )
            )
        ).asString();
    }
}
