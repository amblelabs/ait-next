package dev.amble.ait.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class PoliceBoxBlock extends BaseEntityBlock {

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
    public static final IntegerProperty TEXTURE = IntegerProperty.create("texture", 0, 4);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 7);
    public static final MapCodec<PoliceBoxBlock> CODEC = simpleCodec(PoliceBoxBlock::new);

    public PoliceBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(TEXTURE, 0)
                .setValue(LIGHT, 7));
    }

    public static Properties defaultProps() {
        return Properties.of()
                .mapColor(MapColor.COLOR_BLUE)
                .strength(50f, 1200f)
                .noOcclusion()
                .lightLevel(state -> state.getValue(LIGHT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, TEXTURE, LIGHT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = Mth.floor((context.getRotation()) / 45.0f + 0.5f) & 7;
        return this.defaultBlockState().setValue(ROTATION, rotation);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PoliceBoxBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PoliceBoxBlockEntity policeBox) {
            policeBox.interact(player.isShiftKeyDown());
        }

        return InteractionResult.CONSUME;
    }
}
