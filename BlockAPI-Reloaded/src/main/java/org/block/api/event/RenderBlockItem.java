package org.block.api.event;

import java.util.Iterator;
import java.util.List;
import org.block.api.BlockAPI;
import org.block.api.CustomBlock;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RenderBlockItem
implements Listener
{
	private BlockAPI plugin;

	public RenderBlockItem(BlockAPI instance)
	{
		this.plugin = instance;
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void inventory_click(InventoryClickEvent event) {
		if (event.getCurrentItem() == null)
			return;
		ItemStack item = event.getCurrentItem();

		if (event.isRightClick()) {
			int amount = BlockAPI.getCustomBlockAmount(item);
			if (amount == -1)
				return;
			event.setResult(Event.Result.DENY);
			amount /= 2;
			int amount2 = BlockAPI.getCustomBlockAmount(item) - amount;
			event.getView().setCursor(BlockAPI.setCustomBlockAmount(item, amount));
			event.getView().setItem(event.getRawSlot(), BlockAPI.setCustomBlockAmount(item, amount2));
		} else if ((event.getCursor() != null) && (event.isLeftClick()))
		{
			if (event.isShiftClick()) {
				InventoryView inv = event.getView();
				if ((inv.getType() == InventoryType.WORKBENCH) || (inv.getType() == InventoryType.CRAFTING)) {
					CraftingInventory bench = (CraftingInventory)inv.getTopInventory();
					Recipe rec = bench.getRecipe();

					if ((rec == null) || (this.plugin.getCustomBlock(rec.getResult()) == null)) {
						int slot = event.getRawSlot();
						int quantity = BlockAPI.getCustomBlockAmount(item);
						CustomBlock cblock = this.plugin.getCustomBlock(item);
						if ((quantity == -1) || (cblock == null))
							return;
						for (int x = 0; x < inv.countSlots(); x++) {
							ItemStack itemX = inv.getItem(x);
							if (itemX != null)
							{
								int amount = BlockAPI.getCustomBlockAmount(itemX);
								if ((amount != -1) && (amount < cblock.getMaxStackSize()) && 
										(cblock.getIdentifier()
												.equalsIgnoreCase(this.plugin.getCustomBlock(itemX).getIdentifier())))
								{
									int test = amount + quantity;
									if (test <= cblock.getMaxStackSize()) {
										quantity = 0;
										inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, test));
										break;
									}
									quantity = test - cblock.getMaxStackSize();
									inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, cblock.getMaxStackSize()));
								}
							}
						}
						event.setResult(Event.Result.DENY);
						inv.setItem(slot, BlockAPI.setCustomBlockAmount(item, quantity));
						return;
					}
					if ((rec instanceof ShapelessRecipe)) {
						ShapelessRecipe recipe = (ShapelessRecipe)rec;
						List<ItemStack> matrix = recipe.getIngredientList();
						int num = this.plugin.getCustomBlock(rec.getResult()).getMaxStackSize();
						ItemStack[] current = bench.getMatrix();
						for (int x = 0; x < current.length; x++) {
							ItemStack matrixed = current[x];
							if (matrixed != null)
							{
								for (Iterator<ItemStack> i = matrix.iterator(); i.hasNext(); ) {
									ItemStack t = (ItemStack)i.next();
									if (matrixed.getType() == t.getType()) {
										int max = (int)Math.floor(matrixed.getAmount() / t.getAmount());
										num = Math.min(num, max);
									}
								}
							}
						}
						for (int x = 0; x < current.length; x++) {
							ItemStack matrixed = current[x];
							if (matrixed != null)
							{
								for (Iterator<ItemStack> i = matrix.iterator(); i.hasNext(); ) {
									ItemStack t = (ItemStack)i.next();
									if (matrixed.getType() == t.getType()) {
										matrixed.setAmount(matrixed.getAmount() - t.getAmount() * num);
										current[x] = matrixed;
									}
								}
							}
						}
						bench.setMatrix(current);
						inv.setCursor(BlockAPI.setCustomBlockAmount(recipe.getResult(), num));
						bench.setResult(null);
						event.setResult(Event.Result.DENY);
					} else if ((rec instanceof ShapedRecipe))
					{
						ShapedRecipe recipe = (ShapedRecipe)rec;
						List<ItemStack> matrix = (List<ItemStack>)recipe.getIngredientMap().values();
						int num = this.plugin.getCustomBlock(rec.getResult()).getMaxStackSize();
						ItemStack[] current = bench.getMatrix();
						for (int x = 0; x < current.length; x++) {
							ItemStack matrixed = current[x];
							if (matrixed != null)
							{
								for (Iterator<ItemStack> i = matrix.iterator(); i.hasNext(); ) {
									ItemStack t = (ItemStack)i.next();
									if (matrixed.getType() == t.getType()) {
										int max = (int)Math.floor(matrixed.getAmount() / t.getAmount());
										num = Math.min(num, max);
									}
								}
							}
						}
						for (int x = 0; x < current.length; x++) {
							ItemStack matrixed = current[x];
							if (matrixed != null)
							{
								for (Iterator<ItemStack> i = matrix.iterator(); i.hasNext(); ) {
									ItemStack t = (ItemStack)i.next();
									if (matrixed.getType() == t.getType()) {
										matrixed.setAmount(matrixed.getAmount() - t.getAmount() * num);
										current[x] = matrixed;
									}
								}
							}
						}
						bench.setMatrix(current);
						inv.setCursor(BlockAPI.setCustomBlockAmount(recipe.getResult(), num));
						bench.setResult(null);
						event.setResult(Event.Result.DENY);
					}
				}
				else {
					int slot = event.getRawSlot();
					int quantity = BlockAPI.getCustomBlockAmount(item);
					CustomBlock cblock = this.plugin.getCustomBlock(item);
					if ((quantity == -1) || (cblock == null))
						return;
					for (int x = 0; x < inv.countSlots(); x++) {
						ItemStack itemX = inv.getItem(x);
						if (itemX != null)
						{
							int amount = BlockAPI.getCustomBlockAmount(itemX);
							if ((amount != -1) && (amount < cblock.getMaxStackSize()) && 
									(cblock.getIdentifier()
											.equalsIgnoreCase(this.plugin.getCustomBlock(itemX).getIdentifier())))
							{
								int test = amount + quantity;
								if (test <= cblock.getMaxStackSize()) {
									quantity = 0;
									inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, test));
									break;
								}
								quantity = test - cblock.getMaxStackSize();
								inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, cblock.getMaxStackSize()));
							}
						}
					}
					event.setResult(Event.Result.DENY);
					inv.setItem(slot, BlockAPI.setCustomBlockAmount(item, quantity));
				}

			}
			else if (item != null) {
				int amount = BlockAPI.getCustomBlockAmount(item);
				int amount2 = BlockAPI.getCustomBlockAmount(event.getCursor());
				CustomBlock cblock = this.plugin.getCustomBlock(item);
				if ((amount == -1) || (amount2 == -1) || (cblock == null) || (!cblock.getIdentifier().equalsIgnoreCase(this.plugin.getCustomBlock(event.getCursor()).getIdentifier())))
					return;
				if (amount + amount2 > cblock.getMaxStackSize()) {
					amount2 = amount + amount2 - cblock.getMaxStackSize();
					amount = cblock.getMaxStackSize();
					event.getView().setCursor(BlockAPI.setCustomBlockAmount(item, amount2));
					event.getView().setItem(event.getRawSlot(), BlockAPI.setCustomBlockAmount(item, amount));
				} else {
					event.getView().setCursor(null);
					event.getView().setItem(event.getRawSlot(), BlockAPI.setCustomBlockAmount(item, amount + amount2));
				}
				event.setResult(Event.Result.DENY);
			}
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void pickup(PlayerPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();
		item.setAmount(item.getAmount() - event.getRemaining());
		Inventory inv = event.getPlayer().getInventory();
		int quantity = BlockAPI.getCustomBlockAmount(item) * item.getAmount();
		CustomBlock cblock = this.plugin.getCustomBlock(item);
		if ((quantity == -1) || (cblock == null))
			return;
		for (int x = 0; x < inv.getSize(); x++) {
			ItemStack itemX = inv.getItem(x);
			if (itemX != null)
			{
				int amount = BlockAPI.getCustomBlockAmount(itemX);
				if ((amount != -1) && (amount < cblock.getMaxStackSize()) && (cblock.getIdentifier().equalsIgnoreCase(this.plugin.getCustomBlock(itemX).getIdentifier())))
				{
					int test = amount + quantity;
					if (test <= cblock.getMaxStackSize()) {
						quantity = 0;
						inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, test));
						break;
					}
					quantity = test - cblock.getMaxStackSize();
					inv.setItem(x, BlockAPI.setCustomBlockAmount(itemX, cblock.getMaxStackSize()));
				}
			}
		}
		if (quantity > 0) {
			item = BlockAPI.setCustomBlockAmount(item, quantity);
			item.setAmount(1);
			event.getPlayer().getInventory().addItem(new ItemStack[] { item });
		}
		event.getItem().remove();
		event.setCancelled(true);
	}
}
