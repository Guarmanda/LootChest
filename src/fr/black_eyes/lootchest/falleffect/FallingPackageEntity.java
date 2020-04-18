package fr.black_eyes.lootchest.falleffect;

import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import static org.inventivetalent.reflection.minecraft.Minecraft.Version.v1_12_R1;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;
import org.inventivetalent.reflection.minecraft.Minecraft;

import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Main;


import org.bukkit.entity.FallingBlock;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;

public class FallingPackageEntity extends PackageEntity {
	Main instance = Main.getInstance();
	Config config = Main.getConfigFiles();
    World world;
    Location startLoc;
    Material material;
    FallingBlock blocky;
    
    public FallingPackageEntity(final Location loc) {
        this.blocky = null;
        this.startLoc = this.applyOffset(loc);
        this.world = loc.getWorld();
        this.material = Material.valueOf(config.getConfig().getString("Fall_Effect_Block"));
        this.summon();
    }
    

	@SuppressWarnings("deprecation")
	@Override
    public void summon() {
        this.blocky = this.world.spawnFallingBlock(startLoc, this.material, (byte)0);
        this.summonSpawnFireworks();
        this.tick();
    }
    

	@SuppressWarnings("deprecation")
	public void tick() {
		if(this.counter > 58) {
        	this.remove();
        }
		else if (this.world.getBlockAt(LocationUtils.offset(this.blocky.getLocation(), 0.0, -1.0, 0.0)).getType() == Material.AIR) {
            ++this.counter;
            if(!Bukkit.getVersion().contains("1.8")) {
				if(Minecraft.VERSION.newerThan(v1_12_R1)) {
		           	this.world.spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, this.blocky.getLocation(), 50, 0.1, 0.1, 0.1, 0.1);
				}				
				else{
				 ParticleEffect.SMOKE_NORMAL.send(this.blocky.getLocation().getWorld().getPlayers(), this.blocky.getLocation(), 0.1, 0.1, 0.1, 0.1, 50, 100);			
				}
            }
            if (this.blocky.isDead()) {
                final Location oldLoc = this.blocky.getLocation();
                final Vector oldVelocity = this.blocky.getVelocity();
                (this.blocky = this.world.spawnFallingBlock(oldLoc, this.material, (byte)0)).setVelocity(oldVelocity);
            }
            if (this.counter % 5 == 0 && this.counter<59) {
                this.summonUpdateFireworks();
            }
            if(this.counter > 58) {
            	this.remove();
            }
            else {
            	this.retick();
            }
        }
        else {
            this.remove();
        }
    }
    
    @Override
    public void remove() {
        this.blocky.remove();

    }
    
    private void summonUpdateFireworks() {
        //if (Main.getInstance().getConfig().getBoolean("options.fireworks_on_fall")) {
            final Firework fw = (Firework)this.world.spawnEntity(this.blocky.getLocation(), EntityType.FIREWORK);
            final FireworkMeta fwm = fw.getFireworkMeta();
            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withColor(Color.WHITE).build());
            fw.setFireworkMeta(fwm);
            Main.getInstance().getServer().getScheduler().runTaskLater((Plugin)Main.getInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }, 1L);
        //}
    }
    
    private void summonSpawnFireworks() {
        //if (Main.getInstance().getConfig().getBoolean("options.fireworks_on_fall")) {
            final Firework fw = (Firework)this.world.spawnEntity(this.blocky.getLocation(), EntityType.FIREWORK);
            final FireworkMeta fwm = fw.getFireworkMeta();
            fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
            fw.setFireworkMeta(fwm);
            Main.getInstance().getServer().getScheduler().runTaskLater((Plugin)Main.getInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    fw.detonate();
                }
            }, 1L);
        //}
    }
    
    private Location applyOffset(final Location loc) {
    	return loc;
    	/*
        final int bounds = Main.getInstance().getConfig().getInt("options.drop_location_offset");
        if (bounds < 1) {
            return loc;
        }
        final Random r = new Random();
        final int zOff = r.nextInt(bounds * 2) + 1 - bounds;
        final int xOff = r.nextInt(bounds * 2) + 1 - bounds;
        return LocationUtils.offset(loc, xOff, 0.0, zOff);
        */
    }
}
