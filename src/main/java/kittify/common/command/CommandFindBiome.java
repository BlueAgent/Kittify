package kittify.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

import static kittify.common.util.CommandUtil.PARSE_BOOLEAN_VALID_VALUES;

public class CommandFindBiome extends CommandBase {
    @Override
    public String getName() {
        return "findbiome";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.kittify.findbiome.usage";
    }

    public Biome parseBiome(String value) throws CommandException {
        Biome biome = GameRegistry.findRegistry(Biome.class).getValue(new ResourceLocation(value));
        if (biome == null) {
            throw new CommandException("commands.kittify.findbiome.biome.invalid", value);
        }
        return biome;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || args.length > 3) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        Biome biome = parseBiome(args[1]);
        int range = args.length < 2 ? 1000 : parseInt(args[2], 0, 10000);
        boolean onlyGenerated = args.length < 3 ? false : parseBoolean(args[3]);

        World world = sender.getEntityWorld();
        BlockPos origin = sender.getPosition();
        BiomeProvider biomeProvider = world.getBiomeProvider();
        Biome[] tempBiomes = new Biome[1];
        tempBiomes = biomeProvider.getBiomes(tempBiomes, 0, 0, 1, 1, false);
        //TODO: Implement
        sender.sendMessage(new TextComponentString("NOT IMPLEMENTED"));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, GameRegistry.findRegistry(Biome.class).getKeys());
        if (args.length == 3)
            return getListOfStringsMatchingLastWord(args, PARSE_BOOLEAN_VALID_VALUES);
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
