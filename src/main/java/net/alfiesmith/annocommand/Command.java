package net.alfiesmith.annocommand;

import java.lang.reflect.Method;
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
import net.alfiesmith.annocommand.annotation.Usage;
import org.jetbrains.annotations.Nullable;

@Getter
public class Command {

  private final String name;
  @Nullable
  private final String description, usage, permission, permissionMessage;
  private final List<String> aliases;

  private Command(Command.Builder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.usage = builder.usage;
    this.permission = builder.permission;
    this.permissionMessage = builder.permissionMessage;
    this.aliases = builder.aliases;
  }

  protected static Optional<Command> getCommand(Object parent, Method method) {
    if (!method.isAnnotationPresent(Name.class)) {
      return Optional.empty();
    }

    Name name = method.getAnnotation(Name.class);
    Builder builder = new Builder(name.name());

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

    private final String name;
    private String description, usage, permission, permissionMessage;
    private final List<String> aliases;

    private Builder(String name) {
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

    private void addAlias(String... alias) {
      aliases.addAll(Arrays.asList(alias));
    }

    protected Command build() {
      return new Command(this);
    }
  }
}
