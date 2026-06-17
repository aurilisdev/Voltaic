package voltaic.common.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import voltaic.Voltaic;
import voltaic.api.radiation.RadiationSystem;

public class CommandWipeRadiationSources {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

	dispatcher.register(Commands.literal(Voltaic.ID).requires(source -> source.hasPermission(3))
		.then(Commands.literal("wiperadiationsources").executes(source -> {

		    RadiationSystem.wipeAllSources(source.getSource().getLevel());
		    source.getSource().sendSuccess(() -> Component.literal("wiped"), true);
		    return 1;

		})));

    }
}
