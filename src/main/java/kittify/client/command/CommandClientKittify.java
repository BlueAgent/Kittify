package kittify.client.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

public class CommandClientKittify extends CommandTreeBase implements IKittifyClientCommand {
    public CommandClientKittify() {
        addSubcommand(new CommandClientTPS());
        addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getName() {
        return "ckittify";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Kittify clientside commands";
    }
}
