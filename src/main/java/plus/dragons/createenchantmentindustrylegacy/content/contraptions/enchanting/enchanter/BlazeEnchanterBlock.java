package plus.dragons.createenchantmentindustrylegacy.content.contraptions.enchanting.enchanter;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiBlockEntities;
import plus.dragons.createenchantmentindustrylegacy.entry.CeiItems;

@SuppressWarnings("deprecation")
public class BlazeEnchanterBlock extends HorizontalDirectionalBlock implements IWrenchable, IBE<BlazeEnchanterBlockEntity> {
    public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", HeatLevel.class);

    public static final MapCodec<BlazeEnchanterBlock> CODEC = simpleCodec(BlazeEnchanterBlock::new);

    public BlazeEnchanterBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, HeatLevel.SMOULDERING));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public Class<BlazeEnchanterBlockEntity> getBlockEntityClass() {
        return BlazeEnchanterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlazeEnchanterBlockEntity> getBlockEntityType() {
        return CeiBlockEntities.BLAZE_ENCHANTER.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT_LEVEL, FACING);
    }

    @Override
    public Item asItem() {
        return AllBlocks.BLAZE_BURNER.get().asItem();
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos blockPos, CollisionContext pContext) {
        return AllShapes.HEATER_BLOCK_SHAPE;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!worldIn.isClientSide() && player instanceof ServerPlayer serverPlayer
                    && worldIn.getBlockEntity(pos) instanceof BlazeEnchanterBlockEntity blazeEnchanter) {
                serverPlayer.openMenu(blazeEnchanter, buf -> {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, blazeEnchanter.targetItem);
                    buf.writeBoolean(false);
                    buf.writeBlockPos(pos);
                });
            }
            return InteractionResult.SUCCESS;
        }
        return onBlockEntityUse(worldIn, pos, te -> {
            ItemStack heldItemStack = te.getHeldItemStack();
            if (!heldItemStack.isEmpty()) {
                if (!worldIn.isClientSide) {
                    te.heldItem = null;
                    player.setItemInHand(InteractionHand.MAIN_HAND, heldItemStack);
                    te.notifyUpdate();
                }
                return InteractionResult.SUCCESS;
            }
            if (!te.goggles)
                return InteractionResult.PASS;
            te.goggles = false;
            te.notifyUpdate();
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player,
            InteractionHand handIn, BlockHitResult hit) {
        if (handIn == InteractionHand.OFF_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemStack heldItem = player.isCreative() ? player.getItemInHand(handIn).copy() : player.getItemInHand(handIn);
        if (heldItem.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        return onBlockEntityUseItemOn(worldIn, pos, te -> {
                if (heldItem.is(CeiItems.ENCHANTING_GUIDE.get())) {
                    if (!worldIn.isClientSide) {
                        var target = te.targetItem.copy();
                        te.targetItem = heldItem;
                        if (!player.getAbilities().instabuild)
                            player.setItemInHand(handIn, target);
                        te.notifyUpdate();
                    }
                    return ItemInteractionResult.SUCCESS;
                } else if (Enchanting.getValidEnchantment(heldItem, te.targetItem, te.hyper()) != null) {
                    ItemStack heldItemStack = te.getHeldItemStack();
                    if (heldItemStack.isEmpty()) {
                        if (!worldIn.isClientSide) {
                            te.heldItem = new TransportedItemStack(heldItem);
                            if (!player.getAbilities().instabuild)
                                player.setItemInHand(handIn, ItemStack.EMPTY);
                            te.notifyUpdate();
                        }
                        return ItemInteractionResult.SUCCESS;
                    }
                    return ItemInteractionResult.FAIL;
                } else if (AllItems.GOGGLES.isIn(heldItem)) {
                    if (te.goggles)
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    te.goggles = true;
                    te.notifyUpdate();
                    return ItemInteractionResult.SUCCESS;
                } else
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (world instanceof ServerLevel) {
            if (player != null)
                player.level().setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState()
                        .setValue(BlazeBurnerBlock.FACING, state.getValue(FACING))
                        .setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(AllBlocks.BLAZE_BURNER.get());
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        var ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(AllBlocks.BLAZE_BURNER.get()));
        return ret;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return ComparatorUtil.levelOfSmartFluidTank(worldIn, pos);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public static int getLight(BlockState state) {
        HeatLevel level = state.getValue(HEAT_LEVEL);
        return switch (level) {
            case SEETHING -> 15;
            case KINDLED -> 11;
            case SMOULDERING -> 7;
        };
    }

    public enum HeatLevel implements StringRepresentable {
        SMOULDERING, KINDLED, SEETHING,;

        public static HeatLevel byIndex(int index) {
            return values()[index];
        }

        public HeatLevel nextActiveLevel() {
            return byIndex(ordinal() % (values().length - 1) + 1);
        }

        public boolean isAtLeast(HeatLevel heatLevel) {
            return this.ordinal() >= heatLevel.ordinal();
        }

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }
}
