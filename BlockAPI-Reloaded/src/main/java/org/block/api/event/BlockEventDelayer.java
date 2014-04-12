package org.block.api.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.block.api.BlockAPI;
import org.block.api.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;

public class BlockEventDelayer
implements Listener
{
	private BlockAPI plugin;

	public BlockEventDelayer(BlockAPI instance)
	{
		this.plugin = instance;
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void click(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null)
			return;
		Block i = event.getClickedBlock();
		if ((event.hasItem()) && (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			CustomBlock toPlace = this.plugin.getCustomBlock(event.getItem());
			if (toPlace != null) {
				toPlace.handlePlace(event, this.plugin);
				event.setUseItemInHand(Event.Result.DENY);
				event.setCancelled(true);
				return;
			}
		}
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			block.leftClick(event);
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			block.rightClick(event);
			return;
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void player_move(PlayerMoveEvent event) {
		Block i = event.getPlayer().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.walk(event);
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void block_break(BlockBreakEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.handleDestroy(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void block_damage(BlockDamageEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.damage(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void block_burn(BlockBurnEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.burn(event);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void block_ignite(BlockIgniteEvent event) {
		Block i = event.getBlock().getRelative(BlockFace.DOWN);
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.burning(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void block_power(BlockRedstoneEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.power(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void vehicle_collide(VehicleBlockCollisionEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.collide(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void hanging_place(HangingPlaceEvent event) {
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.hanging_placed(event);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void projectile_hit(ProjectileHitEvent event) {
		Block i = event.getEntity().getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.projectile_hit(event, i);
	}


	@EventHandler(priority=EventPriority.NORMAL)
	public void exploded(EntityExplodeEvent event) {

		Iterator<Block> i = event.blockList().iterator();
		List<Block> blockToHandleList= new LinkedList<Block> ();
		while(i.hasNext())
		{
			Block block = (Block)i.next();
			CustomBlock cblock = this.plugin.getCustomBlock(block);
			if (cblock != null)
			{
				blockToHandleList.add(block);
			}
		}
		Iterator<Block> iter = blockToHandleList.iterator();
		while(iter.hasNext())
		{
			Block block = (Block)iter.next();
			CustomBlock cblock = this.plugin.getCustomBlock(block);
			if (cblock != null)
			{
				cblock.handleExplode(event, block);
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void physics(BlockPhysicsEvent event) { 
		Block i = event.getBlock();
		CustomBlock block = this.plugin.getCustomBlock(i);
		if (block == null)
			return;
		block.physics(event);
	}
}
