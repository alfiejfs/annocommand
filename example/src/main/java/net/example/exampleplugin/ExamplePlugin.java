package net.example.exampleplugin;

import java.util.List;
import net.alfiesmith.annocommand.CommandManager;
import net.alfiesmith.annocommand.annotation.Aliases;
import net.alfiesmith.annocommand.annotation.Description;
import net.alfiesmith.annocommand.annotation.Name;
import net.alfiesmith.annocommand.annotation.Permission;
import net.alfiesmith.annocommand.annotation.PermissionMessage;
import net.alfiesmith.annocommand.annotation.PlayerOnly;
import net.alfiesmith.annocommand.annotation.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    CommandManager commandManager = new CommandManager(this);
    commandManager.registerCommands(this);
  }

  @Name("playercommand") @Description("A command only a player can run") @Aliases({"playercmd"}) @Usage("/playercommand")
  @Permission("playercommand.use") @PermissionMessage("You can't use this command!")
  @PlayerOnly
  public void playerOnlyCommand(Player player, String[] args) {
    player.sendMessage("A player ran this!");
    for (String arg : args) {
      player.sendMessage("PLAYER: " + arg);
    }
  }

  @Name("consolecommand") @Description("A command any command sender can run") @Aliases({"consolecmd"})
  @Usage("/consolecommand") @PlayerOnly
  @Permission("consolecommand.use") @PermissionMessage("You can't use this command!")
  public void consoleCommand(CommandSender sender, String[] args) {
    sender.sendMessage("A player or console ran this!");
    for (String arg : args) {
      sender.sendMessage("CONSOLE: " + arg);
    }
  }
}
