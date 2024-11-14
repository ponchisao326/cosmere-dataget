package leaf.cosmere.common.network.modifications;

import leaf.cosmere.common.Cosmere;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = Cosmere.MODID)
public class AdvancementUtils {

    public static double getAdvancementCompletionPercentage(ServerPlayer player) {
        // Obtén todos los avances del jugador
        Collection<Advancement> advancements = player.server.getAdvancements().getAllAdvancements();
        int completed = 0;
        int total = 0;

        for (Advancement advancement : advancements) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (progress.isDone()) {
                completed++;
            }
            total++;
        }

        // Evitar división por cero
        return total == 0 ? 0 : ((double) completed / total) * 100;
    }
}