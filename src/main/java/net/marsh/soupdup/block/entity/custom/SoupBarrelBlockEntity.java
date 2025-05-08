package net.marsh.soupdup.block.entity.custom;

import net.marsh.soupdup.block.entity.SoupdUpBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SoupBarrelBlockEntity extends BlockEntity {
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

    public ItemStack getSoupStack() {
        return this.soup;
    }
    public Item getSoupItem() {
        return this.soup.getItem();
    }
    public int getSoupCount() {
        return this.soup.getCount();
    }
    public void setSoup(ItemStack soup) {
        this.soup = soup.copy();
    }
    public void setSoupCount(Integer soup_count) {
        this.soup.setCount(soup_count);
    }
    public void increaseSoupCount() {
        soup.setCount(this.getSoupCount() + 1);
    }
    public void decreaseSoupCount() {
        soup.setCount(soup.getCount() - 1);
        if (this.soup.isEmpty()) {
            this.setSoup(ItemStack.EMPTY);
        }
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
