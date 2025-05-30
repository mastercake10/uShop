package xyz.spaceio.ushop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import xyz.spaceio.customitem.CustomItem;

public class EssentialsWorthConverter {
	
	private static final String[] legacyNames = new String[] {"ACACIA_DOOR","ACACIA_DOOR_ITEM","ACACIA_FENCE","ACACIA_FENCE_GATE","ACACIA_STAIRS","ACTIVATOR_RAIL","AIR","ANVIL","APPLE","ARMOR_STAND","ARROW","BAKED_POTATO","BANNER","BARRIER","BEACON","BED","BED_BLOCK","BEDROCK","BEETROOT","BEETROOT_BLOCK","BEETROOT_SEEDS","BEETROOT_SOUP","BIRCH_DOOR","BIRCH_DOOR_ITEM","BIRCH_FENCE","BIRCH_FENCE_GATE","BIRCH_WOOD_STAIRS","BLACK_GLAZED_TERRACOTTA","BLACK_SHULKER_BOX","BLAZE_POWDER","BLAZE_ROD","BLUE_GLAZED_TERRACOTTA","BLUE_SHULKER_BOX","BOAT","BOAT_ACACIA","BOAT_BIRCH","BOAT_DARK_OAK","BOAT_JUNGLE","BOAT_SPRUCE","BONE","BONE_BLOCK","BOOK","BOOK_AND_QUILL","BOOKSHELF","BOW","BOWL","BREAD","BREWING_STAND","BREWING_STAND_ITEM","BRICK","BRICK_STAIRS","BROWN_GLAZED_TERRACOTTA","BROWN_MUSHROOM","BROWN_SHULKER_BOX","BUCKET","BURNING_FURNACE","CACTUS","CAKE","CAKE_BLOCK","CARPET","CARROT","CARROT_ITEM","CARROT_STICK","CAULDRON","CAULDRON_ITEM","CHAINMAIL_BOOTS","CHAINMAIL_CHESTPLATE","CHAINMAIL_HELMET","CHAINMAIL_LEGGINGS","CHEST","CHORUS_FLOWER","CHORUS_FRUIT","CHORUS_FRUIT_POPPED","CHORUS_PLANT","CLAY","CLAY_BALL","CLAY_BRICK","COAL","COAL_BLOCK","COAL_ORE","COBBLE_WALL","COBBLESTONE","COBBLESTONE_STAIRS","COCOA","COMMAND","COMMAND_CHAIN","COMMAND_MINECART","COMMAND_REPEATING","COMPASS","CONCRETE","CONCRETE_POWDER","COOKED_BEEF","COOKED_CHICKEN","COOKED_FISH","COOKED_MUTTON","COOKED_RABBIT","COOKIE","CROPS","CYAN_GLAZED_TERRACOTTA","CYAN_SHULKER_BOX","DARK_OAK_DOOR","DARK_OAK_DOOR_ITEM","DARK_OAK_FENCE","DARK_OAK_FENCE_GATE","DARK_OAK_STAIRS","DAYLIGHT_DETECTOR","DAYLIGHT_DETECTOR_INVERTED","DEAD_BUSH","DETECTOR_RAIL","DIAMOND","DIAMOND_AXE","DIAMOND_BARDING","DIAMOND_BLOCK","DIAMOND_BOOTS","DIAMOND_CHESTPLATE","DIAMOND_HELMET","DIAMOND_HOE","DIAMOND_LEGGINGS","DIAMOND_ORE","DIAMOND_PICKAXE","DIAMOND_SPADE","DIAMOND_SWORD","DIODE","DIODE_BLOCK_OFF","DIODE_BLOCK_ON","DIRT","DISPENSER","DOUBLE_PLANT","DOUBLE_STEP","DOUBLE_STONE_SLAB2","DRAGON_EGG","DRAGONS_BREATH","DROPPER","EGG","ELYTRA","EMERALD","EMERALD_BLOCK","EMERALD_ORE","EMPTY_MAP","ENCHANTED_BOOK","ENCHANTMENT_TABLE","END_BRICKS","END_CRYSTAL","END_GATEWAY","END_ROD","ENDER_CHEST","ENDER_PEARL","ENDER_PORTAL","ENDER_PORTAL_FRAME","ENDER_STONE","EXP_BOTTLE","EXPLOSIVE_MINECART","EYE_OF_ENDER","FEATHER","FENCE","FENCE_GATE","FERMENTED_SPIDER_EYE","FIRE","FIREBALL","FIREWORK","FIREWORK_CHARGE","FISHING_ROD","FLINT","FLINT_AND_STEEL","FLOWER_POT","FLOWER_POT_ITEM","FROSTED_ICE","FURNACE","GHAST_TEAR","GLASS","GLASS_BOTTLE","GLOWING_REDSTONE_ORE","GLOWSTONE","GLOWSTONE_DUST","GOLD_AXE","GOLD_BARDING","GOLD_BLOCK","GOLD_BOOTS","GOLD_CHESTPLATE","GOLD_HELMET","GOLD_HOE","GOLD_INGOT","GOLD_LEGGINGS","GOLD_NUGGET","GOLD_ORE","GOLD_PICKAXE","GOLD_PLATE","GOLD_RECORD","GOLD_SPADE","GOLD_SWORD","GOLDEN_APPLE","GOLDEN_CARROT","GRASS","GRASS_PATH","GRAVEL","GRAY_GLAZED_TERRACOTTA","GRAY_SHULKER_BOX","GREEN_GLAZED_TERRACOTTA","GREEN_RECORD","GREEN_SHULKER_BOX","GRILLED_PORK","HARD_CLAY","HAY_BLOCK","HOPPER","HOPPER_MINECART","HUGE_MUSHROOM_1","HUGE_MUSHROOM_2","ICE","INK_SACK","IRON_AXE","IRON_BARDING","IRON_BLOCK","IRON_BOOTS","IRON_CHESTPLATE","IRON_DOOR","IRON_DOOR_BLOCK","IRON_FENCE","IRON_HELMET","IRON_HOE","IRON_INGOT","IRON_LEGGINGS","IRON_NUGGET","IRON_ORE","IRON_PICKAXE","IRON_PLATE","IRON_SPADE","IRON_SWORD","IRON_TRAPDOOR","ITEM_FRAME","JACK_O_LANTERN","JUKEBOX","JUNGLE_DOOR","JUNGLE_DOOR_ITEM","JUNGLE_FENCE","JUNGLE_FENCE_GATE","JUNGLE_WOOD_STAIRS","KNOWLEDGE_BOOK","LADDER","LAPIS_BLOCK","LAPIS_ORE","LAVA","LAVA_BUCKET","LEASH","LEATHER","LEATHER_BOOTS","LEATHER_CHESTPLATE","LEATHER_HELMET","LEATHER_LEGGINGS","LEAVES","LEAVES_2","LEVER","LIGHT_BLUE_GLAZED_TERRACOTTA","LIGHT_BLUE_SHULKER_BOX","LIME_GLAZED_TERRACOTTA","LIME_SHULKER_BOX","LINGERING_POTION","LOG","LOG_2","LONG_GRASS","MAGENTA_GLAZED_TERRACOTTA","MAGENTA_SHULKER_BOX","MAGMA","MAGMA_CREAM","MAP","MELON","MELON_BLOCK","MELON_SEEDS","MELON_STEM","MILK_BUCKET","MINECART","MOB_SPAWNER","MONSTER_EGG","MONSTER_EGGS","MOSSY_COBBLESTONE","MUSHROOM_SOUP","MUTTON","MYCEL","NAME_TAG","NETHER_BRICK","NETHER_BRICK_ITEM","NETHER_BRICK_STAIRS","NETHER_FENCE","NETHER_STALK","NETHER_STAR","NETHER_WART_BLOCK","NETHER_WARTS","NETHERRACK","NOTE_BLOCK","OBSERVER","OBSIDIAN","ORANGE_GLAZED_TERRACOTTA","ORANGE_SHULKER_BOX","PACKED_ICE","PAINTING","PAPER","PINK_GLAZED_TERRACOTTA","PINK_SHULKER_BOX","PISTON_BASE","PISTON_EXTENSION","PISTON_MOVING_PIECE","PISTON_STICKY_BASE","POISONOUS_POTATO","PORK","PORTAL","POTATO","POTATO_ITEM","POTION","POWERED_MINECART","POWERED_RAIL","PRISMARINE","PRISMARINE_CRYSTALS","PRISMARINE_SHARD","PUMPKIN","PUMPKIN_PIE","PUMPKIN_SEEDS","PUMPKIN_STEM","PURPLE_GLAZED_TERRACOTTA","PURPLE_SHULKER_BOX","PURPUR_BLOCK","PURPUR_DOUBLE_SLAB","PURPUR_PILLAR","PURPUR_SLAB","PURPUR_STAIRS","QUARTZ","QUARTZ_BLOCK","QUARTZ_ORE","QUARTZ_STAIRS","RABBIT","RABBIT_FOOT","RABBIT_HIDE","RABBIT_STEW","RAILS","RAW_BEEF","RAW_CHICKEN","RAW_FISH","RECORD_10","RECORD_11","RECORD_12","RECORD_3","RECORD_4","RECORD_5","RECORD_6","RECORD_7","RECORD_8","RECORD_9","RED_GLAZED_TERRACOTTA","RED_MUSHROOM","RED_NETHER_BRICK","RED_ROSE","RED_SANDSTONE","RED_SANDSTONE_STAIRS","RED_SHULKER_BOX","REDSTONE","REDSTONE_BLOCK","REDSTONE_COMPARATOR","REDSTONE_COMPARATOR_OFF","REDSTONE_COMPARATOR_ON","REDSTONE_LAMP_OFF","REDSTONE_LAMP_ON","REDSTONE_ORE","REDSTONE_TORCH_OFF","REDSTONE_TORCH_ON","REDSTONE_WIRE","ROTTEN_FLESH","SADDLE","SAND","SANDSTONE","SANDSTONE_STAIRS","SAPLING","SEA_LANTERN","SEEDS","SHEARS","SHIELD","SHULKER_SHELL","SIGN","SIGN_POST","SILVER_GLAZED_TERRACOTTA","SILVER_SHULKER_BOX","SKULL","SKULL_ITEM","SLIME_BALL","SLIME_BLOCK","SMOOTH_BRICK","SMOOTH_STAIRS","SNOW","SNOW_BALL","SNOW_BLOCK","SOIL","SOUL_SAND","SPECKLED_MELON","SPECTRAL_ARROW","SPIDER_EYE","SPLASH_POTION","SPONGE","SPRUCE_DOOR","SPRUCE_DOOR_ITEM","SPRUCE_FENCE","SPRUCE_FENCE_GATE","SPRUCE_WOOD_STAIRS","STAINED_CLAY","STAINED_GLASS","STAINED_GLASS_PANE","STANDING_BANNER","STATIONARY_LAVA","STATIONARY_WATER","STEP","STICK","STONE","STONE_AXE","STONE_BUTTON","STONE_HOE","STONE_PICKAXE","STONE_PLATE","STONE_SLAB2","STONE_SPADE","STONE_SWORD","STORAGE_MINECART","STRING","STRUCTURE_BLOCK","STRUCTURE_VOID","SUGAR","SUGAR_CANE","SUGAR_CANE_BLOCK","SULPHUR","THIN_GLASS","TIPPED_ARROW","TNT","TORCH","TOTEM","TRAP_DOOR","TRAPPED_CHEST","TRIPWIRE","TRIPWIRE_HOOK","VINE","WALL_BANNER","WALL_SIGN","WATCH","WATER","WATER_BUCKET","WATER_LILY","WEB","WHEAT","WHITE_GLAZED_TERRACOTTA","WHITE_SHULKER_BOX","WOOD","WOOD_AXE","WOOD_BUTTON","WOOD_DOOR","WOOD_DOUBLE_STEP","WOOD_HOE","WOOD_PICKAXE","WOOD_PLATE","WOOD_SPADE","WOOD_STAIRS","WOOD_STEP","WOOD_SWORD","WOODEN_DOOR","WOOL","WORKBENCH","WRITTEN_BOOK","YELLOW_FLOWER","YELLOW_GLAZED_TERRACOTTA","YELLOW_SHULKER_BOX"};
	
