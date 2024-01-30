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
package git.tracehub.agents.github;

import com.jcabi.github.Github;
import com.jcabi.log.VerboseRunnable;
import com.jcabi.log.VerboseThreads;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.cactoos.proc.IoCheckedProc;

/**
 * GitHub Invites.
 *
 * @since 0.0.0
 */
public final class GhRun implements Runnable {

    /**
     * GitHub.
     */
    private final Github github;

    /**
     * Schedule Service.
     */
    private final ScheduledExecutorService service;

    /**
     * Ctor.
     *
     * @param gthb GitHub
     */
    public GhRun(final Github gthb) {
        this.github = gthb;
        this.service = Executors.newSingleThreadScheduledExecutor(
            new VerboseThreads(GhRun.class)
        );
    }

    @SneakyThrows
    @Override
    public void run() {
        new IoCheckedProc<>(
            new AcceptInvites(this.github)
        ).exec(true);
    }

    /**
     * Start it.
     */
    public void start() {
        this.service.scheduleWithFixedDelay(
            new VerboseRunnable(this, true, true),
            1L, 1L, TimeUnit.MINUTES
        );
    }
}
