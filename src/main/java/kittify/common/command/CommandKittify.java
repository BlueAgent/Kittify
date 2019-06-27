package kittify.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

public class CommandKittify extends CommandTreeBase {
    public CommandKittify() {
        addSubcommand(new CommandTPS());
        addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName() {
        return "kittify";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.kittify.usage";
    }
}
