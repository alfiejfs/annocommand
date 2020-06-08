package net.alfiesmith.annocommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.alfiesmith.annocommand.annotation.Aliases;
import net.alfiesmith.annocommand.annotation.Description;
import net.alfiesmith.annocommand.annotation.Name;
import net.alfiesmith.annocommand.annotation.Permission;
import net.alfiesmith.annocommand.annotation.PermissionMessage;
import net.alfiesmith.annocommand.annotation.PlayerOnly;
import net.alfiesmith.annocommand.annotation.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class Command extends org.bukkit.command.Command {

  private final Object parent;
  private final Method method;
  private final String name;
  private final boolean playerOnly;
  private final List<String> aliases;

  @Nullable
  private final String description, usage, permission, permissionMessage;

  private Command(Command.Builder builder) {
    super(builder.name);
    this.parent = builder.parent;
    this.method = builder.method;
    this.name = builder.name;
    this.playerOnly = builder.playerOnly;
    this.description = builder.description;
    this.usage = builder.usage;
    this.permission = builder.permission;
    this.permissionMessage = builder.permissionMessage;
    this.aliases = builder.aliases;
  }


  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
    try {
      if (!playerOnly || sender instanceof Player) {
        method.invoke(parent, sender, args);
      }
    } catch (IllegalAccessException | InvocationTargetException err) {
      err.printStackTrace();
    }
    return false;
  }

  // Method requires [ (CommandSender|Player) String[] ]
  protected static Optional<Command> getCommand(Object parent, Method method) {
    Parameter[] parameters = method.getParameters();

    if (!method.isAnnotationPresent(Name.class) || parameters.length != 2) {
      return Optional.empty();
    }

    Name name = method.getAnnotation(Name.class);
    Builder builder = new Builder(parent, method, name.name());

    if (parameters[0].getType() == Player.class) {
      if (method.isAnnotationPresent(PlayerOnly.class)) {
        PlayerOnly playerOnly = method.getAnnotation(PlayerOnly.class);
        builder.setPlayerOnly(playerOnly.playerOnly());
      } else {
        return Optional.empty();
      }
    } else if (parameters[0].getType() != CommandSender.class) {
      return Optional.empty();
    }

    Parameter second = parameters[1];
    if (second.getType() != String.class || !second.getType().isArray()) {
      return Optional.empty();
    }

    if (method.isAnnotationPresent(Aliases.class)) {
      Aliases aliases = method.getAnnotation(Aliases.class);
      builder.addAlias(aliases.aliases());
    }

    if (method.isAnnotationPresent(Description.class)) {
      Description description = method.getAnnotation(Description.class);
      builder.setDescription(description.description());
    }

    if (method.isAnnotationPresent(Permission.class)) {
      Permission permission = method.getAnnotation(Permission.class);
      builder.setPermission(permission.permission());
    }

    if (method.isAnnotationPresent(PermissionMessage.class)) {
      PermissionMessage message = method.getAnnotation(PermissionMessage.class);
      builder.setPermissionMessage(message.permissionMessage());
    }

    if (method.isAnnotationPresent(Usage.class)) {
      Usage usage = method.getAnnotation(Usage.class);
      builder.setUsage(usage.usage());
    }

    return Optional.of(builder.build());
  }

  protected static class Builder {

    private final Object parent;
    private final Method method;
    private final String name;
    private String description, usage, permission, permissionMessage;
    private boolean playerOnly;
    private final List<String> aliases;

    private Builder(Object parent, Method method, String name) {
      this.parent = parent;
      this.method = method;
      this.name = name;
      this.aliases = new ArrayList<>();
    }

    private void setDescription(String description) {
      this.description = description;
    }

    private void setUsage(String usage) {
      this.usage = usage;
    }

    private void setPermission(String permission) {
      this.permission = permission;
    }

    private void setPermissionMessage(String permissionMessage) {
      this.permissionMessage = permissionMessage;
    }

    private void setPlayerOnly(boolean playerOnly) {
      this.playerOnly = playerOnly;
    }

    private void addAlias(String... alias) {
      aliases.addAll(Arrays.asList(alias));
    }

    protected Command build() {
      return new Command(this);
    }
  }
}
