package leaf.cosmere.common.network.modifications;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.common.Cosmere;
import leaf.cosmere.common.commands.subcommands.CosmereEffectCommand;
import leaf.cosmere.common.commands.subcommands.ManifestationCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static leaf.cosmere.common.network.modifications.AdvancementUtils.getAdvancementCompletionPercentage;

@Mod.EventBusSubscriber(modid = Cosmere.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class onJoin {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        double completionPercentage = getAdvancementCompletionPercentage(player);
        CosmereAPI.logger.info("Player " + player.getName().getString() + " has completed " + completionPercentage + "% of advancements.");

        // Llamar a checkEffectsOnJoin y almacenar el resultado
        int effectsInfo = CosmereEffectCommand.checkEffectsOnJoin(player);

        // Consultar y reportar poderes al iniciar sesión
        JsonObject powersInfo = ManifestationCommand.reportPowersOnJoin(player);

        // Imprimir en la consola del servidor
        CosmereAPI.logger.info("Check effects on join for player: " + player.getName().getString() + " returned: " + effectsInfo);

        // Enviar un mensaje al jugador con la información de los poderes y el % del cosmere
        player.sendSystemMessage(Component.literal("Your effects have been checked on login and updated the database. Check it on the web!"));
        player.sendSystemMessage(Component.literal("You have completed " + completionPercentage + "% of advancements"));

        // Enviar en consola los poderes al jugador
        CosmereAPI.logger.info(powersInfo.toString());
        player.sendSystemMessage(Component.literal(powersInfo.toString()));
    }
}
