package net.obmc.OBMetaProducerBukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class OBMetaTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    
    public OBMetaTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }
	@Override
	public void run() {
        plugin.getServer().broadcastMessage("Welcome to OB-Minecraft! We play, we build!");
	}

	
}
