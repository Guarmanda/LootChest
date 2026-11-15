package fr.black_eyes.lootchest;

/** Minecraft version values shared by feature checks and fall-effect selection. */
public final class ServerVersion {
    private ServerVersion() {
    }

    public static String minecraftVersion(String bukkitVersion) {
        // Paper 26.x appends .build.<number>-<channel> to the Minecraft version.
        return bukkitVersion.split("(?:\\.build\\.|-)", 2)[0];
    }

    public static int completeVersion(String bukkitVersion) {
        String completeVer = minecraftVersion(bukkitVersion);
        String sversion = completeVer.replace(".", "");
        if (sversion.startsWith("18") || sversion.startsWith("19") || sversion.startsWith("17")) {
            //add a 0 between the first and second digit
            sversion = sversion.charAt(0) + "0" + sversion.substring(1);
            if (sversion.endsWith("10")) {
                //remove the 0 at the end
                sversion = sversion.substring(0, sversion.length() - 1);
            }
        }
        if (sversion.length() == 3) {
            //add a 0 at the end
            sversion = sversion + "0";
        }
        // we just have to remove the dots and parse string as integer
        return Integer.parseInt(sversion);
    }
}
