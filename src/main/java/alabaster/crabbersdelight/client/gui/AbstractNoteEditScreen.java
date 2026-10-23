package alabaster.crabbersdelight.client.gui;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Note editing with a separate signing page, shared by the held note and the placed note.
 */
public abstract class AbstractNoteEditScreen extends Screen {
    static final Identifier BOOK_BACKGROUND = CrabbersDelight.modPrefix("textures/gui/note.png");
    static final int IMAGE_WIDTH = 192;
    static final int IMAGE_HEIGHT = 192;
    static final int IMAGE_TOP = 2;
    static final int TEXT_WIDTH = 114;
    private static final int TEXT_HEIGHT = 128;

    private final String initialText;
    private NotePageWidget textBox;
    private NotePageWidget titleBox;
    private Button doneButton;
    private Button signButton;
    private Button cancelButton;
    private Button finalizeButton;
    private boolean isSigning;
    private int imageLeft;
    private @Nullable NotePageWidget pendingFocus;

    protected AbstractNoteEditScreen(String initialText) {
        super(Component.translatable("gui.crabbersdelight.note.edit"));
        this.initialText = initialText;
    }

    protected abstract void saveText(String text);

    protected abstract void sign(String title, String text);

    @Override
    protected void init() {
        imageLeft = (width - IMAGE_WIDTH) / 2;
        int left = imageLeft + 36;

        textBox = addRenderableWidget(new NotePageWidget(font, left, IMAGE_TOP + 30, TEXT_WIDTH, TEXT_HEIGHT, initialText, CDModDataComponents.MAX_NOTE_LENGTH));
        titleBox = addRenderableWidget(new NotePageWidget(font, left, 50, TEXT_WIDTH, 9, "", CDModDataComponents.MAX_TITLE_LENGTH, true));

        int buttonY = IMAGE_TOP + IMAGE_HEIGHT + 2;
        doneButton = addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
            saveText(textBox.getValue());
            onClose();
        }).bounds(width / 2 - 100, buttonY, 98, 20).build());
        signButton = addRenderableWidget(Button.builder(Component.translatable("gui.crabbersdelight.note.sign"), b -> setSigning(true))
                .bounds(width / 2 + 2, buttonY, 98, 20).build());
        cancelButton = addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> setSigning(false))
                .bounds(width / 2 - 100, buttonY, 98, 20).build());
        finalizeButton = addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), b -> {
            sign(titleBox.getValue(), textBox.getValue());
            onClose();
        }).bounds(width / 2 + 2, buttonY, 98, 20).build());

        updateVisibility();
        setInitialFocus(textBox);
    }

    private void setSigning(boolean signing) {
        isSigning = signing;
        updateVisibility();
        pendingFocus = signing ? titleBox : textBox;
    }

    private void updateVisibility() {
        textBox.visible = !isSigning;
        doneButton.visible = !isSigning;
        signButton.visible = !isSigning;
        titleBox.visible = isSigning;
        cancelButton.visible = isSigning;
        finalizeButton.visible = isSigning;
        finalizeButton.active = !titleBox.getValue().isBlank();
    }

    @Override
    public void tick() {
        super.tick();
        if (isSigning) {
            finalizeButton.active = !titleBox.getValue().isBlank();
        }
        if (pendingFocus != null) {
            setInitialFocus(pendingFocus);
            pendingFocus = null;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_BACKGROUND, imageLeft, IMAGE_TOP, 0.0F, 0.0F, IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (isSigning) {
            int left = imageLeft + 36;

            Component label = Component.translatable("gui.crabbersdelight.note.editTitle");
            graphics.text(font, label, left + (TEXT_WIDTH - font.width(label)) / 2, 34, NotePageWidget.TEXT_COLOR, false);

            Component ownerText = Component.translatable("book.byAuthor", minecraft.player.getName());
            graphics.text(font, ownerText, left + (TEXT_WIDTH - font.width(ownerText)) / 2, 60, NotePageWidget.TEXT_COLOR, false);

            graphics.textWithWordWrap(font, Component.translatable("gui.crabbersdelight.note.finalizeWarning"), left, 82, TEXT_WIDTH, NotePageWidget.TEXT_COLOR, false);
        }
    }
}
