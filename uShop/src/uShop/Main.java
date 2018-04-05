package uShop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{
	
	public static Economy economy = null;
	public static FileConfiguration cfg;
	
	public static HashMap<Player, Inventory> openShops = new HashMap<Player, Inventory>();
	
	public static List<CustomItem> customItems = new ArrayList<CustomItem>();
	public static Plugin pl;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		setupEconomy();
		reloadConfig2();
		pl = this;
		
		// registering command
		registerCommand(cfg.getString("command"));
		this.getCommand("ushop").setExecutor(new uShopCmd(this));
		
		this.getServer().getPluginManager().registerEvents(new Listeners(), this);
		
		final String itemEnumFormat = Main.cfg.getString("gui-item-enumeration-format").replace("&", "§");
		
		
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			Iterator<Player> it = openShops.keySet().iterator();
			while(it.hasNext()){
				Player p = it.next();
				if(p.getOpenInventory().getTopInventory() != null){
					if(p.getOpenInventory().getTopInventory().getTitle() != null){
						if(p.getOpenInventory().getTopInventory().getTitle().equals(Main.cfg.getString("gui-name").replace("&", "§"))){
							//Aktuallisieren
							ItemStack[] is = p.getOpenInventory().getTopInventory().getContents();
							is[p.getOpenInventory().getTopInventory().getSize() - 5] = null;
							List<String> lore = new ArrayList<String>();
							for(SimpleItem si : Main.getMaterialsWithData(is)){
								int amount = Main.getTotalAmountOfMaterialAndData(is, si);
								double price = Main.getPrice(si) * amount;
								String s = itemEnumFormat.replace("%amount%", amount + "").replace("%material%", si.material.name().toLowerCase().replace("_", " ")).replace("%price%", Main.economy.format(price));
								lore.add(s);
							}
							
							ItemStack sell = p.getOpenInventory().getTopInventory().getItem(p.getOpenInventory().getTopInventory().getSize() - 5);
							if(sell == null) continue;
							if(sell.getItemMeta() == null) continue;
							ItemMeta im  = sell.getItemMeta();
							im.setDisplayName(Main.cfg.getString("gui-sellitem.displayname").replace('&', '§').replace("%total%", Main.economy.format(Main.calcPrices(is))));
							im.setLore(lore);
							sell.setItemMeta(im);
							
							p.getOpenInventory().getTopInventory().setItem(p.getOpenInventory().getTopInventory().getSize() - 5, sell);
						}else{
							ItemStack[] stacks = openShops.get(p).getContents();
							stacks[openShops.get(p).getSize() - 5] = null;
							addToInv(p.getInventory(), stacks);
							openShops.remove(p);
						}
						
					}
				}
			}
		}, 20L, 20L);
	}
	public void reloadConfig2(){
		this.saveDefaultConfig();
		cfg = this.getConfig();
		for(Material mat : Material.values()){
			if(!cfg.contains("sell-prices." + mat.name())){
				cfg.set("sell-prices." + mat.name(), 0.00);
			}
		}
		this.saveConfig();
	}
	public static void addToInv(Inventory inv, ItemStack[] is){
		for(ItemStack stack : is){
			if(stack != null){
				inv.addItem(stack);			
			}
		}
	}
	public static double getPrice(SimpleItem si){
		if(cfg.getBoolean("use-essentials-worth-list")){
			com.earth2me.essentials.Essentials plugin = (com.earth2me.essentials.Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			try{
				return plugin.getWorth().getPrice(new ItemStack(si.material, 1, si.data)).doubleValue();
			}catch(NullPointerException e){
				return 0.0;
			}
		}else{
			if(si.data == (byte) 0){
				return cfg.getDouble("sell-prices." + si.material.name());	
			}else{
				System.out.println(si.data);
				return cfg.getDouble("sell-prices." + si.material.name() + "!" + si.data);
			}
		}
	}
	public static int getTotalAmountOfMaterialAndData(ItemStack[] is, SimpleItem si){
		int amount = 0;
		for(ItemStack stack : is){
			if(stack != null){
				if(stack.getType() == si.material && stack.getData().getData() == si.data){
					amount += stack.getAmount();
				}
			}
		}
		return amount;
	}
	public static List<SimpleItem> getMaterialsWithData(ItemStack[] is){
		List<SimpleItem> mats = new ArrayList<SimpleItem>();
		for(ItemStack stack : is){
			if(stack != null){
				SimpleItem si = new SimpleItem(stack);
				if(!mats.contains(si)){
					mats.add(si);
				}	
			}
		}
		return mats;
	}
	public static double calcPrices(ItemStack[] is){
		double price = 0;
		for(ItemStack stack : is){
			price += getPrice(stack);
		}
		return price;
	}
	
	public static double getPrice(ItemStack stack){
		if(stack != null){
			if(cfg.getBoolean("use-essentials-worth-list")){
				com.earth2me.essentials.Essentials plugin = (com.earth2me.essentials.Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
				try{
					return plugin.getWorth().getPrice(stack).doubleValue() * stack.getAmount();	
				}catch(NullPointerException e){
					
				}
			}else{
				return getPrice(new SimpleItem(stack)) * stack.getAmount();
			}
		}
		return 0d;
	}
	
	public void registerCommand(String cmdLabel){
		
		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			// remove old command if already used
			SimplePluginManager spm = (SimplePluginManager) this.getServer().getPluginManager();
			Field f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			SimpleCommandMap scm = (SimpleCommandMap) f.get(spm);
			Field f2 = scm.getClass().getDeclaredField("knownCommands");
			f2.setAccessible(true);
			HashMap<String, Command> map = (HashMap<String, Command>) f2.get(scm);
			map.remove(cmdLabel);
            
        	f.setAccessible(false);

            // register 
			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			Cmd cmd = new Cmd(cmdLabel);
			commandMap.register(cmdLabel, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	public static void addCustomItem(CustomItem i) {
		customItems.add(i);
		
	}
}
