package dev.amble.ait.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionHand;
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

public class DoorBlock extends BaseEntityBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 4);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 7);
    public static final BooleanProperty ON_SLAB = BooleanProperty.create("on_slab");
    public static final BooleanProperty BETWEEN = BooleanProperty.create("between");
    public static final MapCodec<DoorBlock> CODEC = simpleCodec(DoorBlock::new);

    public static final double[][] BETWEEN_OFFSETS = {
            { 0.5,   0,  0},
            { 0.354, 0,  0.354},
            { 0,     0,  0.5},
            { 0.354, 0, -0.354},
            { 0.5,   0,  0},
            { 0.354, 0,  0.354},
            { 0,     0,  0.5},
            { 0.354, 0, -0.354}
    };

    private static final VoxelShape[] SHAPES = new VoxelShape[8];
    private static final VoxelShape[] SHAPES_SLAB = new VoxelShape[8];
    private static final VoxelShape[] SHAPES_BETWEEN = new VoxelShape[8];
    private static final VoxelShape[] SHAPES_BETWEEN_SLAB = new VoxelShape[8];

    static {
        SHAPES[0] = Block.box(0, 0, 0, 16, 39, 5);
        SHAPES[1] = Block.box(4, 0, 4, 16, 39, 16);
        SHAPES[2] = Block.box(11, 0, 0, 16, 39, 16);
        SHAPES[3] = Block.box(4, 0, 0, 16, 39, 12);
        SHAPES[4] = Block.box(0, 0, 11, 16, 39, 16);
        SHAPES[5] = Block.box(0, 0, 0, 12, 39, 12);
        SHAPES[6] = Block.box(0, 0, 0, 5, 39, 16);
        SHAPES[7] = Block.box(0, 0, 4, 12, 39, 16);

        for (int i = 0; i < 8; i++) {
            SHAPES_SLAB[i] = SHAPES[i].move(0, -0.5, 0);
            SHAPES_BETWEEN[i] = SHAPES[i].move(BETWEEN_OFFSETS[i][0], 0, BETWEEN_OFFSETS[i][2]);
            SHAPES_BETWEEN_SLAB[i] = SHAPES_BETWEEN[i].move(0, -0.5, 0);
        }
    }

    public DoorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(TEXTURE, 0)
                .setValue(LIGHT, 7)
                .setValue(ON_SLAB, false)
                .setValue(BETWEEN, false));
    }

    public static Properties defaultProps() {
        return Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .strength(50f, 1200f)
                .noOcclusion()
                .dynamicShape()
                .noCollission()
                .lightLevel(state -> state.getValue(LIGHT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, TEXTURE, LIGHT, ON_SLAB, BETWEEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = (Mth.floor((context.getRotation()) / 45.0f + 0.5f) + 4) & 7;
        BlockPos below = context.getClickedPos().below();
        BlockState belowState = context.getLevel().getBlockState(below);
        boolean crouching = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();

        return this.defaultBlockState()
                .setValue(ROTATION, rotation)
                .setValue(ON_SLAB, isSlabBottom(belowState))
                .setValue(BETWEEN, crouching);
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
        int rotation = state.getValue(ROTATION);
        boolean slab = state.getValue(ON_SLAB);
        boolean between = state.getValue(BETWEEN);

        if (between && slab) return SHAPES_BETWEEN_SLAB[rotation];
        if (between) return SHAPES_BETWEEN[rotation];
        if (slab) return SHAPES_SLAB[rotation];
        return SHAPES[rotation];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoorBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.DEBUG_STICK)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DoorBlockEntity door) {
            if (player.isShiftKeyDown()) {
                door.cycleTextureVariant();
            } else {
                door.cycleModelVariant();
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DoorBlockEntity door) {
            door.interact(player.isShiftKeyDown());
        }

        return InteractionResult.CONSUME;
    }
}
