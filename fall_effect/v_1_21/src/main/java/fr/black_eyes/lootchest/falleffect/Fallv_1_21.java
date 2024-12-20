package fr.black_eyes.lootchest.falleffect;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * 1.17+ class to make an invisible armorstand fall from the sky with packets and a block on its head
 */
@SuppressWarnings("unused")
public final class Fallv_1_21 implements IFallPacket {
    private final ClientboundAddEntityPacket spawnPacket;
    private final net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket dataPacket;
    private final ClientboundSetEquipmentPacket equipmentPacket;
    private final ClientboundMoveEntityPacket motionPacket;
    private final ArmorStand armorstand;
    private final Location startLocation;
    private final int height;
    private final double speed;
    private long counter;
    private static final short SPEED_ONE_BLOCK_PER_SECOND = 410; // speed found after like 10 tests corresponding to one block fall per second
    private static final long COUNTER_ONE_BLOCK = 10; // after 10*2 ticks at speed 410, the armorstand falls one block
    private static final short SPEED_MULTIPLIER = 31;
    private final JavaPlugin instance;

    /**
     * Get the actual location of the armorstand
     * Getting it with the entity class won't work because I move the armorstand, but only client sees it moving,
     * so I need to get the location from the start location and the counter
     * @return Location of the armorstand
     */
    @Override
    public Location getLocation() {
        Location loc = startLocation.clone();
        loc.setY(loc.getY() - (height-((counter /(COUNTER_ONE_BLOCK/(this.speed*SPEED_MULTIPLIER)))-3))   );
        return loc;
    }

    /**
     * Creates four packets to make an armorstand fall from the sky
     * The armorstand will have a head made of the headItem material
     * The armorstand will fall from its spawn location to {height} blocks below
     * One packet for the spawn of the armorstand
     * One packet for the equipment of the armorstand
     * One packet for the movement of the armorstand
     * One packet for its datas (invisible, etc.)
     * @param loc The location where the armorstand will spawn
     * @param headItem The material used for the head of the armorstand, the main reason for all of this
     * @param height The height of the fall, in blocks
     * @param speed The speed of the fall, does not have a clear meaning
     */
    public Fallv_1_21(Location loc, Material headItem, int height, double speed, JavaPlugin plugin) {
        this.instance = plugin;
        this.speed = speed;
        this.height = height;
        this.startLocation = loc;
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        
        // stream all levels and filter the one that matches the world name
        org.bukkit.World world = loc.getWorld();
        String worldName = (world != null) ? world.getName() : null;
        ServerLevel s = StreamSupport.stream(server.getAllLevels().spliterator(), false).filter(level -> level.getWorld().getName().equals(worldName)).findFirst().orElse(null);
        ArmorStand stand = new ArmorStand(s, loc.getX(), loc.getY(), loc.getZ());  
        stand.setInvisible(true);
        stand.setNoBasePlate(true);
        armorstand = stand;
        
        List<Pair<EquipmentSlot, ItemStack>> equipmentList = Collections.singletonList(
            new Pair<>(EquipmentSlot.HEAD, getNmsItemStackFromMaterial(headItem))  // nmsHeadItem is your helmet ItemStack
        );

        equipmentPacket = new ClientboundSetEquipmentPacket(stand.getId(), equipmentList);
            spawnPacket = new ClientboundAddEntityPacket(
                stand.getId(),                                  // Entity ID
                UUID.randomUUID(),                                  // Unique ID
                loc.getX(), loc.getY(), loc.getZ(),                  // Position (X, Y, Z)
                loc.getYaw(), loc.getPitch(),                        // Rotation (Yaw, Pitch)
                stand.getType(),                               // Entity type (ArmorStand)
                0,                                                  // No specific motion (use 0 for no velocity)
                new Vec3(0, 0, 0),                                   // Velocity (none in this case)
                0.0);   
            dataPacket = new ClientboundSetEntityDataPacket(stand.getId(), stand.getEntityData().getNonDefaultValues());
        short newSpeed = (short)(this.speed*SPEED_MULTIPLIER*SPEED_ONE_BLOCK_PER_SECOND); // the plugin had a default speed of 0.8 wich was quite fast, but it was never meaningful, 0.8 was like 5 blocks per seconds.
        // divide the counter by the speed multiplyer to get the number of ticks the armorstand will need to fall to get the fall ticks of one block for the new speed, then multiply it by the total height to fall
        counter = (int)((COUNTER_ONE_BLOCK/(this.speed*SPEED_MULTIPLIER))*(height+3));
        // I added 3 to height, else packet is removed too fast
        motionPacket = new ClientboundMoveEntityPacket.Pos(
                        armorstand.getId(),
                        (short) (0),  // Multiply by 4096 for correct movement scaling
                        (short) (-newSpeed), // Adjust Y for gravity/fall (lower Y for falling)
                        (short) (0),
                        true  // Yaw
                    ); 
    }

    /**
     * Sends the four packets to all players that are in a 100 blocks radius of the armorstand
     * An entity can't have gravity with packets, so we will manually move it to the ground, ticks after ticks
     */
    @Override
    public void sendPacketToAll() {
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        Stream<ServerPlayer> players = server.getPlayerList().getPlayers().stream();
        players.forEach(p -> {
            // check distance between player and armorstand
            if (p.distanceTo(armorstand) > 100) {
                return;
            }
            p.connection.send(spawnPacket);
            p.connection.send(dataPacket);
            p.connection.send(equipmentPacket);
            new BukkitRunnable() {
                    @Override
					public void run() {
                        p.connection.send(motionPacket);
                        counter--;
                        if(counter <= 0){
                            cancel();
                            removePacketToAll();
                        }
					}
                    
				}.runTaskTimer(instance, 0, 2L);
        });
    }

    /**
     * Removes the armorstand from all players
     */
    @Override
    public void removePacketToAll() {
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        Stream<ServerPlayer> players = server.getPlayerList().getPlayers().stream();
players.forEach(p -> p.connection.send(new ClientboundRemoveEntitiesPacket(armorstand.getId())));
    }


     /**
      * Get an NMS ItemStack from a Bukkit Material
      */
      public ItemStack getNmsItemStackFromMaterial(Material material) {
        if (material == null || !material.isItem()) return null;
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(material.getKey().toString())));
    }
}
