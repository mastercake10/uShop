package uShop;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_13_R1.WorldGenWoodlandMansionPieces.i;

public class CustomItem implements ConfigurationSerializable {

	private String material;

	private Map<Enchantment, Integer> enchantements;
	private String displayname;

	private boolean hasMeta = false;

	private short durability;
	private List<String> lore;

	private double price;

	public CustomItem(ItemStack is, double price) {
		this.price = price;
		this.material = is.getType().name();
		this.durability = is.getDurability();
		if (is.hasItemMeta()) {
			hasMeta = true;
			if (is.getItemMeta().hasDisplayName()) {
				this.displayname = is.getItemMeta().getDisplayName();
			}
			if (is.getItemMeta().hasLore()) {
				this.lore = is.getItemMeta().getLore();
			}
		}
		if (is.getEnchantments() != null) {
			this.enchantements = is.getEnchantments();
		}
	}

	/**
	 * Checks whether or not a real item stack equals to this custom item setup
	 * @param is
	 * @return
	 */
	public boolean matches(ItemStack is) {
		System.out.println("ci: " + is.getType().name());
		System.out.println("is: " + material);
		if (!is.getType().name().equals(material)) return false;
		
		if (is.hasItemMeta() != hasMeta) return false;
		
		if(is.getDurability() != durability) return false;
		
		if (!hasMeta) return true;
		
		if (hasMeta) {
			System.out.println("ci: " + displayname);
			System.out.println("is: " + is.getItemMeta().getDisplayName());
			
			if ((displayname == null && is.getItemMeta().getDisplayName() == null) || (displayname != null && is.getItemMeta().getDisplayName() != null && displayname.equals(is.getItemMeta().getDisplayName()))) {
			
				if(enchantements != null && is.getEnchantments().size() != 0) {
					int[] matches = {0};
					enchantements.forEach((enchantement, level) -> {
						if(is.getEnchantments().containsKey(enchantement)) {
							if(is.getEnchantments().get(enchantement) == level) {
								matches[0]++;		
							}
						}
					});
					if(matches[0] != is.getEnchantments().size()) {
						return false;
					}
				}
				if(lore != null && is.getItemMeta().getLore().size() != 0) {
					int[] matches = {0};
					lore.forEach((line) -> {
						if(is.getItemMeta().getLore().contains(line)) {
							matches[0]++;		
						}
					});
					if(matches[0] != is.getItemMeta().getLore().size()) {
						return false;
					}
				}
				return true;
				
			}
		}
		return false;
		
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		for(Field field : this.getClass().getDeclaredFields()) {
			try {
				map.put(field.getName(), field.get(this));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material.name();
	}

	public Map<Enchantment, Integer> getEnchantements() {
		return enchantements;
	}

	public void setEnchantements(Map<Enchantment, Integer> enchantements) {
		this.enchantements = enchantements;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public short getDurability() {
		return durability;
	}

	public void setDurability(short durability) {
		this.durability = durability;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
