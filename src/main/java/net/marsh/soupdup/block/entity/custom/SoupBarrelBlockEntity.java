package net.marsh.soupdup.block.entity.custom;

import com.mojang.serialization.Codec;
import net.marsh.soupdup.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SoupBarrelBlockEntity extends BlockEntity{
    private String soup = "";
    private int soup_count = 0;
    public SoupBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUP_BARREL_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (!this.soup.isEmpty()) {
            nbt.put("soup", Codec.STRING, this.soup);
            nbt.put("soup_count", Codec.INT, this.soup_count);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.soup = nbt.get("soup", Codec.STRING).orElse("");
        this.soup_count = nbt.get("soup_count", Codec.INT).orElse(0);
    }

    public String getSoup() {
        return this.soup;
    }
    public int getSoupCount() {
        return this.soup_count;
    }
    public void setSoup(String soup) {
        this.soup = soup;
    }
    public void setSoupCount(Integer soup_count) {
        this.soup_count = soup_count;
    }
    public void addSoupCount(int soup_count) {
        this.soup_count += soup_count;
    }
    public void removeSoupCount(int soup_count) {
        this.soup_count -= soup_count;
    }
    public boolean isEmpty() {
        return this.soup_count <= 0;
    }
    public boolean isFull() {
        return this.soup_count >= this.size();
    }

    public int size() {
        return 54;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
