package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.item.component.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CDModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CrabbersDelight.MODID);

    public static final int MAX_TITLE_LENGTH = 16;




    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CrabTrapContents>> CRAB_TRAP_CONTENTS =
            DATA_COMPONENTS.register("crab_trap_contents", () -> DataComponentType.<CrabTrapContents>builder()
                    .persistent(CrabTrapContents.CODEC)
                    .networkSynchronized(CrabTrapContents.STREAM_CODEC)
                    .build());


}