package kittify.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandClientTPS extends CommandBase implements IKittifyClientCommand {
    @Override
    public String getName() {
        return "tps";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.ckittify.tps.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }
        double tps = parseDouble(args[0], 1.0, 200.0);
        double delay = 1000.0 / tps;
        Minecraft.getMinecraft().timer.tickLength = (float) delay;
        sender.sendMessage(new TextComponentTranslation("commands.ckittify.tps.success", tps, delay));
    }
}
