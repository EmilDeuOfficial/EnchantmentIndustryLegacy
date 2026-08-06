package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.disenchanter;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiBlockEntities;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiBlocks;

@SuppressWarnings("deprecation")
public class DisenchanterBlock extends Block implements IWrenchable, IBE<DisenchanterBlockEntity> {
    public DisenchanterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<DisenchanterBlockEntity> getBlockEntityClass() {
        return DisenchanterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DisenchanterBlockEntity> getBlockEntityType() {
        return CeiBlockEntities.DISENCHANTER.get();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        return onBlockEntityUse(worldIn, pos, be -> {
            if (!be.getHeldItemStack().isEmpty()) {
                if (!worldIn.isClientSide) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, be.heldItem.stack);
                    be.heldItem = null;
                    be.notifyUpdate();
                }
                return InteractionResult.sidedSuccess(worldIn.isClientSide);
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level worldIn, BlockPos pos, Player player,
            InteractionHand handIn, BlockHitResult hit) {
        if (handIn == InteractionHand.OFF_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (heldItem.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (hit.getDirection() == Direction.UP) {
            return onBlockEntityUseItemOn(worldIn, pos, be -> {
                if (be.getHeldItemStack().isEmpty()) {
                    var insert = heldItem.copy();
                    insert.setCount(1);
                    var result = Disenchanting.disenchantResult(insert, worldIn);
                    if (result != null) {
                        if (!worldIn.isClientSide()) {
                            be.heldItem = new TransportedItemStack(insert);
                            be.notifyUpdate();
                            heldItem.shrink(1);
                        }
                    }
                    return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            });
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos blockPos, CollisionContext pContext) {
        return AllShapes.CASING_13PX.get(Direction.UP);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        var ret = new ArrayList<ItemStack>();
        ret.add(CeiBlocks.DISENCHANTER.asStack());
        return ret;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return ComparatorUtil.levelOfSmartFluidTank(level, pos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
