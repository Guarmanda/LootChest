package fr.black_eyes.lootchest.falleffect;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.util.Vector;

import fr.black_eyes.lootchest.Main;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import fr.black_eyes.simpleJavaPlugin.Utils;


public final class FallingPackageEntity {


    final World world;
    final Location startLoc;
    final Material material;
    Object blocky;
    boolean armorstand;
    final Location target;
    final double speed;
    final boolean fireworks;
    final int height;
    IFallPacket armorstandFall;
    private int counter = 0;
    
    public FallingPackageEntity(final Location loc, boolean loaded, Location target) {
    	Main main = Main.getInstance();
    	this.fireworks = Main.configs.fallEnableFireworks;
    	this.target = target;
        this.height = Main.configs.fallHeight;
    	this.armorstand = main.isUseArmorStands();
        this.blocky = null;
        this.armorstandFall = null;
        this.startLoc = loc.clone();
        this.world = loc.getWorld();
        this.material = Material.valueOf(Main.configs.fallBlock);
        this.speed = Main.configs.FALL_Speed;
        if (Bukkit.getVersion().contains("1.7")) {
        	this.armorstand = false;
        }
        if(loaded)
            this.summon();
    }
    

	@SuppressWarnings("deprecation")
    public void summon() {
        if(Main.getCompleteVersion() < 1083) this.armorstand = false;
        String version = Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
        try {
            Class.forName("fr.black_eyes.lootchest.falleffect.Fallv_" + version);
        }  catch (ClassNotFoundException e) {
            this.armorstand = false;
        }
		if(!this.armorstand) {
			this.blocky = this.world.spawnFallingBlock(startLoc, this.material, (byte)0);
		}else {	
            
            try {
                this.armorstandFall = (IFallPacket) Class.forName("fr.black_eyes.lootchest.falleffect.Fallv_" + version)
                        .getDeclaredConstructor(Location.class, Material.class, int.class, double.class, JavaPlugin.class)
                        .newInstance(startLoc, this.material, this.height, this.speed, Main.getInstance());
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                Utils.logInfo("&cError while creating the armorstand fall packet: " + ex.getMessage());
            }
            armorstandFall.sendPacketToAll();
		}
        if(fireworks) {
            this.summonSpawnFireworks();
        }
        this.tick();
    }
    
	public Location goodLocation() {
		if(!armorstand) return ((Entity) this.blocky).getLocation().clone();
		else {
			Location loc2 = armorstandFall.getLocation().clone();
			loc2.setY(loc2.getY()+3);
			return loc2;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void tick() {
        if(blocky!=null){
            Vector v = ((Entity) blocky).getVelocity();
            v.setY(-(speed));
            ((Entity) blocky).setVelocity(v);
        }
        Location locPackage = null;
        if (this.blocky != null) {
            locPackage = (!this.armorstand)? ((Entity) this.blocky).getLocation() : armorstandFall.getLocation();
        }

        if (this.world.getBlockAt(LocationUtils.offset(locPackage, 0.0, -1.0, 0.0)).getType() == Material.AIR) {
            ++this.counter;
            if(Main.getCompleteVersion() >= 1206 && Main.getInstance().getParticles().get("SMOKE") != null)
                Main.getInstance().getParticles().get("SMOKE").display((float)0.1, (float)0.1, (float)0.1, (float)0.1, 1,  goodLocation());
            else if (Main.getInstance().getParticles().get("SMOKE_NORMAL") != null)
			    Main.getInstance().getParticles().get("SMOKE_NORMAL").display((float)0.1, (float)0.1, (float)0.1, (float)0.1, 1,  goodLocation());
            if(!this.armorstand && ((Entity) this.blocky).isDead()) {
                final Vector oldVelocity = ((Entity) this.blocky).getVelocity().setY(-(speed));
                this.blocky = this.world.spawnFallingBlock(locPackage, this.material, (byte)0);
                ((Entity) (this.blocky)).setVelocity(oldVelocity);
            }

            if (this.counter % 5 == 0 && (   (locPackage.getY() - target.getY()) >3 || counter > 100) && fireworks ) {
                this.summonUpdateFireworks();
            }
            if((locPackage.getY() - target.getY()) <1) {
            	this.remove();
            }
            else if(counter < 100){
            	this.retick();
            }else {
            	this.remove();
            }
        }
        else {
        	 this.remove();
        }
    }
    
    public void remove() {
        if(!this.armorstand) {
        	((Entity) this.blocky).remove();
        }
    }
    
    private void summonUpdateFireworks() {
            final Firework fw; 
            if(Main.getCompleteVersion() < 1206) {
                fw = (Firework)this.world.spawnEntity(goodLocation(), EntityType.valueOf("FIREWORK"));
            }else {
                fw = (Firework)this.world.spawnEntity(goodLocation(), EntityType.valueOf("FIREWORK_ROCKET"));
            }
            final FireworkMeta fwm = fw.getFireworkMeta();
            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withColor(Color.WHITE).build());
            fwm.setPower(1);
            fw.setFireworkMeta(fwm);
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), fw::detonate, 1L);
    }
    
    private void summonSpawnFireworks() {
            final Firework fw; 
            if(Main.getCompleteVersion() < 1206) {
                fw = (Firework)this.world.spawnEntity(goodLocation(), EntityType.valueOf("FIREWORK"));
            }else {
                fw = (Firework)this.world.spawnEntity(goodLocation(), EntityType.valueOf("FIREWORK_ROCKET"));
            }
            final FireworkMeta fwm = fw.getFireworkMeta();
            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
            fwm.setPower(1);
            fw.setFireworkMeta(fwm);
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), fw::detonate, 1L);
    }

    private void retick() {
    	Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), this::tick, 1L);
    }
    

}
