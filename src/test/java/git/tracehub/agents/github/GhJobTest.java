package git.tracehub.agents.github;

import com.amihaiemil.eoyaml.Yaml;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link GhJob}.
 *
 * @since 0.0.0
 */
final class GhJobTest {

    @Test
    void returnsLabel() throws Exception {
        final String label = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/fix-me.yml").stream()
            )
        ).label();
        final String expected = "Update License year to 2024";
        MatcherAssert.assertThat(
            "Job label %s does not match with expected %s"
                .formatted(label, expected),
            label,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsOnelineDescription() throws Exception {
        final String description = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf(
                    "github/jobs/oneline-description.yml"
                ).stream()
            )
        ).verbose();
        final String expected = "Lets update a copyright year in our License to 2024";
        MatcherAssert.assertThat(
            "Job description %s does not match with expected %s"
                .formatted(description, expected),
            description,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsDescription() throws Exception {
        final String description = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/fix-me.yml").stream()
            )
        ).verbose();
        final String expected = "Lets update a copyright year in our License to 2024. Its very important task.";
        MatcherAssert.assertThat(
            "Job description %s does not match with expected %s"
                .formatted(description, expected),
            description,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsCost() throws Exception {
        final String cost = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/fix-me.yml").stream()
            )
        ).cost();
        final String expected = "20 minutes";
        MatcherAssert.assertThat(
            "Job cost %s does not match with expected %s"
                .formatted(cost, expected),
            cost,
            new IsEqual<>(expected)
        );
    }

    @Test
    void returnsNullOnInvalidYaml() throws Exception {
        final String label = new GhJob(
            Yaml.createYamlInput(
                new ResourceOf("github/jobs/--invalid.yml").stream()
            )
        ).label();
        MatcherAssert.assertThat(
            "Label %s does not match with NULL".formatted(label),
            label,
            new IsNull<>()
        );
    }
}