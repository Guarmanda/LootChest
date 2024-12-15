package fr.black_eyes.lootchest.falleffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.Item;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_15_R1.Vec3D;
import net.minecraft.server.v1_15_R1.WorldServer;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;





/**
 * 1.17+ class to make an invisible armorstand fall from the sky with packets and a block on its head
 */
public class Fallv_1_15_1 implements IFallPacket {
    private final PacketPlayOutSpawnEntity spawnPacket;
    private final PacketPlayOutEntityMetadata dataPacket;
    private final PacketPlayOutEntityEquipment equipmentPacket;
    private final PacketPlayOutRelEntityMove motionPacket;
    private final EntityArmorStand armorstand;
    private final Location startLocation;
    private final int height;
    private final double speed;
    private long counter;
    private final short SPEED_ONE_BLOCK_PER_SECOND = 410; // speed found after like 10 tests corresponding to one block fall per second
    private final long COUNTER_ONE_BLOCK = 10; // after 10*2 ticks at speed 410, the armorstand falls one block
    private static final short SPEED_MULTIPLYER = 31; 
    private static ItemStack headItem;
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
        loc.setY(loc.getY() - (height-((counter /(COUNTER_ONE_BLOCK/(this.speed*SPEED_MULTIPLYER)))-3))   );
        return loc;
    }

    /**
     * Creates four packets to make an armorstand fall from the sky
     * The armorstand will have a head made of the headItem material
     * The armorstand will fall from its spawn location to {height} blocks below
     * One packet for the spawn of the armorstand
     * One packet for the equipment of the armorstand
     * One packet for the movement of the armorstand
     * One packet for its datas (invisible, etc)
     * @param loc The location where the armorstand will spawn
     * @param headItem The material used for the head of the armorstand, the main reason for all of this
     * @param height The height of the fall, in blocks
     * @param speed The speed of the fall, does not have a clear meaning
     */
    public Fallv_1_15_1(Location loc, Material headItem, int height, double speed, JavaPlugin plugin) {
        this.instance = plugin;
        this.speed = speed;
        this.height = height;
        this.startLocation = loc;
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        
        // stream all levels and filter the one that matches the world name
        org.bukkit.World world = loc.getWorld();
        String worldName = (world != null) ? world.getName() : null;
        WorldServer s = StreamSupport.stream(server.getWorlds().spliterator(), false).filter(level -> level.getWorld().getName().equals(worldName)).findFirst().orElse(null);
        EntityArmorStand stand = new EntityArmorStand(s, loc.getX(), loc.getY(), loc.getZ());  
        stand.setInvisible(true);
        
        armorstand = stand;
        
        equipmentPacket = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, getNmsItemStackFromMaterial(headItem));
        

            spawnPacket = new PacketPlayOutSpawnEntity(
                stand.getId(),                                  // Entity ID
                UUID.randomUUID(),                                  // Unique ID
                loc.getX(), loc.getY(), loc.getZ(),                  // Position (X, Y, Z)
                loc.getYaw(), loc.getPitch(),                        // Rotation (Yaw, Pitch)
                stand.getEntityType(),                               // Entity type (ArmorStand)
                0,                                                  // No specific motion (use 0 for no velocity)
                new Vec3D(0, 0, 0)                                   // Velocity (none in this case)
                );   

                //dataPacket = constructor.newInstance(stand.getId(), stand.getEntityData().getNonDefaultValues(), true);

            dataPacket = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
        short new_speed = (short)(this.speed*SPEED_MULTIPLYER*SPEED_ONE_BLOCK_PER_SECOND); // the plugin had a default speed of 0.8 wich was quite fast, but it was never meaningful, 0.8 was like 5 blocks per seconds.
        // divide the counter by the speed multiplyer to get the number of ticks the armorstand will need to fall to get the fall ticks of one block for the new speed, then multiply it by the total height to fall
        counter = (int)((COUNTER_ONE_BLOCK/(this.speed*SPEED_MULTIPLYER))*(height+3));
        // I added 3 to height, else packet is removed too fast
        motionPacket = new PacketPlayOutRelEntityMove(
                        armorstand.getId(),
                        (short) (0),  // Multiply by 4096 for correct movement scaling
                        (short) (-new_speed), // Adjust Y for gravity/fall (lower Y for falling)
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
        Stream<EntityPlayer> players = StreamSupport.stream(server.getPlayerList().getPlayers().spliterator(), false);
        players.forEach(p -> {
            // get player from uuid
            Player bukkitPlayer = Bukkit.getPlayer(p.getUniqueID());
            //p.getUniqueID();
            // check distance between player and armorstand
            if (bukkitPlayer != null && bukkitPlayer.getLocation().distance(startLocation) > 100) {
                return;
            }
            p.playerConnection.sendPacket(spawnPacket);
            p.playerConnection.sendPacket(dataPacket);
            p.playerConnection.sendPacket(equipmentPacket);
            new BukkitRunnable() {
                    @Override
					public void run() {
                        p.playerConnection.sendPacket(motionPacket);
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
        Stream<EntityPlayer> players = StreamSupport.stream(server.getPlayerList().getPlayers().spliterator(), false);
        players.forEach(p -> {
            p.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorstand.getId()));
        });
    }


     /**
      * Get an NMS ItemStack from a Bukkit Material
      */
      private ItemStack getNmsItemStackFromMaterial(Material material) {
        String itemKey = "item."+material.getKey().toString().replace(":",".");
        String blockKey = "block."+material.getKey().toString().replace(":",".");
        if(headItem != null && (headItem.getItem().getName().equals(itemKey) || headItem.getItem().getName().equals(blockKey))) {
            return headItem;
        }
        for(Item item : Arrays.asList(Items.class.getFields()).stream().map(field -> {
            try {
                return (Item) field.get(null);
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }
            return null;
        }).toArray(Item[]::new)) {
            if (item == null) {
                continue;
            }
            if (item.getName().equals(itemKey) || item.getName().equals(blockKey)) {
                headItem = new ItemStack(item);
                return headItem;
            }

        }
        return new ItemStack(Items.AIR);  // Return an empty item if reflection fails  
    }
}
