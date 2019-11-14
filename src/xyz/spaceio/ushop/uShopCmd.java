package xyz.spaceio.ushop;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class uShopCmd implements CommandExecutor {

	Main plugin;

	public uShopCmd(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if (!cs.hasPermission("ushop.admin")) {
			cs.sendMessage("§cYou dont have permissions to use this command!");
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				cs.sendMessage("§aConfig.yml reloaded!");
				plugin.reloadItems();
				return true;
			} else if (args[0].equalsIgnoreCase("setprice")) {
				if (args.length > 1) {
					if (!(cs instanceof Player)) {
						cs.sendMessage("You have to be a player!");
						return true;
					}
					Player p = (Player) cs;
					if (p.getInventory().getItemInMainHand() != null) {
						if (p.getInventory().getItemInMainHand().getType() != null) {
							if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
								// item holding
								ItemStack inHand = p.getInventory().getItemInMainHand();
								double price = Double.parseDouble(args[1]);
								
								Optional<CustomItem> result = plugin.findCustomItem(inHand);
								if(result.isPresent()) {
									plugin.getCustomItems().remove(result.get());
									p.sendMessage("§aSuccessfully updated item:");
								}else {
									p.sendMessage("§aSuccessfully added item:");
								}
								CustomItem i = new CustomItem(p.getInventory().getItemInMainHand(), price);
								plugin.addCustomItem(i);
								plugin.saveMainConfig();
								
								p.sendMessage(plugin.getCustomItemDescription(i, 1).stream().toArray(String[]::new));
								return true;
							}
						}
					}
					cs.sendMessage("§cYou need to hold an item in your hand!");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("open")) {
				if (args.length > 1) {
					String playername = args[1];
					Player p = Bukkit.getPlayer(playername);
					if(p != null) {
						plugin.openShop(p);
						cs.sendMessage("§cShop opened.");
						return true;
					}else {
						cs.sendMessage("§cPlayer is not online.");
						return true;
					}
				}else {
					cs.sendMessage("§cYou need to specify a playername in the command!");
					return true;
				}
			}else if (args[0].equalsIgnoreCase("convert")) {

				List<String> log = EssentialsWorthConverter.convert(plugin);
				cs.sendMessage(log.toArray(new String[0]));
				
				return true;
			}
				
		}

		showHelp(cs);

		return true;
	}

	private void showHelp(CommandSender cs) {
		cs.sendMessage("§c -- uShop v" + plugin.getDescription().getVersion() + " help: --");
		cs.sendMessage("§e/ushop §creload §r- reloads the config");
		cs.sendMessage("§e/ushop §csetprice <price> §r- sets a custom price for an item with custom lore, displayname, durability and enchants");
		cs.sendMessage("§e/ushop §copen <player> §r- opens the shop for other players");
		cs.sendMessage("§e/ushop §cconvert §r- will convert your essentials worth list to the ushop one");
		cs.sendMessage("§cCurrently configured custom items (with NBT Data): §a" + plugin.getCustomItemCount());
	}

}
