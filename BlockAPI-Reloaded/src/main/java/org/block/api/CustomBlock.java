package org.block.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.metadata.FixedMetadataValue;

public class CustomBlock
{
	private String identifier;
	private String name = "Custom Block";
	private String _materialName = "STONE";
	private int maxStackSize = 64;
	private ArrayList<Location> locs = new ArrayList<Location>();
	private List<Recipe> recipes = new ArrayList<Recipe>();
	private List<ItemStack> drops = new ArrayList<ItemStack>();
	private float hardness = 0.0F;
	private double _lootPercentageOnExplode = 0.0D;
	private double _lootPercentageOnDrop = 0.0D;

	private List<String> description = new ArrayList<String>();

	public CustomBlock()
	{
	}

	public CustomBlock(String id)
	{
		if (id == null)
			this.identifier = ("rand" + (int)(System.currentTimeMillis() / 10000L));
		else
			this.identifier = id;
		if (BlockAPI.plugin.isIdTaken(this.identifier))
			this.identifier = ("rand" + (int)(System.currentTimeMillis() / 10000L));
		setDrops(new ItemStack[] { BlockAPI.getItem(this) });
	}

	public String getIdentifier()
	{
		return this.identifier;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public final void handlePlace(PlayerInteractEvent event, BlockAPI plugin)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		BlockState state = block.getState();

		if ((!block.isEmpty()) && (!block.isLiquid())) {
			return;
		}
		if (player.getGameMode() != GameMode.CREATIVE) {
			item = BlockAPI.setCustomBlockAmount(event.getItem(), BlockAPI.getCustomBlockAmount(event.getItem()) - 1);
			player.setItemInHand(item);
		}
		block.setType(Material.getMaterial(getMaterialName().toUpperCase()));

		block.setMetadata(getIdentifier(), new FixedMetadataValue(plugin, Integer.valueOf(0)));
		BlockPlaceEvent event2 = new BlockPlaceEvent(block, state, event.getClickedBlock(), item, player, true);
		plugin.getServer().getPluginManager().callEvent(event2);
		place(event2);
		if (!event2.isCancelled())
			addLocation(block.getLocation());
	}

	public void place(BlockPlaceEvent event)
	{
	}

