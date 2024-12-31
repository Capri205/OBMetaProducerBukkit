package net.obmc.OBMetaProducerBukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class OBMetaProducer extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");

	private static String plugin = "OBMetaProducer";
	private static String pluginPrefix = "[" + plugin + "]";
	private static String logMsgPrefix = pluginPrefix + " » ";

    private String metaFileName;
    private File metaFile;
	private Boolean obsfucate;
	private Integer range;
	private Boolean fault = false;

	@Override
	public void onEnable() {

		loadConfig();

		log.log(Level.INFO, logMsgPrefix + "Plugin Version " + this.getDescription().getVersion() + " activated");
		metaFile = getMetafile(this.metaFileName);
		try {
		    metaFile.createNewFile();
		} catch (IOException e) {
		    log.log(Level.INFO, logMsgPrefix + "Failed to create metafile: (" + metaFile + ")");
		    fault = true;
		}

		// enable the task
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {

            if (fault) return;
            	    
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile, false))) {
                    
                if (Bukkit.getServer().getOnlinePlayers().size() != 0) {

                    writer.write("[");
                    int i = 0;
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        i++;
                        int x = (int) player.getLocation().getX();
                        int y = (int) player.getLocation().getY();
                        int z = (int) player.getLocation().getZ();
                                
                        // add any required player position obsfucation
                        if (obsfucate) {
                            Random rand = new Random();
                            int xob = rand.nextInt(range); if (xob%2 == 0) {x+=xob;}else{x-=xob;} 
                            int zob = rand.nextInt(range); if (zob%2 == 0) {z+=zob;}else{z-=zob;}
                            int yob = rand.nextInt(40); if(yob%2 == 0) {y+=yob;}else{y-=yob;} if (y > 256) y=255; if (y<0) y=0; 
                        }

                        writer.write("{\"sys\":\"spigot\",\"msg\":\"" + player.getName() +
                            "\",\"uuid\":\"" + player.getUniqueId() + "\"," + "\"world\":\"" +
                            player.getWorld().getName() + "\",\"id\":\"" +
                            player.getWorld().getEnvironment().name() + "\"," + "\"x\":\"" +
                            x + "\",\"y\":\"" + y + "\",\"z\":\"" + z + "\"}");
            	            
                        if (i < Bukkit.getServer().getOnlinePlayers().size()) {
                            writer.write(",");
                        }
                    }
                    writer.write("]");
                            
                } else {
                    writer.write("[]");
                }

                writer.flush();

            } catch(IOException e) {
                log.log(Level.SEVERE, logMsgPrefix + "Failed to write to metafile: (" + metaFile + ")");
                fault = true;
            }
        }, 0L, 100L);
	}

	@Override
	public void onDisable() {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile, false))) {
	        writer.write("[]");
		} catch (IOException e) {
		    log.log(Level.SEVERE, logMsgPrefix + "Failed to write to metafile on plugin disable");
			e.printStackTrace();
		}
		Bukkit.getLogger().info(logMsgPrefix + "Unloaded");
	}

	public void loadConfig() {
		this.saveDefaultConfig();
		Configuration config = this.getConfig();
	    this.metaFileName = config.getString("metafile.name") != null ? config.getString("metafile.name") : "OBMetaProducer.dat";
		this.obsfucate = config.getBoolean("options.obsfucate") ? config.getBoolean("options.obsfucate") : false;
        this.range = config.getInt("options.range") != 0 ? config.getInt("options.range") : 1000;
        log.log(Level.INFO, logMsgPrefix + "Metadata output in "+metaFileName);
        log.log(Level.INFO, logMsgPrefix + "Player positon masking is set to "+obsfucate);
        if (obsfucate) {
        	log.log(Level.INFO, logMsgPrefix + "Masking range is "+range+" blocks");
        }
	}
	
	 private File getMetafile(String path) {
	     File file = new File(path);
	     if (!file.isAbsolute()) {
	         file = new File(getDataFolder(), path);
	     }
         return file;
     }
}
