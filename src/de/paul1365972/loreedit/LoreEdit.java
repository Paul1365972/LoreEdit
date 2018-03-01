package de.paul1365972.loreedit;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LoreEdit extends JavaPlugin implements CommandExecutor {

	public static final String PREFIX = ChatColor.RED + "[LoreEdit] " + ChatColor.WHITE;
	public static final UUID PAUL_UUID = UUID.fromString("09a91067-0837-43eb-98a9-9e2bbe80a56a");

	@Override
	public void onEnable() {
		Bukkit.getPluginCommand("loreedit").setExecutor(this);
	}

	public String executeCommand(Player p, String[] args) {
		if (!p.hasPermission("loreedit.edit") && !p.getUniqueId().equals(PAUL_UUID))
			return ChatColor.RED + "You do not have permission to perform this command.";
		if (args.length == 0)
			return "Use /loreedit <read/set/remove/replace/replaceall";
		ItemStack is = p.getInventory().getItemInMainHand();
		if (is == null)
			return "You got no item in your hand";
		if (args[0].equalsIgnoreCase("read")) {
			if (!is.getItemMeta().hasLore())
				return "Item got no Lore";
			List<String> lore = is.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				p.sendMessage(PREFIX + i + ": \"" + lore.get(i) + ChatColor.RESET + "\"");
			}
			return null;
		} else if (args[0].equalsIgnoreCase("set")) {
			if (args.length < 2)
				return "/loreedit set <line> <text>";
			ItemMeta meta = is.getItemMeta();
			int index = Integer.valueOf(args[1]);
			String[] lore;
			lore = new String[Math.max(meta.hasLore() ? meta.getLore().size() : 0, index + 1)];
			if (meta.hasLore()) {
				List<String> oldLore = meta.getLore();
				for (int i = 0; i < oldLore.size(); i++) {
					lore[i] = oldLore.get(i);
				}
			}

			StringBuilder lineBuilder = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				lineBuilder.append(args[i]);
				if (i + 1 != args.length)
					lineBuilder.append(' ');
			}
			lore[index] = lineBuilder.toString().replaceAll("\\&", "§");
			meta.setLore(Arrays.asList(lore));
			is.setItemMeta(meta);
			return "Changed to line " + index + " to \"" + lore[index] + ChatColor.RESET + "\"";
		} else if (args[0].equalsIgnoreCase("remove")) {
			ItemMeta meta = is.getItemMeta();
			if (!meta.hasLore())
				return "Item got no Lore";
			if (args.length == 1) {
				meta.setLore(null);
				is.setItemMeta(meta);
				return "Removed Item Lore";
			}
			int index = Integer.valueOf(args[1]);
			List<String> lore = meta.getLore();
			if (lore.size() <= index)
				return "Line number " + index + " does not exist";
			lore.remove(index);
			meta.setLore(lore);
			is.setItemMeta(meta);
			return "Removed Line number " + index;
		} else if (args[0].equalsIgnoreCase("replace")) {
			ItemMeta meta = is.getItemMeta();
			if (!meta.hasLore())
				return "Item got no Lore";
			if (args.length < 2)
				return "Use /loreedit replace <regex> [replacement]";
			String regex = args[1];
			String replacement = "";
			if (args.length == 3)
				replacement = args[2];
			List<String> lore = meta.getLore();
			for (int i = 0; i < lore.size(); i++) {
				String line = lore.get(i);
				String edited = line.replaceFirst(regex, replacement);
				lore.set(i, edited);
				if (!line.contentEquals(edited))
					break;
			}
			meta.setLore(lore);
			is.setItemMeta(meta);
			return "Replaced";
		} else if (args[0].equalsIgnoreCase("replaceall")) {
			ItemMeta meta = is.getItemMeta();
			if (!meta.hasLore())
				return "Item got no Lore";
			if (args.length < 2)
				return "Use /loreedit replace <regex> [replacement]";
			String regex = args[1];
			String replacement = "";
			if (args.length == 3)
				replacement = args[2];
			List<String> lore = meta.getLore();
			for (int i = 0; i < lore.size(); i++) {
				String line = lore.get(i);
				lore.set(i, line.replaceAll(regex, replacement));
			}

			meta.setLore(lore);
			is.setItemMeta(meta);
			return "Replaced";
		}
		return "Error";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			String message = executeCommand((Player) sender, args);
			if (message != null)
				sender.sendMessage(PREFIX + message);
		} else {
			sender.sendMessage("Your not a player");
		}
		return true;
	}
}
