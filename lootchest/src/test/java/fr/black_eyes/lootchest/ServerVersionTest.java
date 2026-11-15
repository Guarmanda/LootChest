package fr.black_eyes.lootchest;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ServerVersionTest {
    @Test public void stripsPaperBuildMetadata() {
        assertEquals("26.3", ServerVersion.minecraftVersion("26.3.build.253-stable"));
        assertEquals(2630, ServerVersion.completeVersion("26.3.build.253-stable"));
    }

    @Test public void retainsMinecraftPatchBeforeBuildMetadata() {
        assertEquals("26.1.2", ServerVersion.minecraftVersion("26.1.2.build.74-stable"));
        assertEquals(2612, ServerVersion.completeVersion("26.1.2.build.74-stable"));
    }

    @Test public void acceptsSpigotAndUnqualifiedVersions() {
        assertEquals("26.3", ServerVersion.minecraftVersion("26.3-R0.1-SNAPSHOT"));
        assertEquals("26.3", ServerVersion.minecraftVersion("26.3"));
        assertEquals(2630, ServerVersion.completeVersion("26.3-R0.1-SNAPSHOT"));
    }

    @Test public void preservesLegacyFeatureCheckValues() {
        assertEquals(1071, ServerVersion.completeVersion("1.7.10-R0.1-SNAPSHOT"));
        assertEquals(1080, ServerVersion.completeVersion("1.8-R0.1-SNAPSHOT"));
        assertEquals(1088, ServerVersion.completeVersion("1.8.8-R0.1-SNAPSHOT"));
        assertEquals(1094, ServerVersion.completeVersion("1.9.4-R0.1-SNAPSHOT"));
        assertEquals(1130, ServerVersion.completeVersion("1.13-R0.1-SNAPSHOT"));
        assertEquals(1206, ServerVersion.completeVersion("1.20.6-R0.1-SNAPSHOT"));
        assertEquals(1214, ServerVersion.completeVersion("1.21.4-R0.1-SNAPSHOT"));
        assertEquals(12111, ServerVersion.completeVersion("1.21.11-R0.1-SNAPSHOT"));
    }
}
