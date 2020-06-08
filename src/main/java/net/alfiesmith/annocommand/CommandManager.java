package net.alfiesmith.annocommand;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class CommandManager {

  private final JavaPlugin plugin;

  public void registerCommands(Object object) {
    for (Method method : object.getClass().getDeclaredMethods()) {
      Optional<Command> commandOp = Command.getCommand(object, method);

      if (!commandOp.isPresent()) {
        continue;
      }

      Command command = commandOp.get();
      registerCommand(command);
    }
  }

  private void registerCommand(Command command) {

  }


}
