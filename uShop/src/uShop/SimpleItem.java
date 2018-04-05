package uShop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SimpleItem {
	public Material material;
	public byte data;
	
	public SimpleItem(ItemStack is) {
		material = is.getType();
		// getData() is a deprecated method, but there are no alternatives.
		data = is.getData().getData();
	}
}
