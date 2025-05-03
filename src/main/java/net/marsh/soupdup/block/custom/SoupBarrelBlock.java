package net.marsh.soupdup.block.custom;

import com.mojang.serialization.MapCodec;
import net.marsh.soupdup.block.entity.custom.SoupBarrelBlockEntity;
import net.marsh.soupdup.SoupdUpTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
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
        if (!(world.getBlockEntity(pos) instanceof SoupBarrelBlockEntity soupBarrel)) {
            return ActionResult.SUCCESS;
        }

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        if (isSoup(stack)) {
            return this.soupInteraction(stack, soupBarrel, serverPlayer, world, pos);
        } else if (isBowl(stack)) {
            return this.bowlInteraction(stack, state, soupBarrel, serverPlayer, world, pos);
        }

        player.sendMessage(Text.of(this.calculateComparatorOutput(world.getBlockEntity(pos)) + ": " + soupBarrel.getSoupCount()), false);

        return ActionResult.SUCCESS;
    }

    public ActionResult soupInteraction(ItemStack stack, SoupBarrelBlockEntity barrel, ServerPlayerEntity player, World world, BlockPos pos) {
        if (barrel.getSoup().isEmpty()) {
            barrel.setSoup(stack.getItem().toString());
            barrel.setSoupCount(1);
        } else if (!barrel.getSoup().equals(stack.getItem().toString()) || barrel.isFull()) {
            world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.75f);
            return ActionResult.PASS;
        } else {
            barrel.addSoupCount(1);
        }

        barrel.markDirty();
        this.exchangeSoup(player, stack, new ItemStack(Items.BOWL));
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, this.soundPitcher(barrel, 0.5f, 0.35f));
        return ActionResult.SUCCESS;
    }

    public ActionResult bowlInteraction(ItemStack stack, BlockState state, SoupBarrelBlockEntity barrel, ServerPlayerEntity player, World world, BlockPos pos) {
        this.changeSpigotState(state, world, pos);

        if (barrel.isEmpty()) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 1.0f, 1.25f);
            return ActionResult.SUCCESS;
        }

        this.giveSoup(player, barrel.getSoup());
        barrel.removeSoupCount(1);
        if (barrel.isEmpty()) {
            barrel.setSoup("");
        }

        barrel.markDirty();
        stack.splitUnlessCreative(1, player);
        world.playSound(null, pos, SoundEvents.ITEM_HONEY_BOTTLE_DRINK.value(), SoundCategory.BLOCKS, 1.0f, 0.75f);
        return ActionResult.SUCCESS;
    }
    public float soundPitcher(SoupBarrelBlockEntity soupBarrelBlockEntity, float start, float maxDiff) {
        return (start + (maxDiff * ((float) soupBarrelBlockEntity.getSoupCount() / (float) soupBarrelBlockEntity.size())));
    }
    public void exchangeSoup(ServerPlayerEntity player, ItemStack stack, ItemStack bowl) {
        int selectedSlot = player.getInventory().getSelectedSlot();
        stack.splitUnlessCreative(1, player);

        if (!player.getGameMode().isCreative()) {
            boolean wasAdded = player.getInventory().insertStack(selectedSlot, bowl);
            if (!wasAdded) {
                player.dropItem(bowl, false);
            }
        }
    }
    public void giveSoup(ServerPlayerEntity player, String soup_string) {
        Identifier soup_id = Identifier.of(soup_string);
        ItemStack soup = Registries.ITEM.get(soup_id).getDefaultStack();

        boolean wasAdded = player.getInventory().insertStack(soup);
        if (!wasAdded) {
            player.dropItem(soup, false);
        }
    }
    public void changeSpigotState(BlockState state, World world, BlockPos pos) {
        switch (state.get(SPIGOT)) {
            case 0:
                world.setBlockState(pos, state.with(SPIGOT, 1), Block.NOTIFY_ALL); break;
            case 1:
                world.setBlockState(pos, state.with(SPIGOT, 0), Block.NOTIFY_ALL); break;
        }
    }

    public boolean isSoup(ItemStack stack) {
        return stack.isIn(SoupdUpTags.Items.SOUPS);
    }
    public boolean isBowl(ItemStack stack) {
        return stack.getItem() == Items.BOWL;
    }
    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(SPIGOT, 0).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return calculateComparatorOutput(world.getBlockEntity(pos));
    }

    public int calculateComparatorOutput(BlockEntity blockEntity) {
        int power = 0;
        if (blockEntity instanceof SoupBarrelBlockEntity barrel) {
            int[] thresholds = {0, 4, 8, 11, 15, 19, 22, 26, 30, 33, 37, 41, 44, 48, 52, 54};
            for (int i = 1; i < thresholds.length; i++) {
                if (barrel.getSoupCount() <= thresholds[i]) {
                    return i;
                }
            }
            return 15;
        }
        return power;
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
