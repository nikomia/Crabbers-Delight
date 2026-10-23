package alabaster.crabbersdelight.client.gui;

import alabaster.crabbersdelight.client.network.NoteClientPackets;
import net.minecraft.core.BlockPos;

/**
 * Editing a note pinned to a wall or lying on the floor.
 */
public class NoteBlockEditScreen extends AbstractNoteEditScreen {
    private final BlockPos pos;

    public NoteBlockEditScreen(BlockPos pos, String initialText) {
        super(initialText);
        this.pos = pos;
    }

    @Override
    protected void saveText(String text) {
        NoteClientPackets.sendBlockEdit(pos, text);
    }

    @Override
    protected void sign(String title, String text) {
        NoteClientPackets.sendBlockSign(pos, title, text);
    }
}
