package fr.black_eyes.lootchest.falleffect;

import java.lang.reflect.Constructor;

import org.bukkit.Location;
import org.bukkit.Material;

import fr.black_eyes.lootchest.Main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import fr.black_eyes.simpleJavaPlugin.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 1.17+ class to make an invisible armorstand fall from the sky with packets and a block on its head
 */
public class FallPacket {
    private ClientboundAddEntityPacket spawnPacket;
    private net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket dataPacket;
    private final ClientboundSetEquipmentPacket equipmentPacket;
    private final ClientboundMoveEntityPacket motionPacket;
    private final ArmorStand armorstand;
    private final Location startLocation;
    private final int height;
    private final double speed;
    private long counter;
    private final short SPEED_ONE_BLOCK_PER_SECOND = 410; // speed found after like 10 tests corresponding to one block fall per second
    private final long COUNTER_ONE_BLOCK = 10; // after 10*2 ticks at speed 410, the armorstand falls one block
    private final short SPEED_MULTIPLYER = 31; 
    private final JavaPlugin instance;

    /**
     * Get the actual location of the armorstand
     * Getting it with the entity class won't work because I move the armorstand, but only client sees it moving,
     * so I need to get the location from the start location and the counter
     * @return Location of the armorstand
     */
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
    public FallPacket(Location loc, Material headItem, int height, double speed) {
        this.instance = Main.getInstance();
        this.speed = speed;
        this.height = height;
        this.startLocation = loc;
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        
        // stream all levels and filter the one that matches the world name
        ServerLevel s = StreamSupport.stream(server.getAllLevels().spliterator(), false).filter(level -> level.getWorld().getName().equals(loc.getWorld().getName())).findFirst().orElse(null);

        ArmorStand stand = new ArmorStand(s, loc.getX(), loc.getY(), loc.getZ());  
        stand.setInvisible(true);
        stand.setNoBasePlate(true);
        armorstand = stand;
        
        List<Pair<EquipmentSlot, ItemStack>> equipmentList = Collections.singletonList(
            new Pair<>(EquipmentSlot.HEAD, getNmsItemStackFromMaterial(headItem))  // nmsHeadItem is your helmet ItemStack
        );

        equipmentPacket = new ClientboundSetEquipmentPacket(stand.getId(), equipmentList);
        
        // for the two next packets, there was changes between versions so we need reflection
        if(Main.getCompleteVersion() > 1182)
            try {
                Constructor<ClientboundAddEntityPacket> constructor = ClientboundAddEntityPacket.class.getConstructor(
                    int.class,        // Entity ID
                    UUID.class,       // Unique ID
                    double.class,     // Position X
                    double.class,     // Position Y
                    double.class,     // Position Z
                    float.class,      // Rotation Yaw
                    float.class,      // Rotation Pitch
                    EntityType.class, // Entity type
                    int.class,        // Motion (use 0 for no velocity)
                    Vec3.class,        // Velocity
                    Double.class     // Head pitch?
                );
                spawnPacket = constructor.newInstance(
                        stand.getId(), 
                        UUID.randomUUID(), 
                        loc.getX(), loc.getY(), loc.getZ(), 
                        loc.getYaw(), loc.getPitch(), 
                        stand.getType(), 
                        0, 
                        new Vec3(0, 0, 0),
                        0.0
                );  
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Utils.logInfo("Error while creating the spawn packet for the armorstand");
                e.printStackTrace();
            }                                         // Head pitch
        else{
            spawnPacket = new ClientboundAddEntityPacket(
                stand.getId(),                                  // Entity ID
                UUID.randomUUID(),                                  // Unique ID
                loc.getX(), loc.getY(), loc.getZ(),                  // Position (X, Y, Z)
                loc.getYaw(), loc.getPitch(),                        // Rotation (Yaw, Pitch)
                stand.getType(),                               // Entity type (ArmorStand)
                0,                                                  // No specific motion (use 0 for no velocity)
                new Vec3(0, 0, 0)                                   // Velocity (none in this case)
                );   
        }
        if(Main.getCompleteVersion() > 1192)
            // we will need reflection to get the entity data like this:
            try {
                //dataPacket = new ClientboundSetEntityDataPacket(stand.getId(), stand.getEntityData().getNonDefaultValues());
                Constructor<ClientboundSetEntityDataPacket> constructor = ClientboundSetEntityDataPacket.class.getConstructor(int.class, List.class);
                // the getNonDefaultValues method doesn't exist in this version, we need reflection to execute it
                Method getNonDefaultValues = SynchedEntityData.class.getMethod("getNonDefaultValues");
                //dataPacket = constructor.newInstance(stand.getId(), stand.getEntityData().getNonDefaultValues(), true);
                dataPacket = constructor.newInstance(stand.getId(), getNonDefaultValues.invoke(stand.getEntityData()));
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Utils.logInfo("Error while creating the data packet for the armorstand");
                e.printStackTrace();
            }
            
        else {
            dataPacket = new ClientboundSetEntityDataPacket(stand.getId(), stand.getEntityData(), true);
        }
        short new_speed = (short)(this.speed*SPEED_MULTIPLYER*SPEED_ONE_BLOCK_PER_SECOND); // the plugin had a default speed of 0.8 wich was quite fast, but it was never meaningful, 0.8 was like 5 blocks per seconds.
        // divide the counter by the speed multiplyer to get the number of ticks the armorstand will need to fall to get the fall ticks of one block for the new speed, then multiply it by the total height to fall
        counter = (int)((COUNTER_ONE_BLOCK/(this.speed*SPEED_MULTIPLYER))*(height+3));
        // I added 3 to height, else packet is removed too fast
        motionPacket = new ClientboundMoveEntityPacket.Pos(
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
    public void sendPacketToAll() {
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        Stream<ServerPlayer> players = StreamSupport.stream(server.getPlayerList().getPlayers().spliterator(), false);
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
    public void removePacketToAll() {
        @SuppressWarnings("deprecation")
        MinecraftServer server = MinecraftServer.getServer();
        Stream<ServerPlayer> players = StreamSupport.stream(server.getPlayerList().getPlayers().spliterator(), false);
        players.forEach(p -> {
            p.connection.send(new ClientboundRemoveEntitiesPacket(armorstand.getId()));
        });
    }


     /**
      * Get an NMS ItemStack from a Bukkit Material
      */
     private ItemStack getNmsItemStackFromMaterial(Material material) {
        try {
            // Get the field in the Items class corresponding to the material name
            Field itemField = Items.class.getField(material.name());
            Item nmsItem = (Item) itemField.get(null);  // Get the static field's value
            return new ItemStack(nmsItem);  // Create an NMS ItemStack
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return ItemStack.EMPTY;  // Return an empty item if reflection fails
        }
    }
}
