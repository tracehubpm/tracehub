package git.tracehub.tk;

import com.jcabi.github.Repo;
import lombok.RequiredArgsConstructor;
import org.cactoos.Scalar;
import org.takes.Request;

/**
 * @since 0.0.0
 */
@RequiredArgsConstructor
public final class RqRepo implements Scalar<Repo> {

    private final Request request;

    @Override
    public Repo value() throws Exception {
        throw new UnsupportedOperationException("#value()");
    }
}
