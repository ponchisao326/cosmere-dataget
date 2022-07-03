/*
 * File created ~ 24 - 4 - 2021 ~ Leaf
 *
 * Special thank you to the New Tardis Mod team.
 * That mod taught me how to correctly add new commands, among other things!
 * https://tardis-mod.com/books/home/page/links#bkmrk-source
 */

package leaf.cosmere.commands;

import com.mojang.brigadier.CommandDispatcher;
import leaf.cosmere.Cosmere;
import leaf.cosmere.commands.arguments.ManifestationsArgumentType;
import leaf.cosmere.commands.subcommands.EyeCommand;
import leaf.cosmere.commands.subcommands.ManifestationCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;


public class CosmereCommand
{

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal(Cosmere.MODID)
				.then(EyeCommand.register(dispatcher))
				.then(ManifestationCommand.register(dispatcher))
		);
	}
	public static void registerCustomArgumentTypes() {
		ArgumentTypeInfos.registerByClass(
				ManifestationsArgumentType.class,
				SingletonArgumentInfo.contextFree(ManifestationsArgumentType::createArgument));
	}
}
