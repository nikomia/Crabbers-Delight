package alabaster.crabbersdelight.client.event;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.client.gui.CrabTrapGUI;
import alabaster.crabbersdelight.client.renderer.*;
import alabaster.crabbersdelight.common.entity.boat.CDBoatModelLayers;
import alabaster.crabbersdelight.common.entity.crab.CrabModel;
import alabaster.crabbersdelight.common.entity.crab.CrabRenderer;
import alabaster.crabbersdelight.common.registry.CDModBlockEntity;
import alabaster.crabbersdelight.common.registry.CDModEntities;
import alabaster.crabbersdelight.common.registry.CDModFluids;
import alabaster.crabbersdelight.common.registry.CDModItems;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = CrabbersDelight.MODID, value = Dist.CLIENT)
public class CDClientEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(alabaster.crabbersdelight.common.registry.CDModMenus.CRAB_TRAP_MENU.get(), CrabTrapGUI::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CrabModel.LAYER_LOCATION, CrabModel::createBodyLayer);
        event.registerLayerDefinition(CDBoatModelLayers.PALM_BOAT_LAYER, BoatModel::createBoatModel);
        event.registerLayerDefinition(CDBoatModelLayers.PALM_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CDModEntities.CRAB.get(), CrabRenderer::new);
        event.registerEntityRenderer(CDModEntities.PALM_BOAT.get(), context -> new BoatRenderer(context, CDBoatModelLayers.PALM_BOAT_LAYER));
        event.registerEntityRenderer(CDModEntities.PALM_CHEST_BOAT.get(), context -> new BoatRenderer(context, CDBoatModelLayers.PALM_CHEST_BOAT_LAYER));
        event.registerEntityRenderer(CDModEntities.LIFE_PRESERVER_SEAT.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(CDModBlockEntity.CRAB_TRAP.get(), CrabTrapRenderer::new);
    }

    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            LivingEntityRenderer renderer = event.getPlayerRenderer(skin);
            if (renderer != null) {
                renderer.addLayer(new LifePreserverLayer<>(renderer));
            }
        }
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?, ?> renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new LifePreserverLayer<>(livingRenderer));
            }
        }
    }

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {}, (entity, state) -> {
            ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
            if (legs.is(CDModItems.LIFE_PRESERVER.get())) {
                ItemStackRenderState item = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().updateForLiving(item, legs, ItemDisplayContext.FIXED, entity);
                state.setRenderData(LifePreserverLayer.WORN_LIFE_PRESERVER, item);
            } else {
                state.setRenderData(LifePreserverLayer.WORN_LIFE_PRESERVER, null);
            }
        });
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        registerFluid(event, "sea_pickle_juice", CDModFluids.SEA_PICKLE_JUICE);
        registerFluid(event, "coconut_milk", CDModFluids.COCONUT_MILK);
    }

    private static void registerFluid(RegisterFluidModelsEvent event, String name, CDModFluids.FluidEntry fluid) {
        event.register(new FluidModel.Unbaked(
                new Material(CrabbersDelight.modPrefix("block/" + name + "_still")),
                new Material(CrabbersDelight.modPrefix("block/" + name + "_flow")),
                null,
                null), fluid.source(), fluid.flowing());
    }
}
