package dev.amble.ait.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PoliceBoxBlock extends BaseEntityBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 4);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 7);
    public static final BooleanProperty ON_SLAB = BooleanProperty.create("on_slab");
    public static final MapCodec<PoliceBoxBlock> CODEC = simpleCodec(PoliceBoxBlock::new);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_SLAB = SHAPE.move(0, -0.5, 0);

    public PoliceBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(TEXTURE, 0)
                .setValue(LIGHT, 7)
                .setValue(ON_SLAB, false));
    }

    public static Properties defaultProps() {
        return Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .strength(50f, 1200f)
                .noOcclusion()
                .dynamicShape()
                .lightLevel(state -> state.getValue(LIGHT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, TEXTURE, LIGHT, ON_SLAB);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = Mth.floor((context.getRotation()) / 45.0f + 0.5f) & 7;
        BlockPos below = context.getClickedPos().below();
        BlockState belowState = context.getLevel().getBlockState(below);

        return this.defaultBlockState()
                .setValue(ROTATION, rotation)
                .setValue(ON_SLAB, isSlabBottom(belowState));
    }

    private boolean isSlabBottom(BlockState state) {
        if (state.getBlock() instanceof SlabBlock
                && state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
            return state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM;
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(ON_SLAB) ? SHAPE_SLAB : SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExteriorBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ExteriorBlockEntity policeBox) {
            policeBox.interact(player.isShiftKeyDown());
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 2);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        level.scheduleTick(pos, this, 2);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (canFallThrough(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            FallingTardisBlockEntity.fall(level, pos, state);
        }
    }

    private static boolean canFallThrough(BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }
}
