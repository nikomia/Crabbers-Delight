package alabaster.crabbersdelight.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Minimal multi-line text area drawn on the note paper.
 */
public class NotePageWidget extends AbstractWidget {
    private static final int LINE_HEIGHT = 9;
    public static final int TEXT_COLOR = 0xFF000000;

    private final Font font;
    private final int maxLength;
    private final boolean centered;
    private String text;

    public NotePageWidget(Font font, int x, int y, int width, int height, String initialText, int maxLength) {
        this(font, x, y, width, height, initialText, maxLength, false);
    }

    public NotePageWidget(Font font, int x, int y, int width, int height, String initialText, int maxLength, boolean centered) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.maxLength = maxLength;
        this.text = initialText;
        this.centered = centered;
    }

    public String getValue() {
        return text;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        List<FormattedCharSequence> lines = font.split(Component.literal(text), getWidth());
        int y = getY();
        int lastLineX = centered ? getX() + getWidth() / 2 : getX();
        int lastLineWidth = 0;

        for (FormattedCharSequence line : lines) {
            int lineWidth = font.width(line);
            int x = centered ? getX() + (getWidth() - lineWidth) / 2 : getX();
            graphics.text(font, line, x, y, TEXT_COLOR, false);
            lastLineX = x;
            lastLineWidth = lineWidth;
            y += LINE_HEIGHT;
        }

        if (isFocused() && System.currentTimeMillis() / 300 % 2 == 0) {
            int cursorY = getY() + Math.max(0, lines.size() - 1) * LINE_HEIGHT;
            graphics.text(font, "_", lastLineX + lastLineWidth, cursorY, TEXT_COLOR, false);
        }
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!isFocused()) {
            return false;
        }
        if (event.isAllowedChatCharacter()) {
            String typed = event.codepointAsString();
            if (text.length() + typed.length() <= maxLength) {
                text = text + typed;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused()) {
            return false;
        }
        if (event.key() == InputConstants.KEY_BACKSPACE) {
            if (!text.isEmpty()) {
                text = text.substring(0, text.offsetByCodePoints(text.length(), -1));
            }
            return true;
        }
        if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER) {
            if (text.length() < maxLength) {
                text = text + "\n";
            }
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("gui.crabbersdelight.note.edit"));
    }
}
