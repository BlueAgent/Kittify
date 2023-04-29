package kittify.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandTPS extends CommandBase {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private double timeModifier = 0;
    private double timeRemainder = 0;

    public CommandTPS() {
        scheduler.scheduleAtFixedRate(() -> {
            if (timeModifier != 0) {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                timeRemainder += timeModifier;
                long timeChange = (long) Math.floor(timeRemainder);
                timeRemainder -= timeChange;
                server.currentTime += timeChange;
                server.timeOfLastWarning += timeChange;
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getName() {
        return "tps";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.kittify.tps.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(this.getUsage(sender));
        }
        double tps = parseDouble(args[0], 1.0, 200.0);
        double delay = 1000.0 / tps;
        double speedMultiplier = tps / 20.0;
        // -450ms = 200 tps
        // -150ms = 80 tps
        // -100ms = 60 tps
        //  -50ms = 40 tps
        //    0ms = 20 tps
        //  +25ms = 10 tps
        //+47.5ms =  1 tps
        //  +50ms =  0 tps Not this pls XD
        timeModifier = 50.0 - 50.0 * speedMultiplier;
        notifyCommandListener(sender, this, "commands.kittify.tps.success", tps, delay, timeModifier);
    }
}
