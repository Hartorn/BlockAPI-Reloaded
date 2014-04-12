package org.block.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.block.api.event.BlockEventDelayer;
import org.block.api.event.RenderBlockItem;
import org.block.api.load.BlockLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockAPI extends JavaPlugin
{
	public static BlockAPI plugin;
	private Logger logger;
	private ArrayList<CustomBlock> customBlocks = new ArrayList<CustomBlock>();
	private BlockPersistence persist;

	public void onEnable()
	{
		this.logger = getLoggerSafely();

		plugin = this;

		this.persist = new BlockPersistence(this);
		getServer().getScheduler().runTask(plugin, new Runnable()
		{
			public void run()
			{
				for (Iterator<World> worlds = BlockAPI.this.getServer().getWorlds().iterator(); worlds.hasNext(); BlockAPI.this.persist.load((World)worlds.next()));
				String s = "Loaded custom blocks (" + BlockAPI.this.customBlocks.size() + ") : ";
				for (Iterator<CustomBlock> blocks = BlockAPI.this.customBlocks.iterator(); blocks.hasNext(); ) {
					CustomBlock cblock = (CustomBlock)blocks.next();
					s = s + cblock.getName() + ", ";
				}
				if (s.endsWith(", "))
					s = s.substring(0, s.lastIndexOf(", "));
				BlockAPI.this.logger.info(s);
			}
		});
		plugin.getServer().getPluginManager().registerEvents(this.persist, this);

		plugin.getServer().getPluginManager().registerEvents(new RenderBlockItem(this), this);

		plugin.getServer().getPluginManager().registerEvents(new BlockEventDelayer(this), this);

		addMyCustomBlocks(BlockLoader.load(plugin));

		this.logger.info("has been enabled");
	}

	public void onDisable()
	{
		for (Iterator<World> worlds = getServer().getWorlds().iterator(); worlds.hasNext(); this.persist.save((World)worlds.next()));
		this.logger.info("has been disabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("blocklist")) {
			String s = ChatColor.GOLD + "Custom Block List :";
			int x = 1;
			for (Iterator<CustomBlock> i = getCustomBlocks().iterator(); i.hasNext(); s = s + ChatColor.GOLD + "\n" + x++ + ". " + ((CustomBlock)i.next()).getName());
			player.sendMessage(s);
			return true;
		}
		else if ((cmd.getName().equalsIgnoreCase("getblock")) && (args.length >= 1)) {
			String name = args[0];

			boolean number = true;
			for (int x = 0; x < name.length(); x++) {
				char c = name.charAt(x);
				if ((c != '0') && (c != '1') && (c != '2') && (c != '3') && (c != '4') && (c != '5') && (c != '6') && (c != '7') && (c != '8') && (c != '9')) {
					number = false;
				}
			}
			int amount = 1;
			CustomBlock block = getCustomBlockFromName(name);
			if (number) {
				block = (CustomBlock) this.customBlocks.get(Integer.parseInt(name) - 1);
				if ((Integer.parseInt(name) - 1 >= this.customBlocks.size()) || (Integer.parseInt(name) - 1 < 0)) {
					player.sendMessage(ChatColor.RED + "No custom block with index : " + name + " was found.");
					return true;
				}
			}
			if (block == null) {
				player.sendMessage(ChatColor.RED + "No custom block with name : " + name + " was found.");
			} else {
				if (args.length == 2) {
					amount = Integer.parseInt(args[1]);
					if (amount > block.getMaxStackSize())
						amount = block.getMaxStackSize();
				}
				ItemStack item = setCustomBlockAmount(getItem(block), amount);
				player.getInventory().addItem(new ItemStack[] { item });
				player.sendMessage(ChatColor.GREEN + "You get " + block.getName() + " x" + amount);
			}
			return true;
		}
		return false;
	}

	private Logger getLoggerSafely()
	{
		Logger log = null;
		try {
			log = getLogger();
		}
		catch (Throwable localThrowable) {
		}
		if (log == null)
			log = Logger.getLogger("Minecraft");
		return log;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<CustomBlock> getCustomBlocks()
	{
		return (ArrayList<CustomBlock>) this.customBlocks.clone();
	}

	public boolean addMyCustomBlock(CustomBlock block)
	{
		if ((block == null) || (isIdTaken(block.getIdentifier())))
			return false;
		this.customBlocks.add(block);
		return true;
	}

	public int addMyCustomBlocks(CustomBlock[] blocks)
	{
		int failed = 0;
		for (int x = 0; x < blocks.length; x++) {
			boolean added = addMyCustomBlock(blocks[x]);
			if (!added)
				failed++;
		}
		return failed;
	}

	public int addMyCustomBlocks(List<CustomBlock> blockList)
	{
		if (blockList.isEmpty())
			return 0;
		int failed = 0;
		for (Iterator<CustomBlock> toadd = blockList.iterator(); toadd.hasNext(); ) {
			boolean added = addMyCustomBlock((CustomBlock)toadd.next());
			if (!added)
				failed++;
		}
		return failed;
	}

	public CustomBlock getCustomBlock(String identifier)
	{
		for (Iterator<CustomBlock> blocks = this.customBlocks.iterator(); blocks.hasNext(); ) {
			CustomBlock block = (CustomBlock)blocks.next();
			if (block.getIdentifier().equalsIgnoreCase(identifier))
				return block;
		}
		return null;
	}

	private CustomBlock getCustomBlockFromName(String name)
	{
		for (Iterator<CustomBlock> blocks = this.customBlocks.iterator(); blocks.hasNext(); ) {
			CustomBlock block = (CustomBlock)blocks.next();
			if (block.getName().equalsIgnoreCase(name))
				return block;
		}
		return null;
	}

	public CustomBlock getCustomBlock(ItemStack item)
	{
		if ((item == null) || (!item.hasItemMeta()))
			return null;
		ItemMeta meta = item.getItemMeta();
		if ((meta == null) || (!meta.hasDisplayName()))
			return null;
		String title = meta.getDisplayName();
		if ((title == null) || (!title.contains(" x ")))
			return null;
		String name = title.substring(0, title.indexOf(" x "));
		return getCustomBlockFromName(name);
	}

	public CustomBlock getCustomBlock(Block block)
	{
		for (Iterator<CustomBlock> blocks = this.customBlocks.iterator(); blocks.hasNext(); ) {
			CustomBlock cblock = (CustomBlock)blocks.next();
			if (cblock.equals(block))
				return cblock;
		}
		return null;
	}

	public boolean isIdTaken(String identifier)
	{
		return getCustomBlock(identifier) != null;
	}

	public void registerRecipe(Recipe[] recipes)
	{
		for (int x = 0; x < recipes.length; x++) {
			Recipe recipe = recipes[x];
			if (recipe != null)
				getServer().addRecipe(recipe);
		}
	}

	public static BlockAPI getInstance()
	{
		return plugin;
	}

	public static Block setBlock(CustomBlock cblock, Block block)
	{
		block.setType(Material.getMaterial(cblock.getMaterialName()));
		block.setMetadata(cblock.getIdentifier(), new FixedMetadataValue(plugin, Integer.valueOf(0)));
		cblock.addLocation(block.getLocation());
		return block;
	}

	public static ItemStack getItem(CustomBlock block)
	{
		if (block == null)
			return null;
		ItemStack item = new ItemStack(Material.getMaterial(block.getMaterialName()), 1);
		ItemMeta meta = null;
		if (!item.hasItemMeta())
			meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.getMaterial(block.getMaterialName()));
		else
			meta = item.getItemMeta();
		meta.setDisplayName(block.getName() + " x 1");
		meta.setLore(block.getDescription());
		item.setItemMeta(meta);
		return item;
	}

	public static int getCustomBlockAmount(ItemStack item)
	{
		if ((item == null) || (!item.hasItemMeta()))
			return -1;
		ItemMeta meta = item.getItemMeta();
		if ((meta == null) || (!meta.hasDisplayName()))
			return -1;
		String title = meta.getDisplayName();
		if ((title == null) || (!title.contains(" x ")))
			return -1;
		item.setAmount(1);
		int amount = Integer.parseInt(title.substring(title.indexOf(" x ") + 3));
		return amount;
	}

	public static ItemStack setCustomBlockAmount(ItemStack item, int amount)
	{
		if ((amount <= 0) || (item == null) || (!item.hasItemMeta()))
			return null;
		ItemMeta meta = item.getItemMeta();
		if ((meta == null) || (!meta.hasDisplayName()))
			return item;
		String title = meta.getDisplayName();
		if ((title == null) || (!title.contains(" x ")))
			return item;
		item.setAmount(1);
		String name = title.substring(0, title.indexOf(" x "));
		List<String> desc = meta.getLore();
		ItemStack item2 = item.clone();
		meta.setDisplayName(name + " x " + amount);
		meta.setLore(desc);
		item2.setItemMeta(meta);
		return item2;
	}
}
