package kittify.client.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.IClientCommand;

public interface IKittifyClientCommand extends IClientCommand {
    @Override
    default boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }
}
