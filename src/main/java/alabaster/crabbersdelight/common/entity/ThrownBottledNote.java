package alabaster.crabbersdelight.common.entity;

import alabaster.crabbersdelight.common.item.component.SignedNoteContent;
import alabaster.crabbersdelight.common.registry.CDModDataComponents;
import alabaster.crabbersdelight.common.registry.CDModEntities;
import alabaster.crabbersdelight.common.registry.CDModItems;
import alabaster.crabbersdelight.common.world.BottledNoteSavedData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;

/**
 * A bottled note thrown by a player: landing in water stores it for other players to fish up.
 */
public class ThrownBottledNote extends ThrowableItemProjectile {
    private String title = "";
    private String text = "";
    private String author = "";
    private int generation = 0;
    private ItemStack reward = ItemStack.EMPTY;
    private boolean resolved = false;

    public ThrownBottledNote(EntityType<? extends ThrownBottledNote> type, Level level) {
        super(type, level);
    }

    public ThrownBottledNote(Level level, LivingEntity thrower, ItemStack stack, String title, String text, String author, int generation) {
        super(CDModEntities.THROWN_BOTTLED_NOTE.get(), thrower, level, stack);
        this.title = title;
        this.text = text;
        this.author = author;
        this.generation = generation;
    }

    public void setReward(ItemStack reward) {
        this.reward = reward;
    }

    @Override
    protected Item getDefaultItem() {
        return CDModItems.BOTTLED_NOTE.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (!resolved && level() instanceof ServerLevel serverLevel && isInWater()) {
            resolved = true;
            landInWater(serverLevel);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (resolved || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        resolved = true;

        if (isInWater()) {
            landInWater(serverLevel);
        } else {
            ItemStack fallback = new ItemStack(CDModItems.SIGNED_NOTE.get());
            fallback.set(CDModDataComponents.SIGNED_NOTE_CONTENT.get(), new SignedNoteContent(title, text, author, generation));
            serverLevel.addFreshEntity(new ItemEntity(serverLevel, getX(), getY(), getZ(), fallback));
            if (!reward.isEmpty()) {
                serverLevel.addFreshEntity(new ItemEntity(serverLevel, getX(), getY(), getZ(), reward.copy()));
            }
            discard();
        }
    }

    private void landInWater(ServerLevel serverLevel) {
        BottledNoteSavedData.get(serverLevel).addNote(title, text, author, generation, reward.copy());
        serverLevel.sendParticles(ParticleTypes.SPLASH, getX(), getY(), getZ(), 8, 0.2, 0.1, 0.2, 0.05);
        discard();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("NoteTitle", title);
        output.putString("NoteText", text);
        output.putString("NoteAuthor", author);
        output.putInt("NoteGeneration", generation);
        if (!reward.isEmpty()) {
            output.store("NoteReward", ItemStack.CODEC, reward);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        title = input.getStringOr("NoteTitle", "");
        text = input.getStringOr("NoteText", "");
        author = input.getStringOr("NoteAuthor", "");
        generation = input.getIntOr("NoteGeneration", 0);
        reward = input.read("NoteReward", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
