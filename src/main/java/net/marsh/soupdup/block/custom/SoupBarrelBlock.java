package net.marsh.soupdup.block.custom;

import com.mojang.serialization.MapCodec;
import net.marsh.soupdup.component.SoupdUpComponentTypes;
import net.marsh.soupdup.block.entity.custom.SoupBarrelBlockEntity;
import net.marsh.soupdup.SoupdUpTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static net.marsh.soupdup.SuspiciousStewHelper.*;

public class SoupBarrelBlock extends BlockWithEntity {
    public static final MapCodec<SoupBarrelBlock> CODEC = createCodec(SoupBarrelBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty SPIGOT = IntProperty.of("spigot", 0, 1);
    private static final List<String> DEFAULT_PARTICLE_COLORS = List.of("#8e5fa5", "#5bf354", "#cd8c6f");

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
        } else if (isSusStew(stack)) {
            return this.susStewInteraction(stack, soupBarrel, serverPlayer, world, pos);
        } else if (isRedstone(stack)) {
            return this.redstoneInteraction(stack, soupBarrel, state, serverPlayer, world, pos);
        } else {
            sendAlert(player, !soupBarrel.isEmpty() ? Text.translatable("block.soup_barrel.alert.contains_message", Text.translatable(soupBarrel.getSoupItem().getTranslationKey()), soupBarrel.getSoupCount()) : Text.translatable("block.soup_barrel.alert.barrel_empty"));
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        return ActionResult.SUCCESS;
    }

    public ActionResult soupInteraction(ItemStack stack, SoupBarrelBlockEntity barrel, ServerPlayerEntity player, World world, BlockPos pos) {
        if (barrel.getSoupStack().isEmpty()) {
            barrel.setSoup(stack);
        } else if (!barrel.getSoupItem().equals(stack.getItem()) || barrel.isFull()) {
            sendAlert(player, Text.translatable(barrel.isFull() ? "block.soup_barrel.alert.barrel_full" : "block.soup_barrel.alert.invalid_soup"));
            world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.75f);
            return ActionResult.PASS;
        } else {
            barrel.increaseSoupCount();
        }

