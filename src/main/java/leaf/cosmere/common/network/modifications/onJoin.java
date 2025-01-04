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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static leaf.cosmere.common.network.modifications.AdvancementUtils.getAdvancementCompletionPercentage;
import static leaf.cosmere.common.network.modifications.Uploader.postMethod;

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

        // Guardar el JSON de poderes y efectos en archivos
        savePowersInfoToFile(player.getName().getString(), powersInfo);
        saveEffectsInfoToFile(player.getName().getString(), effectsInfo);

        postMethod("powers/power_" + player.getName().getString() + ".json", "https://ponchisaohosting.xyz/downloads/cosmere/post/");
        postMethod("effects/effect_" + player.getName().getString() + ".json", "https://ponchisaohosting.xyz/downloads/cosmere/post/");
    }

    private static void savePowersInfoToFile(String playerName, JsonObject powersInfo) {
        File powersFolder = new File("powers/");

        if (!powersFolder.exists()) {
            powersFolder.mkdir();
        }
        try (FileWriter fileWriter = new FileWriter("powers/power_" + playerName + ".json")) {
            fileWriter.write(powersInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveEffectsInfoToFile(String playerName, int effectsInfo) {
        File effectsFolder = new File("effects/");

        if (!effectsFolder.exists()) {
            effectsFolder.mkdir();
        }

        JsonObject effectsJson = new JsonObject();
        effectsJson.addProperty("player", playerName);
        effectsJson.addProperty("effectsInfo", effectsInfo);

        try (FileWriter fileWriter = new FileWriter("effects/effect_" + playerName + ".json")) {
            fileWriter.write(effectsJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
