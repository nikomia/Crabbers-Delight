package alabaster.crabbersdelight.common.item;

import alabaster.crabbersdelight.client.gui.NoteClientHandler;
import alabaster.crabbersdelight.common.block.NoteBlock;
import alabaster.crabbersdelight.common.block.entity.NoteBlockEntity;
import alabaster.crabbersdelight.common.item.component.SignedNoteContent;
import alabaster.crabbersdelight.common.registry.CDModBlocks;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class SignedNoteItem extends BlockItem {
    public SignedNoteItem(Properties properties) {
        super(CDModBlocks.NOTE_BLOCK.get(), properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        SignedNoteContent content = stack.get(CDModDataComponents.SIGNED_NOTE_CONTENT.get());
        if (content != null && !content.title().isBlank()) {
            return Component.literal(content.title());
        }
        return super.getName(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        SignedNoteContent content = context.getItemInHand().get(CDModDataComponents.SIGNED_NOTE_CONTENT.get());

        if (!NoteItem.canAttachToWall(context) && !NoteItem.canAttachFlat(context)) {
            if (level.isClientSide() && content != null) {
                NoteClientHandler.openReadScreen(content.title(), content.text(), content.author());
            }
            return InteractionResult.SUCCESS;
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        InteractionResult result = place(placeContext);
        if (result.consumesAction() && !level.isClientSide() && content != null) {
            BlockPos placedPos = placeContext.getClickedPos();
            if (level.getBlockEntity(placedPos) instanceof NoteBlockEntity note) {
                note.signDirectly(content.title(), content.text(), content.author(), content.generation());
                BlockState signedState = level.getBlockState(placedPos).setValue(NoteBlock.SIGNED, true);
                level.setBlock(placedPos, signedState, Block.UPDATE_ALL);
                level.sendBlockUpdated(placedPos, signedState, signedState, Block.UPDATE_ALL);
            }
        }
        return result;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            SignedNoteContent content = player.getItemInHand(hand).get(CDModDataComponents.SIGNED_NOTE_CONTENT.get());
            if (content != null) {
                NoteClientHandler.openReadScreen(content.title(), content.text(), content.author());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        SignedNoteContent content = stack.get(CDModDataComponents.SIGNED_NOTE_CONTENT.get());
        if (content == null) {
            return;
        }
        tooltip.accept(Component.translatable("item.crabbersdelight.signed_note.author", content.author()).withStyle(ChatFormatting.GRAY));
        String generationKey = switch (content.generation()) {
            case SignedNoteContent.GENERATION_ORIGINAL -> "original";
            case SignedNoteContent.GENERATION_COPY -> "copy";
            case SignedNoteContent.GENERATION_COPY_OF_COPY -> "copy_of_copy";
            case SignedNoteContent.GENERATION_TATTERED -> "tattered";
            default -> null;
        };
        if (generationKey != null) {
            tooltip.accept(Component.translatable("gui.crabbersdelight.note.generation." + generationKey).withStyle(ChatFormatting.GRAY));
        }
    }
}
