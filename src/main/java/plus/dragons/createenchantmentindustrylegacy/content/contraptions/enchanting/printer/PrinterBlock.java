package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.printer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
public class PrinterBlock extends Block implements IWrenchable, IBE<PrinterBlockEntity> {
    public PrinterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext pContext) {
        return AllShapes.SPOUT;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        var ret = new ArrayList<ItemStack>();
        ret.add(CeiBlocks.PRINTER.asStack());
        return ret;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player,
            BlockHitResult blockRayTraceResult) {
        return onBlockEntityUse(world, pos, be -> {
            if (!be.getCopyTarget().isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, be.getCopyTarget());
                be.setCopyTarget(ItemStack.EMPTY);
                playTargetSound(world, pos, player, false);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level world, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult blockRayTraceResult) {
        if (hand == InteractionHand.OFF_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (heldItem.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var copy = heldItem.copy();
        copy.setCount(1);
        if (Printing.match(copy) != null) {
            return onBlockEntityUseItemOn(world, pos, be -> {
                boolean consumed = !player.getAbilities().instabuild;
                if (consumed)
                    heldItem.shrink(1);
                // setCopyTarget replaces the field, so the old stack can be handed over as is.
                ItemStack previousTarget = be.getCopyTarget();
                be.setCopyTarget(copy);
                // Only give the previous target back when the new one was actually taken from the
                // player. In creative nothing is consumed, so returning it would mint a free item
                // on every click.
                if (consumed && !previousTarget.isEmpty()) {
                    if (heldItem.isEmpty())
                        player.setItemInHand(hand, previousTarget);
                    else if (!player.addItem(previousTarget))
                        player.drop(previousTarget, false, true);
                }
                playTargetSound(world, pos, player, true);
                return ItemInteractionResult.SUCCESS;
            });
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * The printer has no visual for its copy target, so without this the interaction is silent and
     * indistinguishable from a click that did nothing. Passing the player makes the server skip them
     * and the client play it locally, so it is heard exactly once on both sides.
     */
    private static void playTargetSound(Level world, BlockPos pos, Player player, boolean inserted) {
        world.playSound(player, pos, inserted ? SoundEvents.ITEM_FRAME_ADD_ITEM : SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                SoundSource.BLOCKS, 0.7f, inserted ? 1.1f : 0.9f);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
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

    @Override
    public Class<PrinterBlockEntity> getBlockEntityClass() {
        return PrinterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PrinterBlockEntity> getBlockEntityType() {
        return CeiBlockEntities.PRINTER.get();
    }
}
