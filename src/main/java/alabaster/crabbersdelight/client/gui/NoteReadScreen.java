package alabaster.crabbersdelight.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class NoteReadScreen extends Screen {
    private static final int LINE_HEIGHT = 9;

    private final String title;
    private final String text;
    private final String author;
    private final boolean showMetadata;
    private List<FormattedCharSequence> wrappedLines = List.of();
    private int imageLeft;

    public NoteReadScreen(String title, String text, String author, boolean showMetadata) {
        super(Component.translatable("gui.crabbersdelight.note.read"));
        this.title = title;
        this.text = text;
        this.author = author;
        this.showMetadata = showMetadata;
    }

    @Override
    protected void init() {
        imageLeft = (width - AbstractNoteEditScreen.IMAGE_WIDTH) / 2;
        wrappedLines = font.split(Component.literal(text), AbstractNoteEditScreen.TEXT_WIDTH);

        int buttonY = AbstractNoteEditScreen.IMAGE_TOP + AbstractNoteEditScreen.IMAGE_HEIGHT + 2;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(width / 2 - 100, buttonY, 200, 20).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, AbstractNoteEditScreen.BOOK_BACKGROUND, imageLeft, AbstractNoteEditScreen.IMAGE_TOP,
                0.0F, 0.0F, AbstractNoteEditScreen.IMAGE_WIDTH, AbstractNoteEditScreen.IMAGE_HEIGHT, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        int textWidth = AbstractNoteEditScreen.TEXT_WIDTH;
        int left = imageLeft + 36;
        int top = AbstractNoteEditScreen.IMAGE_TOP + 30;

        if (showMetadata && title != null && !title.isBlank()) {
            Component titleComponent = Component.literal(title);
            graphics.text(font, titleComponent, left + (textWidth - font.width(titleComponent)) / 2, 18, NotePageWidget.TEXT_COLOR, false);
        }

        for (int i = 0; i < wrappedLines.size(); i++) {
            graphics.text(font, wrappedLines.get(i), left, top + i * LINE_HEIGHT, NotePageWidget.TEXT_COLOR, false);
        }

        if (showMetadata && author != null && !author.isBlank()) {
            graphics.text(font, "- " + author, left, top + wrappedLines.size() * LINE_HEIGHT + 6, NotePageWidget.TEXT_COLOR, false);
        }
    }
}
