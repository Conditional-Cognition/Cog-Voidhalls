package com.cogworks.voidhalls.items;

import com.cogworks.voidhalls.blocks.ModTableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TablespoonItem extends TieredItem {

    public TablespoonItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public int getBurnTime(@NotNull ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        if (this.getTier() == net.minecraft.world.item.Tiers.WOOD) {
            return 200;
        }
        return super.getBurnTime(itemStack, recipeType);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.hasProperty(ModTableBlock.HORIZONTAL_AXIS)) {
            if (!level.isClientSide) {
                Direction.Axis currentAxis = state.getValue(ModTableBlock.HORIZONTAL_AXIS);
                Direction.Axis newAxis = (currentAxis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
                level.setBlock(pos, state.setValue(ModTableBlock.HORIZONTAL_AXIS, newAxis), 3);
                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.2F);

                ItemStack spoon = context.getItemInHand();
                if (context.getPlayer() != null) {
                    spoon.hurtAndBreak(1, context.getPlayer(), EquipmentSlot.MAINHAND);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}