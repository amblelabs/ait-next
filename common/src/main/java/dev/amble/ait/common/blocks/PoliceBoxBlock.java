package dev.amble.ait.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PoliceBoxBlock extends BaseEntityBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 4);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 7);
    public static final BooleanProperty ON_SLAB = BooleanProperty.create("on_slab");
    public static final MapCodec<PoliceBoxBlock> CODEC = simpleCodec(PoliceBoxBlock::new);

    // ── Cardinal shapes ──────────────────────────────────────────────────────
    // Each shape is 16 wide × 32 tall (≈2 blocks) × 12 deep, with a 1 px high
    // ledge covering the front 4 px strip.
    // Convention: ROTATION 0 = player placed while facing South → front = south (+Z).

    /** R0 – front faces south (+Z): body backs against north, ledge at south. */
    private static final VoxelShape SHAPE_R0 = Shapes.or(
            Block.box(0, 0,  0, 16, 32, 12),   // main body (back 12 px)
            Block.box(0, 0, 12, 16,  1, 16));   // front ledge (1 px high, 4 px deep)

    /** R2 – front faces west (−X): body backs against east, ledge at west. */
    private static final VoxelShape SHAPE_R2 = Shapes.or(
            Block.box( 4, 0, 0, 16, 32, 16),
            Block.box( 0, 0, 0,  4,  1, 16));

    /** R4 – front faces north (−Z): body backs against south, ledge at north. */
    private static final VoxelShape SHAPE_R4 = Shapes.or(
            Block.box(0, 0, 4, 16, 32, 16),
            Block.box(0, 0, 0, 16,  1,  4));

    /** R6 – front faces east (+X): body backs against west, ledge at east. */
    private static final VoxelShape SHAPE_R6 = Shapes.or(
            Block.box(0, 0, 0, 12, 32, 16),
            Block.box(12, 0, 0, 16,  1, 16));

    // ── Diagonal shapes (approximated) ───────────────────────────────────────
    // Each diagonal is the union of the two adjacent cardinal bodies, leaving
    // just the opposite corner open as a 1 px ledge.

    /** R1 – front faces SW (back = NE corner). */
    private static final VoxelShape SHAPE_R1 = Shapes.or(
            Shapes.or(
                    Block.box(0, 0,  0, 16, 32, 12),   // R0 body
                    Block.box(4, 0, 12, 16, 32, 16)),  // R2 body extension (NE fill)
            Block.box(0, 0, 12,  4,  1, 16));          // SW ledge

    /** R3 – front faces NW (back = SE corner). */
    private static final VoxelShape SHAPE_R3 = Shapes.or(
            Shapes.or(
                    Block.box(4, 0, 0, 16, 32, 16),    // R2 body
                    Block.box(0, 0, 4,  4, 32, 16)),   // R4 body extension (SE fill)
            Block.box(0, 0, 0,  4,  1,  4));           // NW ledge

    /** R5 – front faces NE (back = SW corner). */
    private static final VoxelShape SHAPE_R5 = Shapes.or(
            Shapes.or(
                    Block.box( 0, 0, 4, 16, 32, 16),   // R4 body
                    Block.box( 0, 0, 0, 12, 32,  4)),  // R6 body extension (SW fill)
            Block.box(12, 0, 0, 16,  1,  4));          // NE ledge

    /** R7 – front faces SE (back = NW corner). */
    private static final VoxelShape SHAPE_R7 = Shapes.or(
            Shapes.or(
                    Block.box( 0, 0,  0, 12, 32, 16),  // R6 body
                    Block.box(12, 0,  0, 16, 32, 12)), // R0 body extension (NW fill)
            Block.box(12, 0, 12, 16,  1, 16));         // SE ledge

    // ── Lookup arrays ─────────────────────────────────────────────────────────

    private static final VoxelShape[] SHAPES = {
            SHAPE_R0, SHAPE_R1, SHAPE_R2, SHAPE_R3,
            SHAPE_R4, SHAPE_R5, SHAPE_R6, SHAPE_R7
    };

    /** Slab variants: each shape shifted 0.5 blocks down so the box sits on the slab's top surface. */
    private static final VoxelShape[] SLAB_SHAPES;

    static {
        SLAB_SHAPES = new VoxelShape[8];
        for (int i = 0; i < 8; i++) {
            SLAB_SHAPES[i] = SHAPES[i].move(0, -0.5, 0);
        }
    }

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
        // Geometry in SHAPES is defined 180 degrees opposite the renderer's base orientation.
        int rotation = (state.getValue(ROTATION) + 4) & 7;
        return state.getValue(ON_SLAB) ? SLAB_SHAPES[rotation] : SHAPES[rotation];
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
