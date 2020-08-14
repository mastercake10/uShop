package xyz.spaceio.ushop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.spaceio.customitem.CustomItem;
import xyz.spaceio.customitem.Flags;

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
			} else if (args[0].equalsIgnoreCase("add")) {
				if (args.length > 1) {
					if (!(cs instanceof Player)) {
						cs.sendMessage("You need to be a player!");
						return true;
					}
					Player p = (Player) cs;
					if (p.getInventory().getItemInMainHand() != null) {
						if (p.getInventory().getItemInMainHand().getType() != null) {
							if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
								// item holding
								ItemStack inHand = p.getInventory().getItemInMainHand();
								double price = Double.parseDouble(args[1]);
								
								
								CustomItem customItem = new CustomItem(p.getInventory().getItemInMainHand(), price);
			
								if(args.length > 2) {
									// handling flags
									for(int i = 2; i < args.length; i++) {
										String flagName = args[i].toUpperCase();
										try{
											Flags flag = Flags.valueOf(flagName);
											customItem.addFlag(flag);
										}catch(Exception e) {
											cs.sendMessage("§cFlag " + flagName + " not found. Valid flags are:");
											List<String> flags = Arrays.stream(Flags.values()).map(flag -> flag.name().toLowerCase()).collect(Collectors.toList());
											cs.sendMessage("§a" + String.join(", ", flags));
											return true;
										}
									}
								}
								
								Optional<CustomItem> result = plugin.findCustomItem(inHand);
								if(result.isPresent()) {
									plugin.getCustomItems().remove(result.get());
									p.sendMessage("§aSuccessfully updated item:");
								}else {
									p.sendMessage("§aSuccessfully added item:");
								}
								
								plugin.addCustomItem(customItem);
								plugin.saveMainConfig();
								
								p.sendMessage(plugin.getCustomItemDescription(customItem, 1).stream().toArray(String[]::new));
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
		cs.sendMessage("§e/ushop §areload §r- reloads the config");
		cs.sendMessage("§e/ushop §aadd <price> [flags ...] §r- sets a custom price for an item with custom lore, displayname, durability and enchants");
		cs.sendMessage("§e/ushop §aopen <player> §r- opens the shop for other players");
		cs.sendMessage("§e/ushop §aconvert §r- will convert your essentials worth list to the ushop one");
		cs.sendMessage("§cCurrently configured custom items (with NBT Data): §a" + plugin.getCustomItemCount());
	}

}
