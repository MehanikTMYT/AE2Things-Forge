package io.github.projectet.ae2things.util;

import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.resources.Identifier;
import net.minecraft.core.UUIDUtil;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class StorageManager extends SavedData {
    // Fleet port (26.1): SavedData is registered by SavedDataType (id = file name) with a codec instead of a Factory.
    private static final SavedDataType<StorageManager> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("ae2things", Constants.MANAGER_NAME), StorageManager::new,
            CompoundTag.CODEC.xmap(StorageManager::readNbt, StorageManager::save));
    private static final String DISKUUID = "disk_id";
    private final Map<UUID, DataStorage> disks;
    @Nullable
    private WeakReference<HolderLookup.Provider> registries;

    public StorageManager() {
        disks = new HashMap<>();
        this.setDirty();
    }

    private StorageManager(Map<UUID, DataStorage> disks) {
        this.disks = disks;
        this.setDirty();
    }

    private CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        ListTag diskList = new ListTag();
        for (Map.Entry<UUID, DataStorage> entry : disks.entrySet()) {
            CompoundTag disk = new CompoundTag();

            disk.store(DISKUUID, UUIDUtil.CODEC, entry.getKey());
            disk.put(Constants.DISKDATA, entry.getValue().toNbt());
            diskList.add(disk);
        }

        nbt.put(Constants.DISKLIST, diskList);
        return nbt;
    }

    public static StorageManager readNbt(CompoundTag nbt) {
        Map<UUID, DataStorage> disks = new HashMap<>();
        ListTag diskList = nbt.getListOrEmpty(Constants.DISKLIST);
        for (int i = 0; i < diskList.size(); i++) {
            CompoundTag disk = diskList.getCompoundOrEmpty(i);
            disks.put(disk.read(DISKUUID, UUIDUtil.CODEC).orElseThrow(), DataStorage.fromNbt(disk.getCompoundOrEmpty(Constants.DISKDATA)));
        }
        return new StorageManager(disks);
    }

    public void updateDisk(UUID uuid, DataStorage dataStorage) {
        disks.put(uuid, dataStorage);
        setDirty();
    }

    public void removeDisk(UUID uuid) {
        disks.remove(uuid);
        setDirty();
    }

    public boolean hasUUID(UUID uuid) {
        return disks.containsKey(uuid);
    }

    public DataStorage getOrCreateDisk(UUID uuid) {
        if (!disks.containsKey(uuid)) {
            updateDisk(uuid, new DataStorage());
        }
        return disks.get(uuid);
    }

    public void modifyDisk(UUID diskID, ListTag stackKeys, long[] stackAmounts, long itemCount) {
        DataStorage diskToModify = getOrCreateDisk(diskID);
        if (stackKeys != null && stackAmounts != null) {
            diskToModify.stackKeys = stackKeys;
            diskToModify.stackAmounts = stackAmounts;
        }
        diskToModify.itemCount = itemCount;

        updateDisk(diskID, diskToModify);
    }

    public static StorageManager getInstance(MinecraftServer server) {
        ServerLevel world = server.getLevel(ServerLevel.OVERWORLD);
        var manager = world.getDataStorage().computeIfAbsent(TYPE);
        manager.registries = new WeakReference<>(server.registryAccess());
        return manager;
    }

    public HolderLookup.Provider getRegistries() {
        var r = this.registries;
        if (r == null) {
            throw new IllegalStateException("StorageManager was not initialized properly.");
        }

        var registries = r.get();
        if (registries == null) {
            throw new IllegalStateException("Using a StorageManager whose server was already closed");
        }

        return registries;
    }
}
