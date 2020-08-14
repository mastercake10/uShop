package xyz.spaceio.customitem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CustomItem implements ConfigurationSerializable {

	private String material;

	private Map<String, Integer> enchantements;
	private String displayname;

	private boolean hasMeta = false;

	private short durability;
	private List<String> lore;

	private double price;
	
	private List<Flags> flags = new ArrayList<Flags>();

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
			// transforming enchantments to the name space of the corresponding enchantment
			this.enchantements = is.getEnchantments().entrySet().stream().collect(Collectors.toMap(x -> x.getKey().getKey().getKey(), x -> x.getValue()));
		}
	}
	
	/**
	 * Returns if this custom item has an item meta and enchantment on it
	 * @return
	 */
	public boolean isSimpleItem() {
		if(hasMeta) {
			return false;
		}
		if(this.enchantements == null) {
			return true;
		}
		if(this.enchantements.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether or not a real item stack equals to this custom item setup
	 * @param is
	 * @return
	 */
	public boolean matches(ItemStack is) {
		if (!is.getType().name().equals(material)) {
			return false;
		}
		
		if (is.hasItemMeta() != hasMeta && !hasFlag(Flags.IGNORE_META)) {
			return false;
		}
		
		if(is.getDurability() != durability && !hasFlag(Flags.IGNORE_DURABILITY)) {
			return false;
		}
	
		if (hasMeta && !hasFlag(Flags.IGNORE_META)) {
			
			if(!hasFlag(Flags.IGNORE_DISPLAYNAME)) {
				if(displayname == null && is.getItemMeta().hasDisplayName() || displayname != null && !is.getItemMeta().hasDisplayName()) {
					return false;
				}
				
				if(displayname != null && is.getItemMeta().hasDisplayName() && !displayname.equals(is.getItemMeta().getDisplayName())) {
					return false;
				}	
			}
			
			if(!hasFlag(Flags.IGNORE_ENCHANTMENTS)) {
				
				if(enchantements != null && is.getEnchantments().size() != 0) {
					boolean matchesEnchantments = is.getEnchantments().entrySet().stream().allMatch(entry -> {
						if(enchantements.containsKey(entry.getKey().getKey().getKey())) {
							if(entry.getValue() == enchantements.get(entry.getKey().getKey().getKey())){
								return true;
							}
						}
						return false;
					});
					
					if(!matchesEnchantments) {
						return false;
					}
					
				}else if(!(enchantements.size() == 0 && is.getEnchantments().size() == 0)){
					return false;
				}
			}
			
			if(lore != null && is.getItemMeta().getLore().size() != 0 && !hasFlag(Flags.IGNORE_LORE)) {
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
				
		}else {
			return true;
		}
		
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

	public Map<String, Integer> getEnchantements() {
		return enchantements;
	}

	public void setEnchantements(Map<String, Integer> enchantements) {
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
	
	public List<Flags> getFlags(){
		return flags;
	}
	
	public void addFlag(Flags flag) {
		flags.add(flag);
	}
	
	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}
	
	public void removeFlag(Flags flag) {
		flags.remove(flag);
	}
}
