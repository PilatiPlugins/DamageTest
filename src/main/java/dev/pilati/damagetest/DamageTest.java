package dev.pilati.damagetest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class DamageTest extends JavaPlugin implements Listener {
    protected final String METADATA_KEY = "damageTest";

    protected void addPlayerMetadata(Player player) {
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(this, true));
    }

    protected void removePlayerMetadata(Player player) {
        player.removeMetadata(METADATA_KEY, this);
    }

    protected boolean hasPlayerMetadata(Player player) {
        return player.hasMetadata(METADATA_KEY);
    }

    protected String getMessage(String key){
        String message = getConfig().getString(key);

        if (message == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginCommand("damagetest").setExecutor(new DamageTestCommand());
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        if(!hasPlayerMetadata(player)) {
            return;
        }

        String message = getMessage("damageDealt")
                            .replace("{player}", player.getName())
                            .replace("{damage}", String.valueOf(event.getFinalDamage()))
                            .replace("{cause}", event.getCause().name());

        player.sendMessage(message);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        if(!hasPlayerMetadata(player)) {
            return;
        }

        String message = getMessage("damageTook")
                            .replace("{player}", player.getName())
                            .replace("{damage}", String.valueOf(event.getFinalDamage()))
                            .replace("{cause}", event.getCause().name());

        player.sendMessage(message);
    }

    public class DamageTestCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(getMessage("onlyPlayers"));
                return true;
            }

            Player player = (Player) sender;
            addPlayerMetadata(player);

            String message = getMessage("command").replace("{status}", 
                hasPlayerMetadata(player) ? getMessage("enabled") : getMessage("disabled")
            );

            player.sendMessage(message);

            return true;
        }
    }
}