        barrel.markDirty();
        this.exchangeSoup(player, stack, new ItemStack(Items.BOWL));
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, this.soundPitcher(barrel, 0.5f, 0.35f));
        return ActionResult.SUCCESS;
    }

    public ActionResult susStewInteraction(ItemStack stack, SoupBarrelBlockEntity barrel, ServerPlayerEntity player, World world, BlockPos pos) {
        ItemStack barrelSoup = barrel.getSoupStack();

        if (barrelSoup.isEmpty()) {
            barrel.setSoup(stack);
        }
        else if (getSusEffects(stack).size() > 1 && compareEffectsExact(stack, barrelSoup)) {
            barrel.increaseSoupCount();
        }
        else if (!barrel.getSoupItem().equals(stack.getItem())
                || barrel.isFull()
                || getSusEffects(barrelSoup).size() > 2
                || getSusEffects(stack).size() > 1)
        {
            sendAlert(player, Text.translatable(!barrel.getSoupItem().equals(stack.getItem()) ? "block.soup_barrel.alert.invalid_soup" :
                    barrel.isFull() ? "block.soup_barrel.alert.barrel_full" :
                            getSusEffects(barrelSoup).size() > 2 ? "block.soup_barrel.alert.barrel_enough_effects" :
                                    getSusEffects(stack).size() > 1 ? "block.soup_barrel.alert.item_enough_effects" :
                                            null));
            world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 1.0f, 0.75f);
            return ActionResult.PASS;
        }
        else {
            addSusEffect(barrelSoup, getSusEffect(stack, 0), getSusEffectDuration(stack, 0));
            barrel.increaseSoupCount();
        }

        barrel.markDirty();
        this.exchangeSoup(player, stack, new ItemStack(Items.BOWL));
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, this.soundPitcher(barrel, 0.5f, 0.35f));
        return ActionResult.SUCCESS;
    }
    private ActionResult redstoneInteraction(ItemStack stack, SoupBarrelBlockEntity soupBarrel, BlockState state, ServerPlayerEntity player, World world, BlockPos pos) {
        ItemStack barrelSoup = soupBarrel.getSoupStack();
        stack.decrementUnlessCreative(1, player);
        if (!isSusStew(barrelSoup) || isTimeUpgraded(barrelSoup)) {
            sendAlert(player, Text.translatable(!isSusStew(barrelSoup) ? "block.soup_barrel.alert.invalid_soup" : isTimeUpgraded(barrelSoup) ? "block.soup_barrel.alert.already_time_upgraded" : null));
        } else {
            increaseSusEffectsTime(barrelSoup, 1.5f);
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 1.0f, 1.0f);
            spawnCustomParticles(world, state, pos, List.of());
        }
        return ActionResult.SUCCESS;
    }

    public static void spawnCustomParticles(World world, BlockState state, BlockPos pos, @Nullable List<String> particles) {
        Random random = new Random();

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.125;
        double z = pos.getZ() + 0.5;

        Direction direction = state.get(FACING);
        Direction.Axis axis = direction.getAxis();

        double ofx = -0.05 + (0.05 + 0.05) * random.nextDouble();
        double ofz = -0.05 + (0.05 + 0.05) * random.nextDouble();

        double xi = axis == Direction.Axis.X ? direction.getOffsetX() * 0.65 : ofx;
        double zi = axis == Direction.Axis.Z ? direction.getOffsetZ() * 0.65 : ofz;

        for (int i = 0; i < 3; i++) {
            ParticleEffect effect;
            if (particles != null && !particles.isEmpty()) {
                String color = particles.get(random.nextInt(particles.size()));
                int[] rgb = hexToRgb(color);
                effect = EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, rgb[0], rgb[1], rgb[2]);
            } else {
                effect = DustParticleEffect.DEFAULT;
            }
            ((ServerWorld) world).spawnParticles(effect, x + xi, y, z + zi, 0, ofx, 0, ofz, 0);
        }
    }

    private static int[] hexToRgb(String hexColor) {
        int color = (int) Long.parseLong(hexColor.substring(1), 16);
        return new int[] {
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF
        };
    }

    public ActionResult bowlInteraction(ItemStack stack, BlockState state, SoupBarrelBlockEntity barrel, ServerPlayerEntity player, World world, BlockPos pos) {
        this.changeSpigotState(state, world, pos);

        if (barrel.isEmpty()) {
            sendAlert(player, Text.translatable("block.soup_barrel.alert.barrel_empty"));
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 1.0f, 1.25f);
            return ActionResult.SUCCESS;
        }

        this.giveSoup(player, barrel.getSoupStack(), world, pos);
        barrel.decreaseSoupCount();

        barrel.markDirty();
        stack.decrementUnlessCreative(1, player);
        world.playSound(null, pos, SoundEvents.ITEM_HONEY_BOTTLE_DRINK.value(), SoundCategory.BLOCKS, 1.0f, 0.75f);
        return ActionResult.SUCCESS;
    }

    public float soundPitcher(SoupBarrelBlockEntity soupBarrelBlockEntity, float minDiff, float maxDiff) {
        return (minDiff + (maxDiff * ((float) soupBarrelBlockEntity.getSoupCount() / (float) soupBarrelBlockEntity.size())));
    }

    public void exchangeSoup(ServerPlayerEntity player, ItemStack stack, ItemStack bowl) {
        stack.decrementUnlessCreative(1, player);
        if (!player.getGameMode().isCreative()) player.giveOrDropStack(bowl);
    }

    public void giveSoup(ServerPlayerEntity player, ItemStack soupStack, World world, BlockPos pos) {
        ItemStack soup = soupStack.copy();
        soup.setCount(1);
        if (isSusStew(soupStack)) {
            if (getSusEffects(soupStack).size() > 1) {soup.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.soupdup.mixed_suspicious_stew"));}
            spawnCustomParticles(world, world.getBlockState(pos), pos, DEFAULT_PARTICLE_COLORS);
        }
        player.giveOrDropStack(soup);
    }

    public void sendAlert(PlayerEntity player, MutableText alert) {
        player.sendMessage(alert, true);
    }

    public void changeSpigotState(BlockState state, World world, BlockPos pos) {
        int currentState = state.get(SPIGOT);
        world.setBlockState(pos, state.with(SPIGOT, 1 - currentState), Block.NOTIFY_ALL);
    }

    public boolean isSoup(ItemStack stack) {
        return stack.isIn(SoupdUpTags.Items.SOUPS);
    }

    public boolean isSusStew(ItemStack stack) {return stack.isOf(Items.SUSPICIOUS_STEW);}

    public boolean isBowl(ItemStack stack) {
        return stack.getItem() == Items.BOWL;
    }
    public boolean isRedstone(ItemStack stack) {
        return stack.getItem() == Items.REDSTONE;
    }
    public boolean isTimeUpgraded(ItemStack stack) {
        return Boolean.TRUE.equals(stack.getComponents().get(SoupdUpComponentTypes.TIME_UPGRADED));
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
        return this.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    public int calculateComparatorOutput(BlockEntity blockEntity) {
        int power = 0;
        if (blockEntity instanceof SoupBarrelBlockEntity barrel) {
            int[] thresholds = {0, 4, 8, 11, 15, 19, 22, 26, 30, 33, 37, 41, 44, 48, 52, 54};
            for (int i = 0; i < thresholds.length; i++) {
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

    /*@Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(SPIGOT).equals(0) && random.nextDouble() < 0.075) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.125;
            double z = pos.getZ() + 0.5;

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();

            double ofx = -0.05 + (0.05 + 0.05) * random.nextDouble();
            double ofz = -0.05 + (0.05 + 0.05) * random.nextDouble();

            double xi = axis == Direction.Axis.X ? direction.getOffsetX() * 0.65 : ofx;
            double zi = axis == Direction.Axis.Z ? direction.getOffsetZ() * 0.65 : ofz;

            for (int i = 0; i < random.nextInt(1) + 1; i++) {
                String color = DEFAULT_PARTICLE_COLORS.get(random.nextInt(DEFAULT_PARTICLE_COLORS.size()));
                int[] rgb = hexToRgb(color);

                ParticleEffect effect = EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, rgb[0], rgb[1], rgb[2]);

                world.addParticleClient(effect, x + xi, y, z + zi, 0, ofx, 0);
            }
        }
    }*/
}
