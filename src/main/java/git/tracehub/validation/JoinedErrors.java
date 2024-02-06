package git.tracehub.validation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.cactoos.Text;
import org.cactoos.text.Joined;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class JoinedErrors implements Text {

    private final Scalar<List<String>> errors;

    @Override
    public String asString() throws Exception {
        return new Joined("\n", this.errors.value()).asString();
    }
}
