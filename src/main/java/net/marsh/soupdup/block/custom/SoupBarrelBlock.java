package net.marsh.soupdup.block.custom;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.marsh.soupdup.block.ModBlocks;
import net.marsh.soupdup.block.entity.custom.SoupBarrelBlockEntity;
import net.marsh.soupdup.item.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoupBarrelBlock extends BlockWithEntity {
    public static final MapCodec<SoupBarrelBlock> CODEC = createCodec(SoupBarrelBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty SPIGOT = IntProperty.of("spigot", 0, 1);

    @Override
    public MapCodec<SoupBarrelBlock> getCodec() {
        return CODEC;
    }
    public SoupBarrelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SPIGOT, 0).with(FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof SoupBarrelBlockEntity soupBarrelBlockEntity) {
            if (world.isClient) {
                return ActionResult.SUCCESS;
            } else {
                if (isSoup(stack)) {
                    soupBarrelBlockEntity.markDirty();
                    if (soupBarrelBlockEntity.getSoup().isEmpty()) {
                        soupBarrelBlockEntity.setSoup(stack.getItem().toString());
                        soupBarrelBlockEntity.setSoupCount(1);
                        exchangeSoup((ServerPlayerEntity) player, stack, new ItemStack(Items.BOWL));
                        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 0.7f);
                        return ActionResult.SUCCESS;
                    } else {
                        if (soupBarrelBlockEntity.getSoup().equals(stack.getItem().toString())) {
                            if (soupBarrelBlockEntity.getSoupCount() >= soupBarrelBlockEntity.size()) {
                                world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.75f);
                                return ActionResult.PASS;
                            } else {
                                soupBarrelBlockEntity.addSoupCount(1);
                                exchangeSoup((ServerPlayerEntity) player, stack, new ItemStack(Items.BOWL));
                                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 0.7f);
                                return ActionResult.SUCCESS;
                            }
                        } else {
                            world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.75f);
                            return ActionResult.PASS;
                        }
                    }
                } else if (isBowl(stack)) {
                    soupBarrelBlockEntity.markDirty();
                    changeSpigotState(state, world, pos);
                    if (!soupBarrelBlockEntity.getSoup().isEmpty() && soupBarrelBlockEntity.getSoupCount() > 0) {
                        giveSoup((ServerPlayerEntity) player, soupBarrelBlockEntity.getSoup());
                        soupBarrelBlockEntity.removeSoupCount(1);
                        if (soupBarrelBlockEntity.getSoupCount() == 0) {
                            soupBarrelBlockEntity.setSoup("");
                        }
                        stack.splitUnlessCreative(1, player);
                        world.playSound(null, pos, SoundEvents.ITEM_HONEY_BOTTLE_DRINK.value(), SoundCategory.BLOCKS, 1.0f, 0.75f);
                        return ActionResult.SUCCESS;
                    } else {
                        world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 1.0f, 1.25f);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }
    private void exchangeSoup(ServerPlayerEntity player, ItemStack stack, ItemStack bowl) {
        stack.splitUnlessCreative(1, player);

        if (!player.getGameMode().isCreative()) {
            boolean wasAdded = player.getInventory().insertStack(bowl);
            if (!wasAdded) {
                player.dropItem(bowl, false);
            }
        }
    }
    private void giveSoup(ServerPlayerEntity player, String soup_string) {
        Identifier soup_id = Identifier.of(soup_string);
        ItemStack soup = Registries.ITEM.get(soup_id).getDefaultStack();
        boolean wasAdded = player.getInventory().insertStack(soup);

        if (!wasAdded) {
            player.dropItem(soup, false);
        }
    }
    private void changeSpigotState(BlockState state, World world, BlockPos pos) {
        switch (state.get(SPIGOT)) {
            case 0:
                world.setBlockState(pos, state.with(SPIGOT, 1), Block.NOTIFY_ALL); break;
            case 1:
                world.setBlockState(pos, state.with(SPIGOT, 0), Block.NOTIFY_ALL); break;
        }
    }

    private boolean isSoup(ItemStack stack) {
        return stack.isIn(ModTags.Items.SOUPS);
    }
    private boolean isBowl(ItemStack stack) {
        return stack.getItem() == Items.BOWL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(SPIGOT, 0).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoupBarrelBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SPIGOT, FACING);
    }
}
