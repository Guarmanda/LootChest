// 
// Decompiled by Procyon v0.5.29
// 

package fr.black_eyes.lootchest.falleffect;

import org.bukkit.plugin.Plugin;

import fr.black_eyes.lootchest.Main;


public class PackageEntity
{
    protected int counter;
    
    public PackageEntity() {
        this.counter = 0;
    }
    
    public void summon() {
    }
    
    public void remove() {
    }
    
    protected void tick() {
    }
    
    protected void retick() {
    	Main.getInstance().getServer().getScheduler().runTaskLater((Plugin)Main.getInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                PackageEntity.this.tick();
            }
        }, 1L);
    }
}
