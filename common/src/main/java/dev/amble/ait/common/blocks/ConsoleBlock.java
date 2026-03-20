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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConsoleBlock extends BaseEntityBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 15);
    public static final BooleanProperty ON_SLAB = BooleanProperty.create("on_slab");
    public static final BooleanProperty BETWEEN = BooleanProperty.create("between");
    public static final IntegerProperty BETWEEN_CORNER = IntegerProperty.create("between_corner", 0, 3);
    public static final MapCodec<ConsoleBlock> CODEC = simpleCodec(ConsoleBlock::new);

    public static final double[][] BETWEEN_OFFSETS = {
            { -0.5, -0.5 },
            {  0.5, -0.5 },
            { -0.5,  0.5 },
            {  0.5,  0.5 }
    };

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_SLAB = SHAPE.move(0, -0.5, 0);
    private static final VoxelShape[] SHAPES_BETWEEN = new VoxelShape[4];
    private static final VoxelShape[] SHAPES_BETWEEN_SLAB = new VoxelShape[4];

    static {
        for (int i = 0; i < 4; i++) {
            SHAPES_BETWEEN[i] = SHAPE.move(BETWEEN_OFFSETS[i][0], 0, BETWEEN_OFFSETS[i][1]);
            SHAPES_BETWEEN_SLAB[i] = SHAPES_BETWEEN[i].move(0, -0.5, 0);
        }
    }

    public ConsoleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(LIGHT, 0)
                .setValue(ON_SLAB, false)
                .setValue(BETWEEN, false)
                .setValue(BETWEEN_CORNER, 3));
    }

    public static Properties defaultProps() {
        return Properties.of()
                .mapColor(MapColor.COLOR_CYAN)
                .strength(5f, 6f)
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
        builder.add(ROTATION, LIGHT, ON_SLAB, BETWEEN, BETWEEN_CORNER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = Mth.floor((context.getRotation()) / 45.0f + 0.5f) & 7;
        BlockPos below = context.getClickedPos().below();
        BlockState belowState = context.getLevel().getBlockState(below);
        boolean crouching = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        int betweenCorner = getBetweenCorner(context);

        return this.defaultBlockState()
                .setValue(ROTATION, rotation)
                .setValue(ON_SLAB, isSlabBottom(belowState))
                .setValue(BETWEEN, crouching)
                .setValue(BETWEEN_CORNER, betweenCorner);
    }

    private static int getBetweenCorner(BlockPlaceContext context) {
        Vec3 clickPos = context.getClickLocation();
        BlockPos blockPos = context.getClickedPos();
        double relX = clickPos.x - blockPos.getX();
        double relZ = clickPos.z - blockPos.getZ();

        boolean east = relX >= 0.5;
        boolean south = relZ >= 0.5;

        return (south ? 2 : 0) + (east ? 1 : 0);
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
        boolean slab = state.getValue(ON_SLAB);
        boolean between = state.getValue(BETWEEN);
        int betweenCorner = state.getValue(BETWEEN_CORNER);

        if (between && slab) return SHAPES_BETWEEN_SLAB[betweenCorner];
        if (between) return SHAPES_BETWEEN[betweenCorner];
        if (slab) return SHAPE_SLAB;
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConsoleBlockEntity(pos, state);
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
        if (be instanceof ConsoleBlockEntity console) {
            if (player.isShiftKeyDown()) {
                console.cycleTextureVariant();
            } else {
                console.cycleModelVariant();
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConsoleBlockEntity console) {
            console.cycleAnimation();
        }

        return InteractionResult.CONSUME;
    }
}
