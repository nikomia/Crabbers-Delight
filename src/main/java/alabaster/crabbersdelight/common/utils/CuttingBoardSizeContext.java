package alabaster.crabbersdelight.common.utils;

import alabaster.crabbersdelight.common.item.component.FishSize;
import org.jspecify.annotations.Nullable;

/**
 * Fish size of the item being cut, valid while a cutting board processes it (server thread only).
 */
public final class CuttingBoardSizeContext {
    private static @Nullable FishSize pendingSize = null;

    private CuttingBoardSizeContext() {
    }

    public static void set(@Nullable FishSize size) {
        pendingSize = size;
    }

    public static @Nullable FishSize get() {
        return pendingSize;
    }
}
