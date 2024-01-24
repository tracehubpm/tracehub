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

import com.jcabi.github.Github;
import com.jcabi.log.Logger;
import git.tracehub.identity.GhIdentity;
import git.tracehub.tk.FtApp;
import org.takes.http.Exit;

/**
 * Entry point.
 *
 * @since 0.0.0
 * @checkstyle HideUtilityClassConstructorCheck (10 lines)
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class Entry {

    /**
     * Application entry point.
     *
     * @param args Application arguments
     * @throws Exception if something went wrong
     */
    public static void main(final String... args) throws Exception {
        Logger.info(Entry.class, "Starting Tracehub on the command line...");
        final Github github = new GhIdentity().value();
        final String vtag = System.getenv("Vsheets-Tag");
        new FtApp(github, vtag).value().start(Exit.NEVER);
    }
}
