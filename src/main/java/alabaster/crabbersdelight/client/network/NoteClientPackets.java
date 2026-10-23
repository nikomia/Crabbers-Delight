package alabaster.crabbersdelight.client.network;

import alabaster.crabbersdelight.common.network.NoteNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class NoteClientPackets {
    private NoteClientPackets() {
    }

    public static void sendEdit(InteractionHand hand, String text) {
        ClientPacketDistributor.sendToServer(new NoteNetworking.NoteEditPayload(hand == InteractionHand.MAIN_HAND, text));
    }

    public static void sendSign(InteractionHand hand, String title, String text) {
        ClientPacketDistributor.sendToServer(new NoteNetworking.NoteSignPayload(hand == InteractionHand.MAIN_HAND, title, text));
    }

    public static void sendBlockEdit(BlockPos pos, String text) {
        ClientPacketDistributor.sendToServer(new NoteNetworking.BlockNoteEditPayload(pos, text));
    }

    public static void sendBlockSign(BlockPos pos, String title, String text) {
        ClientPacketDistributor.sendToServer(new NoteNetworking.BlockNoteSignPayload(pos, title, text));
    }
}
