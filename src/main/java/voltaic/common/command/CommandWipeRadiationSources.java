package voltaic.common.command;

import com.mojang.brigadier.CommandDispatcher;
import voltaic.Voltaic;
import voltaic.api.radiation.RadiationSystem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class CommandWipeRadiationSources {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal(Voltaic.ID).requires(source -> source.hasPermission(3)).then(Commands.literal("wiperadiationsources").executes(source -> {

            RadiationSystem.wipeAllSources(source.getSource().getLevel());
            source.getSource().sendSuccess(new TextComponent("wiped"), true);
            return 1;

        })));


    }
}
