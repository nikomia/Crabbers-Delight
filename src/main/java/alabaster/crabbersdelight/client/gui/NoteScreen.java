package alabaster.crabbersdelight.client.gui;

import alabaster.crabbersdelight.client.network.NoteClientPackets;
import net.minecraft.world.InteractionHand;

/**
 * Editing a note held in hand.
 */
public class NoteScreen extends AbstractNoteEditScreen {
    private final InteractionHand hand;

    public NoteScreen(InteractionHand hand, String initialText) {
        super(initialText);
        this.hand = hand;
    }

    @Override
    protected void saveText(String text) {
        NoteClientPackets.sendEdit(hand, text);
    }

    @Override
    protected void sign(String title, String text) {
        NoteClientPackets.sendSign(hand, title, text);
    }
}
