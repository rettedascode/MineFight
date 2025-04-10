package de.rettedasplanet.minefight.shop;

import de.rettedasplanet.minefight.manager.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    /**
     * Öffnet das Shop-Inventar (Double Chest, 54 Slots) für den Spieler.
     * Es werden fünf Bomben-Varianten angeboten.
     */
    public static void openShop(Player player) {
        Inventory shopInv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Base Shop");

        // Bomben in fünf Leveln – Beispiel: Platziere sie in den Slots 10 bis 14
        for (int i = 0; i < 5; i++) {
            int bombLevel = i + 1;
            int explosionMultiplier = (int) Math.pow(2, i); // Werte: 1, 2, 4, 8, 16
            int cost = bombLevel * 50;

            ItemStack bombItem = new ItemStack(Material.TNT);
            ItemMeta bombMeta = bombItem.getItemMeta();
            bombMeta.setDisplayName(ChatColor.RED + "Bombe Level " + bombLevel + " (Kosten: " + cost + " Punkte)");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Sprengkraft: " + explosionMultiplier + "× TNT");
            lore.add(ChatColor.GRAY + "Kosten: " + cost + " Punkte");
            lore.add(ChatColor.GRAY + "Rechtsklick zum Kaufen");
            lore.add(ChatColor.GRAY + "Wurfbar");
            bombMeta.setLore(lore);

            bombItem.setItemMeta(bombMeta);
            shopInv.setItem(10 + i, bombItem);
        }

        player.openInventory(shopInv);
    }

    public static void handleShopClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.contains("Bombe Level")) {
            List<String> lore = clickedItem.getItemMeta().getLore();
            if (lore == null || lore.size() < 2) {
                return;
            }
            // Erwartete Lore-Zeile: "Kosten: 50 Punkte" (Farbcode vorher entfernen)
            String costLine = ChatColor.stripColor(lore.get(1));
            int cost;
            try {
                String[] parts = costLine.split(" ");
                cost = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Fehler beim Auslesen des Preises!");
                event.setCancelled(true);
                return;
            }

            int currentPoints = PointsManager.getPoints(player.getUniqueId());
            if (currentPoints >= cost) {
                PointsManager.removePoints(player.getUniqueId(), cost);
                // Erzeuge das Bomben-Item in der gleichen Ausführung wie im Shop
                ItemStack bombForPlayer = new ItemStack(Material.TNT);
                bombForPlayer.setItemMeta(clickedItem.getItemMeta());
                player.getInventory().addItem(bombForPlayer);
                player.sendMessage(ChatColor.GREEN + "Du hast " + displayName + " für " + cost + " Punkte gekauft!");
            } else {
                // Falls zu wenig Punkte: Nachricht senden
                player.sendMessage(ChatColor.RED + "Du hast nicht genügend Punkte, um " + displayName + " zu kaufen!");
            }
            event.setCancelled(true);
            player.closeInventory();
        }
    }
}