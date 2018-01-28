package uShop;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class uShopCmd implements CommandExecutor {

	Plugin main;
	
	public uShopCmd(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if(!cs.hasPermission("ushop.admin")){
			cs.sendMessage("§cYou dont have permissions to use this command!");
			return true;
		}
		if(args.length > 0){
			if(args[0].equalsIgnoreCase("reload")){
				cs.sendMessage("§aConfig.yml reloaded!");
				((Main) main).reloadConfig2();
				return true;
			}else if(args[0].equalsIgnoreCase("setprice")){
				if(args.length > 1){
					if(!(cs instanceof Player)){
						cs.sendMessage("You have to be a player!");
						return true;
					}
					Player p = (Player) cs;
					if(p.getItemInHand() != null){
						if(p.getItemInHand().getType() != null){
							if(p.getItemInHand().getType() != Material.AIR){
								// item holding
								double price = Double.parseDouble(args[1]);
								CustomItem i = new CustomItem(p.getItemInHand(), price);
								Main.addCustomItem(i);
								cs.sendMessage("§aPrice set to " + price);
							}
						}
					}
					cs.sendMessage("§cYou need to hold an item in your hand!");
					return true;
				}
			}
		}
		
		showHelp(cs);
		
		return true;
	}

	private void showHelp(CommandSender cs) {
		cs.sendMessage("§uShop help:");
		cs.sendMessage("/ushop §creload §r- reloads the config");
		//cs.sendMessage("/ushop §csetprice <price> §r- sets a custom price for an item with custom lore, displayname, durability and enchants");
	}

}
