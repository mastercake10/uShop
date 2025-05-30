package xyz.spaceio.ushop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.Linus122.SpaceIOMetrics.Metrics;
import net.milkbowl.vault.economy.Economy;
import xyz.spaceio.customitem.CustomItem;

public class Main extends JavaPlugin {

	/*
	 * Vault Economy plugin
	 */
	private Economy economy = null;

	/*
	 * Main config.yml
	 */
	private FileConfiguration cfg;

	/*
	 * Inventories that are currently open
	 */
	private Map<Player, Inventory> openShops = new HashMap<Player, Inventory>();

	/*
	 * List that contains all information about sell items
	 */
	private List<CustomItem> customItems = new ArrayList<CustomItem>();
	
	/*
	 * Gson object for serializing processes
	 */
	private Gson gson = new Gson();


	/**
	 * Logger for logging all sell actions
	 */
	private PrintStream logs;


	@Override
	public void onEnable() {
		setupEconomy();
		
		this.saveDefaultConfig();
		this.loadItems();

		// registering command
		registerCommand(this.cfg.getString("command"));
		
		this.getCommand("ushop").setExecutor(new uShopCmd(this));

		this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
		
		String fileName = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'.log'").format(new Date());
        File dir = new File(getDataFolder(), "logs");
        dir.mkdirs();
        File logs = new File(dir, fileName);
        try {
			this.logs = new PrintStream(logs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// init spaceio metrics
		new Metrics(this);
	}
	
	@Override
	public void onDisable() {
		logs.flush();
		logs.close();
	}

	/**
	 * @return the logs
	 */
	public PrintStream getLogs() {
		return logs;
	}
	
	public List<String> getCustomItemDescription(CustomItem item, int amount){
		return getCustomItemDescription(item, amount, cfg.getString("gui-item-enumeration-format").replace("&", "§"));
	}

	public List<String> getCustomItemDescription(CustomItem item, int amount, String itemEnumFormat){
		List<String> list = new ArrayList<String>();
	
		String s = itemEnumFormat.replace("%amount%", amount + "")
				.replace("%material%", item.getDisplayname() == null ? WordUtils.capitalize(item.getMaterial().toLowerCase().replace("_", " ")) : item.getDisplayname())
				.replace("%price%", economy.format(item.getPrice() * amount));
		list.add(s);
		
		// adding enchantements
		item.getEnchantements().forEach((enchantement, level) -> {
			list.add(String.format("§7%s %s", WordUtils.capitalize(enchantement), Utils.toRoman(level)));
		});
		
		item.getFlags().forEach(flag -> {
			list.add(String.format("§e%s", flag.name().toLowerCase()));
		});
		
		return list;
	}

	/**
	 * Saved all Custom items to the config.
	 */
	public void saveMainConfig() {
		List<CustomItem> advancedItems = new ArrayList<CustomItem>();
		List<String> simpleItems = new ArrayList<String>();
		
		for(CustomItem customItem : customItems) {
			if(customItem.isSimpleItem()) {
				simpleItems.add(customItem.getMaterial() + ":" + customItem.getPrice());
			}else {
				advancedItems.add(customItem);
			}
		}
		cfg.set("sell-prices-simple", simpleItems);
		cfg.set("sell-prices", gson.toJson(advancedItems));
		this.saveConfig();
	}

	public HashMap<CustomItem, Integer> getSalableItems(ItemStack[] is) {
		HashMap<CustomItem, Integer> customItemsMap = new HashMap<CustomItem, Integer>();
		for (ItemStack stack : is) {
			if (stack != null) {
				if (stack.getType().toString().toUpperCase().contains("SHULKER_BOX")) {
					Inventory container = ((InventoryHolder) ((BlockStateMeta) stack.getItemMeta()).getBlockState()).getInventory();
					for (int j = 0; j < container.getSize(); j++) {
						ItemStack shulkerItem = container.getItem(j);
						if (shulkerItem != null && !shulkerItem.getType().equals(Material.AIR)) {
							Optional<CustomItem> opt = findCustomItem(shulkerItem);
							if(opt.isPresent() && this.isSalable(shulkerItem)) {
								// add item to map
								customItemsMap.compute(opt.get(), (k, v) -> v == null ? shulkerItem.getAmount() : v + shulkerItem.getAmount());
							}
						}
					}										
				} else {
					// check if item is in the custom item list
					Optional<CustomItem> opt = findCustomItem(stack);
					if(opt.isPresent() && this.isSalable(stack)) {
						// add item to map
						customItemsMap.compute(opt.get(), (k, v) -> v == null ? stack.getAmount() : v + stack.getAmount());
					}
				}		
			}
		}
		return customItemsMap;
	}
	
	/**
	 * Finds the representing Custom Item for a certain Item Stack
	 * @param stack
	 * @return
	 */
	public Optional<CustomItem> findCustomItem(ItemStack stack) {
		return customItems.stream().filter((item) -> item.matches(stack)).findFirst();
	}

	public double calcWorthOfContent(ItemStack[] content) {
		HashMap<CustomItem, Integer> salable = getSalableItems(content);
		return salable.keySet().stream().mapToDouble(v -> v.getPrice() * salable.get(v)).sum();
	}
	
	public boolean isSalable(ItemStack is) {
		if(is == null || is.getType() == null || is.getType() == Material.AIR) return false;
		Optional<CustomItem> customItemOptional = this.findCustomItem(is);
		if(customItemOptional.isPresent()) {
			if(customItemOptional.get().getPrice() > 0d) {
				return true;
			}
		}
		return false;
	}

	public Economy getEconomy() {
		return economy;
	}

	public Map<Player, Inventory> getOpenShops() {
		return openShops;
	}

	public List<CustomItem> getCustomItems() {
		return customItems;
	}

	public void registerCommand(String cmdLabel) {

		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			// remove old command if already used
			SimplePluginManager spm = (SimplePluginManager) this.getServer().getPluginManager();
			Field f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			SimpleCommandMap scm = (SimpleCommandMap) f.get(spm);
			
			Field f2 = SimpleCommandMap.class.getDeclaredField("knownCommands");
			f2.setAccessible(true);
			HashMap<String, Command> map = (HashMap<String, Command>) f2.get(scm);
			map.remove(cmdLabel);

			f.setAccessible(false);

			// register
			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			Cmd cmd = new Cmd(this, cmdLabel);
			commandMap.register(cmdLabel, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	public void addCustomItem(CustomItem i) {
		customItems.add(i);
	}
	
	public boolean isShopGUI(InventoryView inventoryView) {
		return inventoryView.getTitle().equals(this.getConfig().getString("gui-name").replace("&", "§"));
	}

	public void openShop(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9 * this.getConfig().getInt("gui-rows"),
				this.getConfig().getString("gui-name").replace("&", "§"));
		ItemStack is = new ItemStack(Material.getMaterial(this.getConfig().getString("gui-sellitem.material")));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.getConfig().getString("gui-sellitem.displayname").replace('&', '§').replace("%total%",
				this.getEconomy().format(0)));
		is.setItemMeta(im);
		inv.setItem(inv.getSize() - 5, is);
		
		ItemStack pane = new ItemStack(Material.valueOf(cfg.getString("gui-bottomrow.material")));
		ItemMeta meta = pane.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', cfg.getString("gui-bottomrow.displayname")));
		pane.setItemMeta(meta);
		
		for (int i = inv.getSize() - 9; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				inv.setItem(i, pane.clone());
			}
		}

		p.openInventory(inv);
		this.getOpenShops().put(p, inv);	
		
		
	}
	
	/**
	 * Loads all item configurations from the config.yml
	 */
	private void loadItems() {
		this.cfg = this.getConfig();
		
		if(this.cfg.getString("sell-prices") != null) {
			customItems = gson.fromJson(cfg.getString("sell-prices"), new TypeToken<List<CustomItem>>(){}.getType());
		}
		
		// converting simple items to custom items
		if(this.cfg.contains("sell-prices-simple")) {
			for(String entry : this.cfg.getStringList("sell-prices-simple")) {
				try {
					CustomItem ci = new CustomItem(new ItemStack(Material.valueOf(entry.split(":")[0])), Double.parseDouble(entry.split(":")[1]));
					customItems.add(ci);
				}catch(Exception e) {
					System.out.println("Error in config.yml: " + entry);
				}
			}
		}else {
			// adding default materials
			List<String> entries = Arrays.stream(Material.values()).map(v -> v.name() + ":0.0").collect(Collectors.toList());
			this.cfg.set("sell-prices-simple", entries);
			this.saveConfig();
			for(Material mat : Material.values()) {
				if (mat.isItem())
					customItems.add(new CustomItem(new ItemStack(mat), 0d));
			}
		}
	}
	
	/**
	 * @return amount of configured custom items
	 */
	public long getCustomItemCount() {
		return customItems.stream().filter(p -> !p.isSimpleItem()).count();
	}
	
	public void reloadItems() {
		this.reloadConfig();
		loadItems();
		
	}

	public void updateUI(Inventory shopInventory) {
		// Update
		ItemStack[] invContent = shopInventory.getContents();
		invContent[shopInventory.getSize() - 5] = null;

		List<String> lore = new ArrayList<String>();
		double[] totalPrice = {0d};

		getSalableItems(invContent).forEach((item, amount) -> {
			double totalStackPrice = item.getPrice() * amount;
			totalPrice[0] += totalStackPrice;
			lore.addAll(getCustomItemDescription(item, amount));
		});

		ItemStack sell = shopInventory.getItem(shopInventory.getSize() - 5);

		if (sell == null)
			return;
		if (sell.getItemMeta() == null)
			return;

		ItemMeta im = sell.getItemMeta();
		im.setDisplayName(cfg.getString("gui-sellitem.displayname").replace('&', '§')
				.replace("%total%", economy.format(totalPrice[0])));
		im.setLore(lore);
		sell.setItemMeta(im);

		shopInventory.setItem(shopInventory.getSize() - 5, sell);
	}

}
