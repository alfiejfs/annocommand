package net.alfiesmith.annocommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class CommandManager {

  private final String pluginName;
  private final CommandMap commandMap;

  public CommandManager(JavaPlugin plugin) {
    this.pluginName = plugin.getName().toLowerCase(Locale.ENGLISH);
    CommandMap map = null;

    try {
      Server server = Bukkit.getServer();
      Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
      getCommandMap.setAccessible(true);
      map = (CommandMap) getCommandMap.invoke(server);
      getCommandMap.setAccessible(false);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException err) {
      err.printStackTrace();
    }

    this.commandMap = map;
  }

  public void registerCommands(Object object) {
    for (Method method : object.getClass().getDeclaredMethods()) {

      if (!Modifier.isPublic(method.getModifiers())) {
        continue;
      }

      method.setAccessible(true);

      Optional<Command> commandOp = Command.getCommand(object, method);
      if (!commandOp.isPresent()) {
        continue;
      }

      Command command = commandOp.get();
      registerCommand(command);

    }
  }

  private void registerCommand(Command command) {
    commandMap.register(command.getLabel(), pluginName, command);
  }

}
