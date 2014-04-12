package org.block.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPersistence
implements Listener
{
	public static String folder = "./plugins/BlockAPI/";
	private BlockAPI plugin;

	public BlockPersistence(BlockAPI instance)
	{
		this.plugin = instance;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void world_load(WorldLoadEvent event) {
		World world = event.getWorld();
		load(world);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void world_save(WorldSaveEvent event) {
		World world = event.getWorld();
		save(world);
	}

	public void load(World world) {
		File file = new File(folder + world.getName() + ".data");
		if (!file.exists())
			return;
		String[] text = read(file).split("!");
		for (int x = 0; x < text.length; x++)
			if ((text[x] != "") && (text[x].contains(":")))
			{
				String metadata = text[x].split(":")[0];
				String s = text[x].replace(metadata + ":", "");
				Location loc = toLoc(world, s);
				if (loc != null)
				{
					CustomBlock block = this.plugin.getCustomBlock(metadata);
					if (block != null)
					{
						block.addLocation(loc);
						loc.getBlock().setMetadata(metadata, new FixedMetadataValue(this.plugin, Integer.valueOf(0))); } 
				}
			}
	}

	public void save(World world) { File file = new File(folder + world.getName() + ".data");
	if (!file.exists()) {
		if (!new File(folder).exists())
			new File(folder).mkdir();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	String s = "";
	for (Iterator<CustomBlock> blocks = this.plugin.getCustomBlocks().iterator(); blocks.hasNext(); ) {
		CustomBlock block = (CustomBlock)blocks.next();
		String identifier = block.getIdentifier() + ":";
		for (Iterator<Location> locs = block.getLocs().iterator(); locs.hasNext(); ) {
			Location loc = (Location)locs.next();
			if (loc.getWorld().getName() == world.getName())
			{
				s = s + identifier + toString(loc) + "!";
			}
		}
		s = s + "\n";
	}
	if (s.endsWith("!"))
		s = s.substring(0, s.length() - 1);
	write(file, s);
	}

	public static String read(File file)
	{
		String s = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
			{
				s = s + line;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static void write(File file, String s) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println(s);
		out.close();
	}

	public static String toString(Location loc) {
		return loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	}

	public static Location toLoc(World world, String s) {
		if ((s == "") || (!s.contains(":")))
			return null;
		String[] split = s.split(":");
		return new Location(world, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}
}