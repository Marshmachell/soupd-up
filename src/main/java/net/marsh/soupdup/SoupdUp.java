package net.marsh.soupdup;

import net.fabricmc.api.ModInitializer;

import net.marsh.soupdup.block.SoupdUpBlocks;
import net.marsh.soupdup.block.entity.SoupdUpBlockEntities;
import net.marsh.soupdup.component.SoupdUpComponentTypes;
import net.marsh.soupdup.item.SoupdUpItemGroups;
import net.marsh.soupdup.item.SoupdUpItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoupdUp implements ModInitializer {
	public static final String MOD_ID = "soupdup";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SoupdUpItemGroups.registerItemGroups();
		SoupdUpItems.registerModItems();
		SoupdUpBlocks.registerModBlocks();
		SoupdUpBlockEntities.registerBlockEntities();
		SoupdUpComponentTypes.registerDataComponentTypes();

		//DispenserBlock.registerBehavior(Items.BOWL.asItem(), new FallibleItemDispenserBehavior() {
		//	private ItemStack pickUpFluid(BlockPointer pointer, ItemStack inputStack, ItemStack outputStack) {
		//		pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pointer.pos());
		//		return this.decrementStackWithRemainder(pointer, inputStack, outputStack);
		//	}
//
		//	@Override
		//	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		//		this.setSuccess(false);
		//		ServerWorld serverWorld = pointer.world();
		//		BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
		//		BlockState blockState = serverWorld.getBlockState(blockPos);
		//		if (blockState.isIn(SoupdUpTags.Blocks.SOUP_BARRELS, state -> state.contains(SoupBarrelBlock.SPIGOT) && state.getBlock() instanceof SoupBarrelBlock)
		//				&& (Integer) blockState.getBlock(). >= 1) {
		//			((BeehiveBlock)blockState.getBlock()).takeHoney(serverWorld, blockState, blockPos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
		//			this.setSuccess(true);
		//			return this.pickUpFluid(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
		//		} else if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
		//			this.setSuccess(true);
		//			return this.pickUpFluid(pointer, stack, PotionContentsComponent.createStack(Items.POTION, Potions.WATER));
		//		} else {
		//			return super.dispenseSilently(pointer, stack);
		//		}
		//	}
		//});
	}
}