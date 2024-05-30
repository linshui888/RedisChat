package dev.unnm3d.redischat.chat;

import dev.unnm3d.redischat.RedisChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.Inventory;

public class ChatColorGUI extends AbstractGui {
    private final RedisChat plugin;

    public ChatColorGUI(RedisChat plugin) {
        super(9, plugin.guiSettings.chatColorGUIStructure.size());
        this.plugin = plugin;
        this.applyStructure(plugin.guiSettings.getChatColorGUIStructure());

    }

    @Override
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        super.handleClick(slotNumber, player, clickType, event);
        player.closeInventory();

        StringBuilder sb = new StringBuilder();
        plugin.guiSettings.chatColorGUIStructure.forEach(row -> sb.append(row.replace(" ", "")));

        char slotChar = sb.toString().charAt(slotNumber);
        final ChatColor color = ChatColor.getByChar(slotChar);
        if (color == null) {
            plugin.messages.sendMessage(player, plugin.messages.invalid_color);
            return;
        }

        plugin.getPlaceholderManager().addPlayerPlaceholder(player.getName(), "chat_color", "<" + color.name().toLowerCase() + ">");
        plugin.messages.sendMessage(player, plugin.messages.color_set);

    }
}
