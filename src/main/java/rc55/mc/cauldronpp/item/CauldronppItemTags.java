package rc55.mc.cauldronpp.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import rc55.mc.cauldronpp.Cauldronpp;

public class CauldronppItemTags {

    public static final TagKey<Item> CAULDRON_BREWING_MATERIALS = register("cauldron_brewing_materials");
    public static final TagKey<Item> POTION_MATERIALS = register("cauldron_brewing_materials/potion");
    public static final TagKey<Item> POTION_TYPE_MATERIALS = register("cauldron_brewing_materials/potion_type");
    public static final TagKey<Item> SPLASH_POTIONS = registerC("potions/splash");
    public static final TagKey<Item> LINGERING_POTIONS = registerC("potions/lingering");
    public static final TagKey<Item> TIPPED_ARROWS = registerC("tipped_arrows");

    private static TagKey<Item> register(String id) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Cauldronpp.MODID, id));
    }
    private static TagKey<Item> registerC(String id) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", id));
    }
}
