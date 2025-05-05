package voltaic.common.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import voltaic.Voltaic;
import voltaic.api.radiation.RadiationSystem;

public class CommandWipeRadiationSources {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal(Voltaic.ID).requires(source -> source.hasPermission(3)).then(Commands.literal("wiperadiationsources").executes(source -> {

            RadiationSystem.wipeAllSources(source.getSource().getLevel());
            source.getSource().sendSuccess(new StringTextComponent("wiped"), true);
            return 1;

        })));


    }
}
