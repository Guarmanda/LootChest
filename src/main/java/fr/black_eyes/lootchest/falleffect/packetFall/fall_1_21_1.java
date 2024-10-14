package fr.black_eyes.lootchest.falleffect.packetFall;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import fr.black_eyes.lootchest.Main;
import lombok.Getter;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 
Cette classe est destinée à faire apparaître et tomber un support d?armure avec un coffre dessus avec des paquets*/
public class Fall_1_21_1 {

    private final ClientboundAddEntityPacket spawnPacket;
    private final net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket dataPacket;
    private final ClientboundSetEquipmentPacket equipmentPacket;
    private final ArmorStand armorstand;
    @Getter private final Location location;
    private final int height;
    private final double speed;
    private long counter;


    public Fall_1_21_1(Location loc, Material headItem, int height, double speed) {
        this.speed = speed;
        this.height = height;
        this.location = loc;
        this.counter = (long) ((height / speed)*3);
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
        
        //int,UUID,double,double,double,float,float,EntityType<?>,int,Vec3,double
        spawnPacket = new ClientboundAddEntityPacket(
                stand.getId(),                                  // Entity ID
                UUID.randomUUID(),                                  // Unique ID
                loc.getX(), loc.getY(), loc.getZ(),                  // Position (X, Y, Z)
                loc.getYaw(), loc.getPitch(),                        // Rotation (Yaw, Pitch)
                stand.getType(),                               // Entity type (ArmorStand)
                0,                                                  // No specific motion (use 0 for no velocity)
                new Vec3(0, 0, 0),                                   // Velocity (none in this case)
                0.0);                                              // Head pitch
        dataPacket = new ClientboundSetEntityDataPacket(stand.getId(), stand.getEntityData().getNonDefaultValues());
    }

    //send to all players
    public void sendPacketToAll() {
        MinecraftServer server = MinecraftServer.getServer();
        Stream<ServerPlayer> players = StreamSupport.stream(server.getPlayerList().getPlayers().spliterator(), false);
        players.forEach(p -> {
            p.connection.send(spawnPacket);
            p.connection.send(dataPacket);
            p.connection.send(equipmentPacket);
            
            short y = (short)(speed *6000);
            new BukkitRunnable() {
					public void run() {
                        ClientboundMoveEntityPacket motionPacket = new ClientboundMoveEntityPacket.Pos(
                            armorstand.getId(),
                            (short) (0),  // Multiply by 4096 for correct movement scaling
                            (short) (-y), // Adjust Y for gravity/fall (lower Y for falling)
                            (short) (0),
                            true  // Yaw
                        ); 
                        p.connection.send(motionPacket);
                        counter--;
                        if(counter <= 0){
                            cancel();
                        }
					}
                    
				}.runTaskTimer(Main.getInstance(), 0, 2L);
        });
    }

    public void removePacket(Player p) {

      
    }

    public void removePacketToAll() {
       
    }
    //send packet with MOJANG nms
    public void sendPacket(org.bukkit.entity.Player p) {
        
    }



     // Convert Bukkit Material to Mojang NMS ItemStack using reflection
     private ItemStack getNmsItemStackFromMaterial(Material material) {
        try {
            // Get the field in the Items class corresponding to the material name
            Field itemField = Items.class.getField(material.name());
            Item nmsItem = (Item) itemField.get(null);  // Get the static field's value
            return new ItemStack(nmsItem);  // Create an NMS ItemStack
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();  // Handle errors (e.g., if the Material doesn't exist in Items)
            return ItemStack.EMPTY;  // Return an empty item if reflection fails
        }
    }


}