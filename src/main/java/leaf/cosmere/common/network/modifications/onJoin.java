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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static leaf.cosmere.common.network.modifications.AdvancementUtils.getAdvancementCompletionPercentage;

@Mod.EventBusSubscriber(modid = Cosmere.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class onJoin {

    private static final Path POWERS_JSON_FILE_PATH = Paths.get("config", Cosmere.MODID, "powersInfo.json");
    private static final Path EFFECTS_JSON_FILE_PATH = Paths.get("config", Cosmere.MODID, "effectsInfo.json");

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

        // Crear o actualizar el archivo powersInfo.json
        createOrUpdateJsonFile(powersInfo, POWERS_JSON_FILE_PATH, "powersInfo");

        // Crear o actualizar el archivo effectsInfo.json
        JsonObject effectsJson = new JsonObject();
        effectsJson.addProperty("effectsInfo", effectsInfo);
        createOrUpdateJsonFile(effectsJson, EFFECTS_JSON_FILE_PATH, "effectsInfo");

        // Enviar un mensaje al jugador con la información de los poderes y el % del cosmere
        player.sendSystemMessage(Component.literal("Your effects have been checked on login and updated the database. Check it on the web!"));
        player.sendSystemMessage(Component.literal("You have completed " + Math.round(completionPercentage) + "% of advancements"));

    }

    /**
     * Crea o actualiza el archivo JSON con los datos proporcionados.
     * @param jsonObject El objeto JSON con los datos que se van a almacenar.
     * @param filePath La ruta del archivo JSON donde se almacenarán los datos.
     * @param fileType El nombre del tipo de archivo para los logs.
     */
    private static void createOrUpdateJsonFile(JsonObject jsonObject, Path filePath, String fileType) {
        try {
            // Crear el directorio si no existe
            File directory = filePath.getParent().toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Crear o sobrescribir el archivo JSON
            File jsonFile = filePath.toFile();
            try (FileWriter writer = new FileWriter(jsonFile)) {
                // Escribir el objeto JSON al archivo
                writer.write(jsonObject.toString());
                CosmereAPI.logger.info(fileType + " information updated successfully in " + jsonFile.getAbsolutePath());
            }
        } catch (IOException e) {
            CosmereAPI.logger.error("Error while creating/updating " + fileType + ".json", e);
        }
    }
}
