package net.marsh.soupdup.block.entity.custom;

import com.mojang.serialization.Codec;
import net.marsh.soupdup.block.entity.SoupdUpBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SoupBarrelBlockEntity extends BlockEntity{
    private ItemStack soup = ItemStack.EMPTY;
    public SoupBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(SoupdUpBlockEntities.SOUP_BARREL_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (!this.soup.isEmpty()) {
            nbt.put("item", ItemStack.CODEC, this.soup);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.soup = nbt.get("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    public ItemStack getSoup() {
        return this.soup;
    }
    public ItemStack getSoupType() {
        return this.soup;
    }
    public int getSoupCount() {
        return this.soup.getCount();
    }
    public void setSoup(ItemStack soup) {
        System.out.println("balls");
        this.soup = soup;
    }
    public void setSoupCount(Integer soup_count) {
        this.soup.setCount(soup_count);
    }
    public void addSoupCount(int soup_count) {
        soup.setCount(this.getSoupCount() + soup_count);
    }
    public void removeSoupCount(int soup_count) {
        this.soup.setCount(soup.getCount() - soup_count);
    }
    public boolean isEmpty() {
        return this.soup.getCount() <= 0;
    }
    public boolean isFull() {
        return this.soup.getCount() >= this.size();
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
