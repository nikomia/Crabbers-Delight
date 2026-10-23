package alabaster.crabbersdelight.common.block.entity;

import alabaster.crabbersdelight.common.item.component.NoteContent;
import alabaster.crabbersdelight.common.item.component.SignedNoteContent;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import alabaster.crabbersdelight.common.registry.CDModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class NoteBlockEntity extends BlockEntity {
    private String text = "";
    private String author = "";
    private String title = "";
    private int generation = 0;
    private boolean signed = false;

    public NoteBlockEntity(BlockPos pos, BlockState state) {
        super(CDModBlockEntity.NOTE.get(), pos, state);
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setUnsignedText(String text) {
        if (signed) {
            return;
        }
        this.text = text;
        setChanged();
    }

    public void signDirectly(String title, String text, String author, int generation) {
        this.text = text;
        this.title = title;
        this.author = author;
        this.generation = generation;
        this.signed = true;
        setChanged();
    }

    // The block has no loot table: the note itself is returned when the block goes away
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            ItemStack drop = signed ? signedNoteStack() : unsignedNoteStack();
            if (drop != null) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
        }
        super.preRemoveSideEffects(pos, state);
    }

    private @Nullable ItemStack unsignedNoteStack() {
        if (text.isEmpty()) {
            return null;
        }
        ItemStack stack = new ItemStack(CDModItems.NOTE.get());
        stack.set(CDModDataComponents.NOTE_CONTENT.get(), new NoteContent(text));
        return stack;
    }

    private ItemStack signedNoteStack() {
        ItemStack stack = new ItemStack(CDModItems.SIGNED_NOTE.get());
        stack.set(CDModDataComponents.SIGNED_NOTE_CONTENT.get(), new SignedNoteContent(title, text, author, generation));
        return stack;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("Text", text);
        output.putString("Author", author);
        output.putString("Title", title);
        output.putInt("Generation", generation);
        output.putBoolean("Signed", signed);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        text = input.getStringOr("Text", "");
        author = input.getStringOr("Author", "");
        title = input.getStringOr("Title", "");
        generation = input.getIntOr("Generation", 0);
        signed = input.getBooleanOr("Signed", false);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
