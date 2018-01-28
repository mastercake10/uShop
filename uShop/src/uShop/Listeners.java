package uShop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {
	HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	@EventHandler
	public void onClick(final InventoryClickEvent e){
		if(e.getInventory() != null){
			if(e.getInventory().getTitle() != null){
				if(e.getInventory().getTitle().equals(Main.cfg.getString("gui-name").replace("&", "§"))){
					if(e.getCurrentItem() != null){
						
						if(e.getSlot() == e.getInventory().getSize() - 5){
							e.setCancelled(true);
							e.setResult(Result.DENY);
							
							final Player p = (Player) e.getInventory().getViewers().get(0);
							
							if(cooldowns.containsKey(p.getName())){
								if(cooldowns.get(p.getName()) + 2000 > System.currentTimeMillis()){
									return;
								}
							}
							if(e.getClick() != ClickType.SHIFT_RIGHT && e.getClick() != ClickType.SHIFT_LEFT){
								//REMOVE SELL ITEM
								e.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
								if(e.getCursor() != null){
									e.getInventory().addItem(e.getCursor());
									e.setCursor(null);
								}
								//SELL
								double total = Main.calcPrices(e.getInventory().getContents());
								Main.economy.depositPlayer(p, total);
								p.sendMessage(Main.cfg.getString("message-sold").replace('&', '§').replace("%total%", Main.economy.format(total)));
								
								cooldowns.put(p.getName(), System.currentTimeMillis());
								e.getInventory().clear();
								//Run later because the inventory bugs if closed immediately.
								Bukkit.getScheduler().runTaskLaterAsynchronously(Main.pl, new Runnable(){
									public void run(){
										p.closeInventory();
										p.updateInventory();
									}
								}, 5L);	
							}
						}
					}
				}
			}
		}
		
		
	}
}
