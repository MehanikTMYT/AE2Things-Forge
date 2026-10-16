package io.github.projectet.ae2things.client;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.AETItems;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import appeng.api.client.StorageCellModels;

@Mod(value = AE2Things.MOD_ID, dist = Dist.CLIENT)
public class AE2ThingsClient {
    public AE2ThingsClient(IEventBus eventBus) {
        eventBus.addListener(AE2ThingsClient::registerItemTintSources);
        eventBus.addListener(AE2ThingsClient::registerCellModels);
    }

    // Fleet port (26.1): item colors are ItemTintSources (see items/*.json).
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(DiskStateTintSource.ID, DiskStateTintSource.MAP_CODEC);
    }

    // Fleet port (26.1): StorageCellModels is client-only; register the drive cell models where AE2 registers its own,
    // before AE2 turns them into standalone models.
    public static void registerCellModels(InitializeClientRegistriesEvent event) {
        StorageCellModels.registerModel(AETItems.DISK_DRIVE_1K.get(), AETItems.MODEL_DISK_DRIVE_1K);
        StorageCellModels.registerModel(AETItems.DISK_DRIVE_4K.get(), AETItems.MODEL_DISK_DRIVE_4K);
        StorageCellModels.registerModel(AETItems.DISK_DRIVE_16K.get(), AETItems.MODEL_DISK_DRIVE_16K);
        StorageCellModels.registerModel(AETItems.DISK_DRIVE_64K.get(), AETItems.MODEL_DISK_DRIVE_64K);
        StorageCellModels.registerModel(AETItems.DISK_DRIVE_256K.get(), AETItems.MODEL_DISK_DRIVE_256K);
    }
}
