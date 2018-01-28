package uShop;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CustomItem {
	Material mat;
	Map<Enchantment, Integer> enchants;
	String displayname;
	short damage;
	short durability;
	List<String> lore;
	
	double price;
	
	public CustomItem(ItemStack is, double price){
		this.price = price;
		mat = is.getType();
		if(is.hasItemMeta()){
			if(is.getItemMeta().hasDisplayName()){
				displayname = is.getItemMeta().getDisplayName();
			}
			if(is.getItemMeta().hasLore()){
				lore = is.getItemMeta().getLore();
			}
		}
		if(is.getEnchantments() != null){
			enchants = is.getEnchantments();
		}
	}
}
