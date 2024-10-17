package fr.black_eyes.lootchest.falleffect;

import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
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

import fr.black_eyes.simpleJavaPlugin.Utils;


public class FallingPackageEntity {


    World world;
    Location startLoc;
    Material material;
    Object blocky;
    Boolean armorstand;
    Location target;
    Double speed;
    Boolean fireworks;
    Integer height;
    FallPacket armorstandFall;
    private int counter = 0;
    
    public FallingPackageEntity(final Location loc, Boolean loaded,Location target) {
    	Main main = Main.getInstance();
    	this.fireworks = Main.configs.FALL_Enable_Fireworks;
    	this.target = target;
        this.height = Main.configs.FALL_Height;
    	this.armorstand = main.getUseArmorStands();
        this.blocky = null;
        this.armorstandFall = null;
        this.startLoc = loc.clone();
        this.world = loc.getWorld();
        this.material = Material.valueOf(Main.configs.FALL_Block);
        this.speed = Main.configs.FALL_Speed;
        if (Bukkit.getVersion().contains("1.7")) {
        	this.armorstand = false;
        }
        if(loaded)
            this.summon();
    }

    private interface FallPacket{
        public void sendPacketToAll();
        public Location getLocation();
    }
    

	@SuppressWarnings("deprecation")
    public void summon() {
        if(Main.getCompleteVersion() < 1170) this.armorstand = false;
		if(!this.armorstand) {
			this.blocky = this.world.spawnFallingBlock(startLoc, this.material, (byte)0);
		}else {	
            Utils.logInfo(Main.getCompleteVersion()+"");
            if(Main.getCompleteVersion() >= 1194)
                this.armorstandFall = (FallPacket)new FallPacket_1_19_4(startLoc, this.material, height, speed, Main.getInstance());
            else if(Main.getCompleteVersion() >= 1182)
                this.armorstandFall = (FallPacket)new FallPacket_1_18_2(startLoc, this.material, height, speed, Main.getInstance());
            else
                this.armorstandFall = (FallPacket)new FallPacket_1_17(startLoc, this.material, height, speed, Main.getInstance());
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
        Location locPackage = (!this.armorstand)? ((Entity) this.blocky).getLocation() : armorstandFall.getLocation();
        
		if (this.world.getBlockAt(LocationUtils.offset(locPackage, 0.0, -1.0, 0.0)).getType() == Material.AIR) {
            ++this.counter;
            if(Main.getCompleteVersion() >= 1206)
                Main.getInstance().getParticles().get("SMOKE").display((float)0.1, (float)0.1, (float)0.1, (float)0.1, 1,  goodLocation(), (float)50.0);
            else
			    Main.getInstance().getParticles().get("SMOKE_NORMAL").display((float)0.1, (float)0.1, (float)0.1, (float)0.1, 1,  goodLocation(), (float)50.0);
            if(!this.armorstand) {
                if (((Entity) this.blocky).isDead()) {
                    final Location oldLoc = locPackage;
                    final Vector oldVelocity = ((Entity) this.blocky).getVelocity().setY(-(speed));
                    ((Entity) (this.blocky = this.world.spawnFallingBlock(oldLoc, this.material, (byte)0))).setVelocity(oldVelocity);
                }
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
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }, 1L);
        //}
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
            
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }, 1L);
        //}
    }

    protected void retick() {
    	Main.getInstance().getServer().getScheduler().runTaskLater((Plugin)Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                tick();
            }
        }, 1L);
    }
    

}
