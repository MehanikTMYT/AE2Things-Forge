package io.github.projectet.ae2things.client;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.DISKDrive;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Fleet port (26.1): item colors are tint sources referenced from items/*.json. Same LED logic as the 1.21
 * RegisterColorHandlersEvent.Item handler (read-only client status, no server-side storage manager).
 */
public record DiskStateTintSource() implements ItemTintSource {
    public static final Identifier ID = AE2Things.id("disk_state");
    public static final MapCodec<DiskStateTintSource> MAP_CODEC = MapCodec.unit(DiskStateTintSource::new);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return DISKDrive.getColor(stack, 1);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
