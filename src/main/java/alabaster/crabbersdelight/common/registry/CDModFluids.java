package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.fluid.CDFluidType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Fluids without blocks or buckets; they exist for compat (Hearth and Harvest fluid bottles, Create recipes).
 */
public class CDModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, CrabbersDelight.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, CrabbersDelight.MODID);

    public record FluidEntry(Supplier<FluidType> type, Supplier<CDFluidType> source, Supplier<CDFluidType> flowing) {}

    @SuppressWarnings("unchecked")
    private static FluidEntry registerFluid(String name, Supplier<? extends Item> bottleItem) {
        Supplier<FluidType> type = FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create().viscosity(2000).density(1400)));
        Supplier<CDFluidType>[] holders = new Supplier[2];
        holders[0] = FLUIDS.register(name, () ->
                new CDFluidType(new BaseFlowingFluid.Properties(type, holders[0], holders[1]), true, bottleItem));
        holders[1] = FLUIDS.register("flowing_" + name, () ->
                new CDFluidType.Flowing(new BaseFlowingFluid.Properties(type, holders[0], holders[1]), bottleItem));
        return new FluidEntry(type, holders[0], holders[1]);
    }

    public static final FluidEntry SEA_PICKLE_JUICE = registerFluid("sea_pickle_juice", CDModItems.SEA_PICKLE_JUICE);
    public static final FluidEntry COCONUT_MILK = registerFluid("coconut_milk", CDModItems.COCONUT_MILK);
}
