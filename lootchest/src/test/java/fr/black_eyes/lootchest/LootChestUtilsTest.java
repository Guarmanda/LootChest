package fr.black_eyes.lootchest;

import java.lang.reflect.Proxy;
import org.bukkit.World;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LootChestUtilsTest {
    @Test public void readsWorldNameIndependentlyOfCraftWorldDebugFormat() {
        World world = (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getName")) return "kingdom_test";
                    if (method.getName().equals("toString")) return "CraftWorld{key=minecraft:overworld}";
                    throw new UnsupportedOperationException(method.getName());
                });
        assertEquals("kingdom_test", LootChestUtils.getWorldName(world));
    }
}
