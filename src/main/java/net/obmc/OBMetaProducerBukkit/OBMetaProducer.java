package net.obmc.OBMetaProducerBukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

	private String metafile;
	private Boolean obsfucate;
	private Integer range;
	private Boolean fault = false;
	public static String program = "OBMetaProducerBukkit";
	
	PrintWriter pw = null;

	@Override
	public void onEnable() {
		
		/**
		 * Initialize Stuff
		 */
		initializeStuff();
		/**
		 * Register stuff
		 */
		registerStuff();
		/**
		 * Output message
		 */
		log.log(Level.INFO, "[" + program + "] Plugin Version " + this.getDescription().getVersion() + " activated");

	
		// enable the task
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		
		//scheduler.scheduleSyncRepeatingTask(this, task, 0L, 200L);
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	
            	// indicates if we have an issue at any point
            	fault = false;
            	
        		// open the meta file for writing
        		File mfile = new File(metafile);
        		try {
					mfile.createNewFile();
				} catch (IOException e) {
					log.log(Level.INFO, "[" + program + "] Failed to create metafile: (" + metafile + ")");
					log.log(Level.INFO, "[" + program + "] Please check the path and filename in the config is correct");
					fault = true;
				}
        		FileWriter writer;
        		if (!fault) {
        			try {
        				writer = new FileWriter(mfile, false);
        				pw = new PrintWriter(writer);
        			} catch(IOException e) {
        				fault = true;
       					log.log(Level.INFO, "[" + program + "] No metafile found (" + metafile + ")");
        			}
        		}

        		// write out player world, dimension and location
        		if (Bukkit.getServer().getOnlinePlayers().size() != 0 && !fault) {
        			pw.print("[");
        			int i = 0;
        			for(Player p : Bukkit.getServer().getOnlinePlayers()){
        				i++;
        				//get player location
        				int x = (int) p.getLocation().getX(); int y = (int) p.getLocation().getY(); int z = (int) p.getLocation().getZ();

        				// add any required player position obsfucation
        				if (obsfucate) {
        					Random rand = new Random();
        					int xob = rand.nextInt(range); if (xob%2 == 0) {x+=xob;}else{x-=xob;} 
        					int zob = rand.nextInt(range); if (zob%2 == 0) {z+=zob;}else{z-=zob;}
        					int yob = rand.nextInt(40); if(yob%2 == 0) {y+=yob;}else{y-=yob;} if (y > 256) y=255; if (y<0) y=0; 
        				}
        				pw.print("{"+
        						"\"msg\":\""+p.getName()+"\","+
        						"\"uuid\":\""+p.getUniqueId()+"\","+
    						    "\"world\":\""+p.getWorld().getName()+"\","+
    						    "\"id\":\""+p.getWorld().getEnvironment().name()+"\","+
            		            "\"x\":\""+x+"\","+
    						    "\"y\":\""+y+"\","+
            		            "\"z\":\""+z+
            		            "\"}"
        				);
        				if (i < Bukkit.getServer().getOnlinePlayers().size() ) { pw.print(",");}
        			}
        			pw.print("]");
        		} else {
        			if (!fault) {
        				pw.print("[]");
        			}
        		}
        		if (!fault) {
        			pw.close();
        		}
            }
        }, 0L, 100L);
		

	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("[" + program + "] Unloaded");
	}


	/**
	 * Initialize Stuff
	 */
	public void initializeStuff() {
		this.saveDefaultConfig();
		Configuration config = this.getConfig();

		this.metafile = config.getString("MetaFile.Filename");
		this.obsfucate = config.getBoolean("Options.Obsfucate");
        this.range = config.getInt("Options.Range");
        log.log(Level.INFO, "[" + program + "] Metadata output in "+metafile);
        log.log(Level.INFO, "[" + program + "] Player positon masking is set to "+obsfucate);
        if (obsfucate) {
        	log.log(Level.INFO, "[" + program + "] Masking range is "+range+" blocks");
        }
	}
	
	public void registerStuff() {

	}
}