	public static List<String> convert(Main plugin){
		List<String> output = new ArrayList<String>();
		
		if(!Bukkit.getPluginManager().isPluginEnabled("Essentials")){
			output.add("§cEssentials not installed!");
			return output;
		}
		
		File worthFile = new File("plugins/Essentials/worth.yml");
		
		if(!worthFile.exists()) {
			output.add("§cWorth file does not exists! (" + worthFile.getAbsolutePath() + ")");
			return output;
		}
		
		FileConfiguration worthcfg = YamlConfiguration.loadConfiguration(worthFile);
		int count = 0;
		
		Map<String, Material> stupidEssentialsRenamings = Arrays.stream(Material.values()).
				collect(Collectors.toMap(m -> m.name().replace("_", "").toLowerCase(), m -> m));
		
		Map<String, Material> stupidEssentialsLegacyRenmings = Arrays.stream(legacyNames).
				collect(Collectors.toMap(m -> m.replace("_", "").toLowerCase(), m -> Material.getMaterial(m, true)));
		
		for(String itemName : worthcfg.getConfigurationSection("worth").getKeys(false)) {
			double price = worthcfg.getDouble("worth." + itemName);
			Material mat = stupidEssentialsRenamings.getOrDefault(itemName, null);
			if (mat == null) {
				mat = stupidEssentialsLegacyRenmings.getOrDefault(itemName, null);
			}
			if (mat == null) {
				output.add("§cError: Material for item name " + itemName + " not found, continue...");
				continue;
			}
			if (!mat.isItem()) {
				output.add("§cNot an item: " + mat);
				continue;
			}

			Optional<CustomItem> customItemOpt = plugin.findCustomItem(new ItemStack(mat));
			
			if(customItemOpt.isPresent()) {
				CustomItem customItem = customItemOpt.get();
				customItem.setPrice(price);
			}else {
				CustomItem customItem = new CustomItem(new ItemStack(mat), price);
				plugin.addCustomItem(customItem);	
			}
			count++;
			
		}

		plugin.saveMainConfig();
		output.add(String.format("§aSuccessfully converted §c%d §aitems!", count));
		
		return output;
	}
}
