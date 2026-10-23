package alabaster.crabbersdelight.common.entity.boat;

import alabaster.crabbersdelight.CrabbersDelight;
import net.minecraft.client.model.geom.ModelLayerLocation;

/**
 * Layer ids double as texture paths: textures/entity/boat/palm.png and textures/entity/chest_boat/palm.png.
 */
public class CDBoatModelLayers {
    public static final ModelLayerLocation PALM_BOAT_LAYER = new ModelLayerLocation(CrabbersDelight.modPrefix("boat/palm"), "main");
    public static final ModelLayerLocation PALM_CHEST_BOAT_LAYER = new ModelLayerLocation(CrabbersDelight.modPrefix("chest_boat/palm"), "main");
}
