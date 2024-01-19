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
package git.tracehub.extensions;

import com.jcabi.github.Repo;
import com.jcabi.github.User;
import com.jcabi.github.mock.MkGithub;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Repo with a few collaborators.
 *
 * @since 0.0.0
 * @checkstyle LocalFinalVariableNameCheck (40 lines)
 */
public final class RepoWithCollaborators implements ParameterResolver {
    @Override
    public boolean supportsParameter(
        final ParameterContext pct,
        final ExtensionContext ect
    ) throws ParameterResolutionException {
        return Objects.equals(pct.getParameter().getType(), Repo.class);
    }

    @SneakyThrows
    @Override
    public Object resolveParameter(
        final ParameterContext pct,
        final ExtensionContext ect
    ) throws ParameterResolutionException {
        final MkGithub gh = new MkGithub();
        final User h1alexbel = gh.users().add("h1alexbel");
        final User hizmailovich = gh.users().add("hizmailovich");
        final Repo repo = gh.randomRepo();
        repo.collaborators().add(h1alexbel.login());
        repo.collaborators().add(hizmailovich.login());
        return repo;
    }
}
