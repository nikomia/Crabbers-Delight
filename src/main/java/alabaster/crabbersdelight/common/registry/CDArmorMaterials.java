package alabaster.crabbersdelight.common.registry;

import alabaster.crabbersdelight.CrabbersDelight;
import alabaster.crabbersdelight.common.tags.CDModTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

public class CDArmorMaterials {
    public static final ResourceKey<EquipmentAsset> PEARL_NECKLACE_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, CrabbersDelight.modPrefix("pearl_necklace"));

    // Base durability 8: chestplates multiply it by 16, matching the original 128 uses
    public static final ArmorMaterial PEARL_NECKLACE = new ArmorMaterial(
            8,
            new EnumMap<>(Map.of(ArmorType.BOOTS, 0, ArmorType.LEGGINGS, 0, ArmorType.CHESTPLATE, 0, ArmorType.HELMET, 0, ArmorType.BODY, 0)),
            25,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            0.0F,
            0.0F,
            CDModTags.REPAIRS_PEARL_NECKLACE,
            PEARL_NECKLACE_ASSET
    );
}
