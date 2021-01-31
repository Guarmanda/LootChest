package fr.black_eyes.lootchest.compatibilties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;


public class Worldguard
{

   
    public static boolean isInRegion(Location loc) {
    	RegionManager regions = null;
		Class<? extends RegionManager> cls = null; 
		Method getregion = null;
		try {
			if(Bukkit.getVersion().contains("1.13")|| Bukkit.getVersion().contains("1.14")|| Bukkit.getVersion().contains("1.15")|| Bukkit.getVersion().contains("1.16")) {
				com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();

				regions = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(loc.getWorld()));
				cls = regions.getClass();

				getregion = cls.getDeclaredMethod("getApplicableRegions",  com.sk89q.worldedit.math.BlockVector3.class);
			}else {
	    	    regions = com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager(loc.getWorld());
				cls = regions.getClass();

				getregion = cls.getDeclaredMethod("getApplicableRegions",  com.sk89q.worldedit.Vector.class);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		ApplicableRegionSet set = null;
		try {
			if(Bukkit.getVersion().contains("1.13")|| Bukkit.getVersion().contains("1.14")|| Bukkit.getVersion().contains("1.15")|| Bukkit.getVersion().contains("1.16")) {
				set = (ApplicableRegionSet) getregion.invoke(regions, com.sk89q.worldedit.math.BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
			}else {
				com.sk89q.worldedit.Vector v = com.sk89q.worldedit.bukkit.BukkitUtil.toVector(loc);
				set = (ApplicableRegionSet) getregion.invoke(regions, v);				
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return set.size()>0;

    }
}