package alabaster.crabbersdelight.common.world;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Notes thrown into water by players; they are fished back out of message bottles.
 */
public class BottledNoteSavedData extends SavedData {
    private static final int MAX_QUEUED_NOTES = 500;

    public record Entry(String title, String text, String author, int generation, ItemStack reward) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.optionalFieldOf("Title", "").forGetter(Entry::title),
                Codec.STRING.optionalFieldOf("Text", "").forGetter(Entry::text),
                Codec.STRING.optionalFieldOf("Author", "").forGetter(Entry::author),
                Codec.INT.optionalFieldOf("Generation", 0).forGetter(Entry::generation),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("Reward", ItemStack.EMPTY).forGetter(Entry::reward)
        ).apply(i, Entry::new));
    }

    public static final Codec<BottledNoteSavedData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Entry.CODEC.listOf().optionalFieldOf("Notes", List.of()).forGetter(BottledNoteSavedData::asList)
    ).apply(i, BottledNoteSavedData::new));

    public static final SavedDataType<BottledNoteSavedData> TYPE = new SavedDataType<>(
            CrabbersDelight.modPrefix("bottled_notes"), BottledNoteSavedData::new, CODEC);

    private final Deque<Entry> queue = new ArrayDeque<>();

    public BottledNoteSavedData() {
    }

    private BottledNoteSavedData(List<Entry> entries) {
        queue.addAll(entries);
    }

    public static BottledNoteSavedData get(ServerLevel level) {
        return level.getServer().getDataStorage().computeIfAbsent(TYPE);
    }

    public void addNote(String title, String text, String author, int generation, ItemStack reward) {
        queue.addLast(new Entry(title, text, author, generation, reward));
        while (queue.size() > MAX_QUEUED_NOTES) {
            queue.pollFirst();
        }
        setDirty();
    }

    public List<Entry> asList() {
        return new ArrayList<>(queue);
    }

    public void remove(Entry entry) {
        if (!Config.THROWN_NOTES_PERSIST.get()) {
            queue.remove(entry);
            setDirty();
        }
    }
}