	public final void handleDestroy(BlockBreakEvent event)
	{
		destroy(event);
		if (event.isCancelled())
			return;
		event.setCancelled(true);
		Block block = event.getBlock();
		block.setType(Material.AIR);
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			for (Iterator<ItemStack> dropped = getDrops().iterator(); dropped.hasNext(); ) {
				ItemStack item = (ItemStack)dropped.next();
				ItemStack item2 = item.clone();
				int toDrop = 0;

				if (BlockAPI.getInstance().getCustomBlock(item) != null) {

					for (int x = 0; x < BlockAPI.getCustomBlockAmount(item); x++) {
						double gen = Math.random();
						if (gen < getLootPercentageOnExplode())
							toDrop++;
					}
					item2 = BlockAPI.setCustomBlockAmount(item2, toDrop);
				} else {					
					for (int x = 0; x < item.getAmount(); x++) {
						double gen = Math.random();
						if (gen < getLootPercentageOnExplode())
							toDrop++;
					}
					item2.setAmount(toDrop);
				}
				if (toDrop>0)
					block.getWorld().dropItemNaturally(block.getLocation(), item2);
			}
		}
		removeLocation(block.getLocation());
	}

	public void destroy(BlockBreakEvent event)
	{
	}

	public void power(BlockRedstoneEvent event)
	{
	}

	public void burn(BlockBurnEvent event)
	{
	}

	public void burning(BlockIgniteEvent event)
	{
	}

	public void damage(BlockDamageEvent event)
	{
	}

	public void walk(PlayerMoveEvent event)
	{
	}

	public void leftClick(PlayerInteractEvent event)
	{
	}

	public void rightClick(PlayerInteractEvent event)
	{
	}

	public void collide(VehicleBlockCollisionEvent event)
	{
	}

	public final void handleExplode(EntityExplodeEvent event, Block block)
	{
		exploded(event, block);
		if (event.isCancelled())	
			return;
		event.blockList().remove(block);

		if ((event.getYield() >= getHardness()) && (getHardness() >= 0.0F)) {
			removeLocation(block.getLocation());
			block.setType(Material.AIR);

			for (Iterator<ItemStack> dropped = getDrops().iterator(); dropped.hasNext(); ) {

				ItemStack item = (ItemStack)dropped.next();
				ItemStack item2 = item.clone();
				int toDrop = 0;

				if (BlockAPI.getInstance().getCustomBlock(item) != null) {
					for (int x = 0; x < BlockAPI.getCustomBlockAmount(item); x++) {
						double gen = Math.random();
						if (gen < getLootPercentageOnExplode())
							toDrop++;
					}
					item2 = BlockAPI.setCustomBlockAmount(item2, toDrop);
				} else {					
					for (int x = 0; x < item.getAmount(); x++) {
						double gen = Math.random();
						if (gen < getLootPercentageOnExplode())
							toDrop++;
					}
					item2.setAmount(toDrop);
				}
				if (toDrop>0)
					block.getWorld().dropItemNaturally(block.getLocation(), item2);
			}
		}
	}

	public void exploded(EntityExplodeEvent event, Block block)
	{
	}

	public void hanging_placed(HangingPlaceEvent event)
	{
	}

	public void projectile_hit(ProjectileHitEvent event, Block block)
	{
	}

	public void physics(BlockPhysicsEvent event)
	{
	}

	public Double getLootPercentageOnExplode()
	{
		return this._lootPercentageOnExplode;
	}

	public Double getLootPercentageOnDrop()
	{
		return this._lootPercentageOnDrop;
	}

	public String getMaterialName()
	{
		return this._materialName;
	}

	public int getMaxStackSize()
	{
		return this.maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize)
	{
		this.maxStackSize = maxStackSize;
	}

	public List<Recipe> getRecipes()
	{
		return this.recipes;
	}

	public void setRecipes(List<Recipe> list)
	{
		this.recipes = list;
	}

	public void addRecipe(Recipe recipe)
	{
		this.recipes.add(recipe);
	}

	public void setRecipes(Recipe[] recipes)
	{
		List<Recipe> list = new ArrayList<Recipe>();
		for (int x = 0; x < recipes.length; x++) {
			list.add(recipes[x]);
		}
		setRecipes(list);
	}

	public List<ItemStack> getDrops()
	{
		return this.drops;
	}

	public void setDrops(List<ItemStack> drops)
	{
		this.drops = drops;
	}

	public void addDrop(ItemStack item)
	{
		this.drops.add(item);
	}

	public void setDrops(ItemStack[] drops)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int x = 0; x < drops.length; x++) {
			list.add(drops[x]);
		}
		setDrops(list);
	}

	public float getHardness()
	{
		return this.hardness;
	}

	public void setHardness(float hardness)
	{
		this.hardness = hardness;
	}

	public List<String> getDescription()
	{
		return this.description;
	}

	public void setDescription(List<String> description)
	{
		this.description = description;
	}

	public void setDescription(String[] lines)
	{
		List<String> desc = new ArrayList<String>();
		for (int x = 0; x < lines.length; x++) {
			desc.add(lines[x]);
		}
		this.description = desc;
	}

	public void setLootPercentageOnExplode(Double lootPercentage)
	{
		if(lootPercentage >= 0.0D && lootPercentage <= 1.0D)
			this._lootPercentageOnExplode = lootPercentage;
	}

	public void setLootPercentageOnDrop(Double lootPercentage)
	{
		if(lootPercentage >= 0.0D && lootPercentage <= 1.0D)
			this._lootPercentageOnDrop = lootPercentage;
	}

	public void setMaterialName(String materialName)
	{
		if (Material.getMaterial(materialName.toUpperCase()) != null && Material.getMaterial(materialName.toUpperCase()).isBlock())
			this._materialName = materialName.toUpperCase();
	}

	public boolean equals(Block block)
	{
		return (block.getType().compareTo(Material.getMaterial(getMaterialName()))==0 && block.hasMetadata(getIdentifier()));
	}

	public final void addLocation(Location loc)
	{
		this.locs.add(loc);
	}

	public final void removeLocation(Location location)
	{
		Location remove = null;
		for (Iterator<Location> tocheck = getLocs().iterator(); tocheck.hasNext(); ) {
			Location loc = (Location)tocheck.next();
			boolean world = loc.getWorld().getName() == location.getWorld().getName();
			boolean x = loc.getBlockX() == location.getBlockX();
			boolean y = loc.getBlockY() == location.getBlockY();
			boolean z = loc.getBlockZ() == location.getBlockZ();
			if ((world) && (x) && (y) && (z)) {
				remove = loc;
				break;
			}
		}
		if (remove != null)
			this.locs.remove(remove);
	}

	public final ArrayList<Location> getLocs()
	{
		return this.locs;
	}
}