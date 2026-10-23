package alabaster.crabbersdelight.common.event;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.block.entity.inventory.TackleBoxItemHandler;
import alabaster.crabbersdelight.common.utils.TackleBoxProximity;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.List;

@EventBusSubscriber(modid = CrabbersDelight.MODID)
public class FishingBaitEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        TackleBoxProximity.TackleBoxAccess tackleBox = TackleBoxProximity.find(player);
        if (tackleBox == null) {
            return;
        }

        ItemStack lure = tackleBox.getSlot(TackleBoxItemHandler.LURE_SLOT);
        if (!lure.isEmpty() && player instanceof ServerPlayer serverPlayer) {
            grantMasterBaiter(serverPlayer);
        }

        int baitSlot = TackleBoxItemHandler.BAIT_SLOT_1;
        ItemStack bait = tackleBox.getSlot(baitSlot);
        if (bait.isEmpty()) {
            baitSlot = TackleBoxItemHandler.BAIT_SLOT_2;
            bait = tackleBox.getSlot(baitSlot);
        }
        if (bait.isEmpty()) {
            return;
        }

        Identifier baitId = BuiltInRegistries.ITEM.getKey(bait.getItem());
        Identifier tableId = CrabbersDelight.modPrefix("gameplay/fishing_bait_loot/" + baitId.getPath());

        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, tableId));
        if (table == LootTable.EMPTY) {
            return;
        }

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.FISHING);

        List<ItemStack> rolled = table.getRandomItems(params);
        event.getDrops().clear();
        event.getDrops().addAll(rolled);

        if (player instanceof ServerPlayer serverPlayer) {
            grantMasterBaiter(serverPlayer);
        }

        ItemStack shrunk = bait.copy();
        shrunk.shrink(1);
        tackleBox.setSlot(baitSlot, shrunk);
    }

    private static void grantMasterBaiter(ServerPlayer serverPlayer) {
        Identifier advancementId = Identifier.fromNamespaceAndPath(CrabbersDelight.MODID, "main/master_baiter");
        AdvancementHolder masterBaiter = serverPlayer.level().getServer().getAdvancements().get(advancementId);
        if (masterBaiter != null) {
            serverPlayer.getAdvancements().award(masterBaiter, "bait_catch");
        }
    }
}