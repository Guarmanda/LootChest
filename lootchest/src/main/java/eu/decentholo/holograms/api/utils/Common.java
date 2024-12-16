package eu.decentholo.holograms.api.utils;

import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Common {

    /**
     * This method generates random Integer between min and max
     *
     * @param min Minimal random number (inclusive)
     * @param max Maximum random number (inclusive)
     * @return Randomly generated Integer
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /*
     * 	Colorize
     */
    public static String colorize(String string) {
        return string;
    }

}
