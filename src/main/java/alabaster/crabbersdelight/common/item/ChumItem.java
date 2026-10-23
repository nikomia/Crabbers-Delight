package alabaster.crabbersdelight.common.item;

import net.minecraft.world.item.Item;

public class ChumItem extends Item {
    public ChumItem(Properties properties) {
        super(properties);
    }

    public static Properties properties() {
        return new Properties().durability(48);
    }
}
