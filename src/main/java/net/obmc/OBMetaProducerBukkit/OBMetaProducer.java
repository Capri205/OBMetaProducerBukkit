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

import net.obmc.OBMetaProducerBukkit.OBMetaLoader;


public class OBMetaProducer extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");

	public OBMetaLoader mlc;
	private String metafile;
	private Boolean obsfucate;
	private Integer range;
	public static String program = "OBMetaProducerBukkit";
	
	PrintWriter pw = null;

	@Override
	public void onEnable() {
		//Bukkit.getLogger().info("[OBMetaProducer] Plugin loaded");
		
		if (!manageConfigs()) {
			return;
		}

		/**
		 * Initialize Stuff
		 */
		Initialize();
		/**
		 * Register stuff
		 */
		Register();
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
            	
        		// open the meta file for writing
        		File mfile = new File(metafile);
        		FileWriter writer;
        		try {
        			writer = new FileWriter(mfile, false);
        			pw = new PrintWriter(writer);
        		} catch(IOException e) {
        			e.printStackTrace();
        		}
            	
            	//log.log(Level.INFO, "testing testin 1 2 3");
        		/*
        		[{"msg":"x_goober_x","world":"world","dimension":"DIM0","x":0,"y":-4965,"z":70,"id":4},
        		 {"msg":"sean_ob","world":"world","dimension":"DIM0","x":0,"y":-5191,"z":86,"id":4}]
				*/
        		if (Bukkit.getServer().getOnlinePlayers().size() != 0 ) {
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
        				//pw.println(i.getName()+ ":" + i.getWorld().getName()+":"+i.getLocation().getX() + ":" + i.getLocation().getY() + ":" + i.getLocation().getZ());
        				pw.print(	"{\"msg\":\""+p.getName()+"\","+
        						"\"uuid\":\""+p.getUniqueId()+"\","+
            		            "\"world\":\""+p.getWorld().getName()+"\","+
    						    "\"dimension\":\""+p.getWorld().getEnvironment().getId()+"\","+
            		            "\"x\":\""+x+"\","+
    						    "\"y\":\""+y+"\","+
            		            "\"z\":\""+z+"\"}");
        				if (i < Bukkit.getServer().getOnlinePlayers().size() ) { pw.print(",");}
        			}
            	pw.print("]");
        		} else {
        			pw.print("[]");
        		}
           		pw.close();
            }
        }, 0L, 100L);
		

	}

	@Override
	public void onDisable() {
		
		//close file
		//pw.close();
		Bukkit.getLogger().info("[" + program + "] Unloaded");
	}


	/**
	 * Load default config
	 */
	public void loadConfig() {
		if (new File("plugins/" + program + "/config.yml").exists()) {
			//log.log(Level.INFO, "[OBMetaProducer] config.yml successfully loaded.");
		} else {
			saveDefaultConfig();
			log.log(Level.INFO, "[" + program + "] New config.yml has been created.");
		}
	}
	
	public boolean manageConfigs() {
		loadConfig();
		try {
			mlc = new OBMetaLoader(this);
		} catch (Exception e) {
			log.log(Level.WARNING, "[" + program + "] Error occurred while loading config.");
			e.printStackTrace();
			this.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		log.log(Level.INFO, "[" + program + "] Loaded configuration");
		return true;
	}

	public void Initialize() {
		Configuration config = this.getConfig();
		// Message stuff
		//this.messagesEnabled = config.getBoolean("MessageOptions.Enabled");
		this.metafile = config.getString("MetaFile.Filename");
        this.obsfucate = config.getBoolean("Options.Obsfucate");
        this.range = config.getInt("Options.Range");
        log.log(Level.INFO, "[" + program + "] Metadata output in "+metafile);
        log.log(Level.INFO, "[" + program + "] Player positon masking is set to "+obsfucate);
        if (obsfucate) {
        	log.log(Level.INFO, "[" + program + "] Masking range is "+range+" blocks");
        }
	}

	public void Register() {
//		wu = new WeaponUtil(this);
//		wl = new WeaponListener(this);
//		this.getCommand("mw").setExecutor(mwCE);
	}
}
