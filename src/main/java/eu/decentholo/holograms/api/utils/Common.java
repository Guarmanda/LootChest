package eu.decentholo.holograms.api.utils;

import lombok.experimental.UtilityClass;



import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class Common {

    public static final String NAME_REGEX = "[a-zA-Z0-9_-]+";

    public static String PREFIX;

    static {
        PREFIX = "&8[&3DecentHolograms&8] &7";
    }

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

    public static List<String> colorize(List<String> list) {
        list.replaceAll(Common::colorize);
        return list;
    }


}
